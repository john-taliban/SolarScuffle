package com.solarscuffle.planets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.solarscuffle.Main;

public class Planet {

    public Vector3 position;
    public PlanetType type = PlanetType.BASIC;
    public int units;
    public Team team;
    private ModelInstance model;
    private double progress;
    private Decal progressBar;
    private Vector3 barPos;

    public Planet(Vector3 pos, Team team) {
        this.position = pos;
        this.team = team;

        model = new ModelInstance(Main.sphere);
        model.transform.set(position, new Quaternion(), new Vector3(type.size,type.size,type.size));
        model.getMaterial("main").set(ColorAttribute.createDiffuse(team.colour));
        model.calculateTransforms();
        progressBar = Decal.newDecal(8f,1.5f,new TextureRegion(Main.square));
        progressBar.lookAt(Vector3.Z, Vector3.Y);
        barPos = pos.add(0,type.size/2 + 4,0);
        progressBar.setPosition(barPos);
    }

    public void draw(ModelBatch modelBatch, DecalBatch decalBatch, Environment environment) {
        modelBatch.render(model, environment);
        progressBar.setWidth(4 * (float)progress);
        progressBar.setX(barPos.x + (2 * (float)progress) - 4);
        decalBatch.add(progressBar);

    }

    public void tick(double deltaTime) {
        progress += deltaTime;
        if (progress > 2d) {
            units += (int) Math.floor(progress);
            progress -= 2d;
        }
    }

}
