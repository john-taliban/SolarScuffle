package com.solarscuffle;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.solarscuffle.planets.Planet;
import com.solarscuffle.planets.PlanetType;
import com.solarscuffle.planets.Team;

import java.util.ArrayList;
import java.util.List;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter implements InputProcessor {
    // Rendering Eniroments
    public Environment environment;
    public ModelBatch modelBatch;
    public DecalBatch decalBatch;

    // Rendering Onjects
    public static Model sphere;
    public static Texture square;
    public static Decal[] numbers = new Decal[10];

    // Camera Variables
    public PerspectiveCamera camera;
    public float zoom = 10.0f;
    private final Plane backPlane = new Plane(new Vector3(0, 0, 1), 0);
    private final Vector3 intersection = new Vector3();
    private final Vector3 lastDragPos = new Vector3();

    // Static Values
    public static float gameTime;

    // Gameplay Variables
    public List<Planet> planets = new ArrayList<>();
    public static Team team = Team.RED;

    private ModelInstance test;

    @Override
    public void create() {

        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight,0.2f,0.2f,0.2f,1f));
        environment.add(new DirectionalLight().set(0.6f,0.6f,0.6f,0f,0f,-1f));

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        scrolled(0,0); // set camera to the correctly zoomed pos
        camera.lookAt(0,0,0);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();

        decalBatch = new DecalBatch(new CameraGroupStrategy(camera));

        ModelBuilder builder = new ModelBuilder();
        sphere = builder.createSphere(1f,1f,1f,32,32, new Material("main",ColorAttribute.createDiffuse(Color.CYAN)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        square = new Texture("red.png");
        for (int i = 0; i < 10; i++) {
            numbers[i] = Decal.newDecal(3f,4f,new TextureRegion(new Texture(i+".png")));
        }

        for (PlanetType type : PlanetType.values()) {
            type.generateRing();
        }
        float[]
        for (VertexAttribute obj : PlanetType.BASIC.ring.meshes.get(0).getVertices()) {
            System.out.println(obj.);
        }
        test = new ModelInstance(PlanetType.LARGE.ring);

        //planets.add(new Planet(Vector3.Zero, Team.NEUTRAL, PlanetType.LARGE));
        planets.add(new Planet(new Vector3(50,50,0),Team.RED));
        planets.add(new Planet(new Vector3(50,-50,0),Team.BLUE));
        planets.add(new Planet(new Vector3(-50,50,0),Team.GREEN));
        planets.add(new Planet(new Vector3(-50,-50,0),Team.YELLOW));
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportHeight = height;
        camera.viewportWidth = width;
    }

    @Override
    public void render() {
        double deltaTime = Gdx.graphics.getDeltaTime();
        gameTime += (float) deltaTime;
        input(deltaTime);
        logic(deltaTime);
        draw(deltaTime);
    }


    private void input(double deltaTime) {

    }

    private void logic(double deltaTime) {
        for (Planet planet : planets) {
            planet.tick(deltaTime);
        }
    }

    private void draw(double deltaTime) {
        camera.update();
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(camera);
        for (Planet planet : planets) {
            planet.draw(modelBatch,environment);
        }
        modelBatch.render(test);
        modelBatch.end();
        for (Planet planet : planets) {
            planet.drawDecals(decalBatch);
        }
        decalBatch.flush();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        decalBatch.dispose();
        sphere.dispose();
        square.dispose();
        for (PlanetType type : PlanetType.values()) {
            type.ring.dispose();
        }
        for (Decal number : numbers) {
            number.getTextureRegion().getTexture().dispose();
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Just find where we clicked initially
        Ray cameraRay = camera.getPickRay(screenX, screenY);
        for ( Planet planet : planets) {
            if (planet.getCollision(cameraRay)) {
                planet.toggleSelected();
            }
        }
        Intersector.intersectRayPlane(cameraRay, backPlane, lastDragPos);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // 1. Find where the finger is in the world RIGHT NOW (relative to current camera)
        if (Intersector.intersectRayPlane(camera.getPickRay(screenX, screenY), backPlane, intersection)) {

            // 2. Calculate the delta (Current - Last)
            // We use .tmp for the calculation to avoid creating new Vector3 objects
            float deltaX = intersection.x - lastDragPos.x;
            float deltaY = intersection.y - lastDragPos.y;

            // 3. Move the camera in the OPPOSITE direction of the drag
            camera.position.sub(deltaX, deltaY, 0);
            camera.update(); // Update immediately so the next intersection calculation is accurate

            // 4. Re-calculate the world position AFTER the camera moved
            // to keep the anchor point under the finger
            Intersector.intersectRayPlane(camera.getPickRay(screenX, screenY), backPlane, lastDragPos);

            return true;
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        // higher values mean less falloff
        float fallOff = 20;
        // how much is the camera moved per unit of zoom
        float scale = 200;
        // clamp zoom values
        zoom = Math.max(10.0f,zoom + amountY*2);
        zoom = Math.min(zoom,fallOff * 4);
        // turn linear zoom value into cubic rational
        float a = zoom / (zoom + fallOff);
        camera.position.z = (20) + scale * a * a * a;
        // return true to acknowledge the processing of the input
        return true;
    }
}
