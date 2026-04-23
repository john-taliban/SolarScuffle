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
    private Vector3 hitbox;
    private ModelInstance model;
    private ModelInstance ring;
    private double progress;
    private Decal progressBar;
    private Decal unit;
    private Vector3 barPos;
    private int id;
    private boolean selected = false;

    private static int tally;

    public Planet(Vector3 pos, Team team, PlanetType type) {
        this.position = new Vector3(pos);
        this.team = team;
        this.type = type;

        model = new ModelInstance(Main.sphere);
        model.transform.set(position, new Quaternion(), new Vector3(type.size,type.size,type.size));
        model.getMaterial("main").set(ColorAttribute.createDiffuse(team.colour));
        model.calculateTransforms();
        ring = new ModelInstance(type.ring);
        ring.transform.setTranslation(position);
        ring.calculateTransforms();
        progressBar = Decal.newDecal(8f,1.5f,new TextureRegion(Main.square));
        progressBar.lookAt(Vector3.Z, Vector3.Y);
        barPos = pos.add(0,type.radius * 1.2f + 2f,0);
        progressBar.setPosition(barPos);
        progressBar.setColor(team.colour);
        unit = Decal.newDecal(2f,2f,new TextureRegion(Main.square));
        unit.setColor(team.unit);
        id = tally++;
        hitbox = new Vector3(0,0,0).add(position);
    }

    public Planet(Vector3 pos, Team team) {
        this(pos,team,PlanetType.BASIC);
    }

    public void draw(ModelBatch modelBatch, DecalBatch decalBatch, Environment environment) {
        modelBatch.render(model, environment);
        progressBar.setWidth(4 * (float)progress);
        progressBar.setX(barPos.x + (2 * (float)progress) - 4);
        decalBatch.add(progressBar);
        Vector3 unitPos = new Vector3(position);
        Vector3 unitOffset = new Vector3(Vector3.X);
        Vector3 unitId = new Vector3();
        unitOffset.scl(type.radius + 4f);
        Random random = new Random(id);
        for (int i = 0; i < Math.pow(units,0.3 + 0.014 * type.size) ; i++) {
            unitId.y = random.nextFloat(-0.5f,0.5f) * type.radius;
            unitOffset.rotate(Vector3.Y,37 + random.nextFloat(-4,4));
            unit.setPosition(new Vector3(unitOffset).rotate(Vector3.Y,Main.gameTime * 40).add(unitPos).add(unitId));
            decalBatch.add(unit);
            decalBatch.flush();
        }
        int length = 1 + (units < 10 ? 0 : (int) Math.log10(units));
        int j = units;
        float letterWidth = 3f;
        Vector3 p = new Vector3(position).add((length-1) * letterWidth / 2,type.radius * 1.3f + 6,0);
        for (int i = 0; i < length; i++) {
            Decal num = Main.numbers[j % 10];
            j/=10;
            num.setColor(team.colour);
            num.setPosition(p);
            p.add(-letterWidth,0,0);
            decalBatch.add(num);
            decalBatch.flush();
        }

        if (selected) {
            modelBatch.render(ring);
        }
    }

    public void tick(double deltaTime) {
        progress += deltaTime * 2 / type.rate;
        if (progress > 2d) {
            units += (int) Math.floor(progress) * type.rate * type.rate * 2;
            progress -= 2d;
        }
    }

    public boolean getCollision(Ray ray) {
        return Intersector.intersectRaySphere(ray,hitbox,type.radius,null);
    }

    public void toggleSelected() {
        if (Main.team == team) {
            selected = !selected;
            model.getMaterial("main").set(selected ? ColorAttribute.createDiffuse(team.highlight) : ColorAttribute.createDiffuse(team.colour));
        }
    }

}
