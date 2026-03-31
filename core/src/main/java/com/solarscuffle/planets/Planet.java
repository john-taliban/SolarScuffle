package com.solarscuffle.planets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.math.collision.Sphere;
import com.solarscuffle.Main;

import java.util.Random;

public class Planet {

    public Vector3 position;
    public PlanetType type = PlanetType.BASIC;
    public int units;
    public Team team;
    private Sphere hitbox;
    private ModelInstance model;
    private double progress;
    private Decal progressBar;
    private Decal unit;
    private Vector3 barPos;
    private int id;

    private static int tally;

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
        progressBar.setColor(team.colour);
        unit = Decal.newDecal(2f,2f,new TextureRegion(Main.square));
        unit.setColor(team.colour);
        id = tally++;
        hitbox = new Sphere(position, type.size);
    }

    public void draw(ModelBatch modelBatch, DecalBatch decalBatch, Environment environment) {
        modelBatch.render(model, environment);
        progressBar.setWidth(4 * (float)progress);
        progressBar.setX(barPos.x + (2 * (float)progress) - 4);
        decalBatch.add(progressBar);
        Vector3 unitPos = new Vector3(position);
        Vector3 unitOffset = new Vector3(Vector3.X);
        Vector3 unitId = new Vector3();
        unitPos.mulAdd(Vector3.Y,-type.size);
        unitOffset.scl(type.size);
        Random random = new Random(id);
        for (int i = 0; i < Math.pow(units,0.5) ; i++) {
            unitId.y = random.nextFloat(-1,1) * type.size/2;
            unitOffset.rotate(Vector3.Y,37 + random.nextFloat(-4,4));
            unit.setPosition(new Vector3(unitOffset).rotate(Vector3.Y,Main.gameTime * 40).add(unitPos).add(unitId));
            decalBatch.add(unit);
            decalBatch.flush();
        }
    }

    public void tick(double deltaTime) {
        progress += deltaTime * 5;
        if (progress > 2d) {
            units += (int) Math.floor(progress);
            progress -= 2d;
        }
    }

    public boolean getCollision(Ray ray) {
        return Intersector.intersectRaySphere(ray,position,type.size,null);
    }

    public void toggleSelected() {

    }

}
