package com.solarscuffle.planets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.solarscuffle.Main;

public class Planet {

    public Vector3 position;
    public ModelInstance model;
    public PlanetType type = PlanetType.BASIC;

    public Planet(Vector3 pos) {
        this.position = pos;
        model = new ModelInstance(Main.sphere);
        model.transform.setToScaling(type.size,type.size,type.size);

        model.calculateTransforms();
    }

    public void draw(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(model, environment);
    }

}
