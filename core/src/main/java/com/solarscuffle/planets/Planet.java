package com.solarscuffle.planets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.solarscuffle.Main;

public class Planet {

    public Vector3 position;
    public ModelInstance model;
    public PlanetType type = PlanetType.BASIC;
    public int units;
    public double progress;
    public Decal progessBar;

    public Planet(Vector3 pos) {
        this.position = pos;

        model = new ModelInstance(Main.sphere);
        model.transform.setToScaling(type.size,type.size,type.size);
        model.calculateTransforms();

        progessBar = Decal.newDecal(new TextureRegion(Main.square));
        progessBar.lookAt(Vector3.Z, Vector3.Y);
    }

    public void draw(ModelBatch modelBatch, DecalBatch decalBatch, Environment environment) {
        modelBatch.render(model, environment);

    }

    public void tick(double deltaTime) {
        progress += deltaTime;
        if (progress > 2d) {
            units += (int) Math.floor(progress);
            progress -= 2d;
        }
    }

}
