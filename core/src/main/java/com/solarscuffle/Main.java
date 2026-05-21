package com.solarscuffle;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter implements InputProcessor {
    // Rendering Eniroments
    private Environment environment;
    private ModelBatch modelBatch;
    private DecalBatch decalBatch;
    private SpriteBatch spriteBatch;

    // Rendering Objects
    public static Model sphere;
    public static Model band;
    public static Texture square;
    public static Decal[] numbers = new Decal[10];

    // UI Objects
    private Sprite sliderBar;

    // Camera Variables
    private PerspectiveCamera camera;
    private float zoom = 80.0f;;
    private final Plane backPlane = new Plane(new Vector3(0, 0, 1), 0);
    private final Vector3 intersection = new Vector3();
    private final Vector3 lastDragPos = new Vector3();

    // Static Values
    public static final int UI_COLOUR = 0xA8C8C8FF;
    public static final float thickness = 1;
    public static final float padding = 2;
    public static float gameTime;
    public static float mouseX = 0f;
    public static float mouseY = 0f;

    // Gameplay Variables
    private List<Planet> planets = new ArrayList<>();
    private List<UnitCloud> unitClouds = new ArrayList<>();
    public static List<Planet> selectedList = new ArrayList<>();
    public static Planet target = null;
    private int percent = 100;
    public static Team team = Team.RED;

    @Override
    public void create() {

        modelBatch = new ModelBatch();
        spriteBatch = new SpriteBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight,0.2f,0.2f,0.2f,1f));
        environment.add(new DirectionalLight().set(0.6f,0.6f,0.6f,0f,0f,-1f));
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight,1f,1f,1f,1f));

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        scrolled(0,0); // set camera to the correctly zoomed pos
        camera.lookAt(0,0,0);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();

        decalBatch = new DecalBatch(new CameraGroupStrategy(camera));

        ModelBuilder builder = new ModelBuilder();
        sphere = builder.createSphere(1f,1f,1f,32,32, new Material("main",ColorAttribute.createDiffuse(Color.CYAN)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        band = builder.createRect(-thickness/2f,-thickness/2f,0f,
                thickness/2f,-thickness/2f,0f,
                thickness/2f,thickness/2f,0f,
                -thickness/2f,thickness/2f,0f,
                0f,0f,1f,new Material("main",ColorAttribute.createDiffuse(new Color(UI_COLOUR))), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        square = new Texture("red.png");
        sliderBar = new Sprite(square);
        sliderBar.setScale(Gdx.graphics.getWidth() * 0.6f,3);
        sliderBar.setPosition(Gdx.graphics.getWidth() / 2, 20);
        for (int i = 0; i < 10; i++) {
            numbers[i] = Decal.newDecal(3f,4f,new TextureRegion(new Texture(i+".png")));
        }

        for (PlanetType type : PlanetType.values()) {
            type.generateRing();
        }

        //planets.add(new Planet(Vector3.Zero, Team.NEUTRAL, PlanetType.LARGE));
        planets.add(new Planet(new Vector3(50,50,0),Team.RED));
        planets.add(new Planet(new Vector3(0,0,0),Team.RED,PlanetType.LARGE));
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
        Stack<Integer> toRemove = new Stack<>();
        for (int i = 0; i < unitClouds.size(); i++) {
            if (unitClouds.get(i).tick((float) deltaTime)) {
                toRemove.add(i);
            }
        }
        for (int remove : toRemove) {
            unitClouds.remove(remove);
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
        if (target != null) {
            target.renderTargeted(modelBatch,environment);
        }
        modelBatch.end();
        for (Planet planet : planets) {
            planet.drawDecals(decalBatch);
        }
        for (UnitCloud cloud : unitClouds) {
            cloud.draw(decalBatch, (float) deltaTime);
        }
        decalBatch.flush();
        spriteBatch.begin();
        sliderBar.draw(spriteBatch);
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        spriteBatch.dispose();
        decalBatch.dispose();
        band.dispose();
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
        if (keycode == Input.Keys.PLUS) {
            percent = Math.min(100,percent + 5);
        }
        else if (keycode == Input.Keys.MINUS) {
            percent = Math.max(0,percent - 5);
        }
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
        Ray cameraRay = camera.getPickRay(screenX, screenY);
        if (target != null) {
            for (Planet planet : selectedList) {
                unitClouds.add(planet.send(target,percent));
            }
        }
        else {
            // Just find where we clicked initially
            for ( Planet planet : planets) {
                if (planet.getCollision(cameraRay)) {
                    planet.toggleSelected();
                    break;
                }
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
        Vector3 i = new Vector3();
        if (Intersector.intersectRayPlane(camera.getPickRay(screenX, screenY), backPlane, i)) {
            mouseX = i.x;
            mouseY = i.y;
        }
        if (!selectedList.isEmpty()) {
            Ray cameraRay = camera.getPickRay(screenX, screenY);
            boolean foundTarget = false;
            for ( Planet planet : planets) {
                if (planet.getCollision(cameraRay)) {
                    if (planet.team != team) {
                        target = planet;
                        foundTarget = true;
                        break;
                    }
                }
            }
            if (!foundTarget) {
                target = null;
            }
        }
        return true;
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
