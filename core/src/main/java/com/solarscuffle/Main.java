package com.solarscuffle;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.solarscuffle.planets.Planet;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private ModelBatch modelBatch;
    private PerspectiveCamera camera;

    public Model sphere;
    public ModelInstance mi;

    @Override
    public void create() {
        modelBatch = new ModelBatch();

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        camera.position.set(10,10,10);
        camera.near = 1f;
        camera.far = 10f;
        camera.lookAt(0,0,0);
        camera.update();

        ModelBuilder builder = new ModelBuilder();
        sphere = builder.createBox(5,5,5,new Material(ColorAttribute.createDiffuse(Color.GREEN)), VertexAttributes.Usage.Normal | VertexAttributes.Usage.Position);
        mi = new ModelInstance(sphere);
    }

    @Override
    public void resize(int width, int height) {
        //viewport.update(width, height,true);
    }

    @Override
    public void render() {
        //input();
        System.out.println("testing git");
        //logic();
        draw();
    }

    private void input() {

    }

    private void logic() {
    }

    private void draw() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(camera);
        modelBatch.render(mi);
        modelBatch.end();
    }


    @Override
    public void dispose() {
        modelBatch.dispose();
        sphere.dispose();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }
}
