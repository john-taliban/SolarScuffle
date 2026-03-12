package com.solarscuffle;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.solarscuffle.planets.Planet;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter implements InputProcessor {
    public Environment environment;
    public ModelBatch modelBatch;
    public PerspectiveCamera camera;
    public ModelInstance instance;

    public static Model sphere;
    public static Texture square;

    public float zoom = 1.0f;
    public Vector3 cameraPosition = new Vector3();

    public Planet[] planets = new Planet[1];

    @Override
    public void create() {
        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight,0.2f,0.2f,0.2f,1f));
        environment.add(new DirectionalLight().set(0.6f,0.6f,0.6f,0f,0f,-1f));


        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0f, 0f, 45f);
        camera.lookAt(0,0,0);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();

        ModelBuilder builder = new ModelBuilder();
        sphere = builder.createSphere(1f,1f,1f,32,32, new Material(ColorAttribute.createDiffuse(Color.CYAN)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        instance = new ModelInstance(sphere);

        square = new Texture("libgdx.png");

        planets[0] = new Planet(Vector3.Zero);
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
        modelBatch.end();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        sphere.dispose();
        square.dispose();
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
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        cameraPosition.set(screenX,screenY,0);
        return true;
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
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        camera.position.add((screenX - cameraPosition.x) * -0.05f,(screenY - cameraPosition.y) * 0.05f,0);
        cameraPosition.set(screenX,screenY,0);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        float fallOff = 20;
        float scale = 200;
        zoom = Math.max(0.0f,zoom + amountY);
        zoom = Math.min(zoom,fallOff * 2);
        System.out.println(zoom);
        float a = zoom / (zoom + fallOff);
        camera.position.z = (20) + scale * a * a * a;
        return true;
    }
}
