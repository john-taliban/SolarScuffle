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
import com.solarscuffle.UnitCloud;

import java.util.EnumMap;
import java.util.Random;

import static com.solarscuffle.Main.*;

public class Planet {

    public Vector3 position;
    public PlanetType type = PlanetType.BASIC;
    private Vector3 hitbox;

    public Team team;
    private int[] units = new int[Team.values().length];
    private int[] attackValues = new int[Team.values().length];
    private boolean occupied;

    private ModelInstance model;
    private ModelInstance ring;
    private ModelInstance band;
    private double progress;
    private Decal progressBar;
    private Vector3 barPos;

    private int id;
    private boolean selected = false;

    private static int tally;

    public Planet(Vector3 pos, Team team, PlanetType type) {
        this.position = new Vector3(pos);
        this.team = team;
        this.type = type;

        units[team.ordinal()] = type.startingUnits;

        model = new ModelInstance(Main.sphere);
        model.transform.set(position, new Quaternion(), new Vector3(type.size,type.size,type.size));
        model.getMaterial("main").set(ColorAttribute.createDiffuse(team.colour));
        model.calculateTransforms();
        band = new ModelInstance(Main.band);

        ring = new ModelInstance(type.ring);
        ring.transform.setTranslation(position);
        ring.calculateTransforms();

        progressBar = Decal.newDecal(8f,1.5f,new TextureRegion(Main.square));
        progressBar.lookAt(Vector3.Z, Vector3.Y);
        barPos = pos.add(0,type.radius * 1.2f + 2f,0);
        progressBar.setPosition(barPos);
        progressBar.setColor(team.colour);

        id = tally++;
        hitbox = new Vector3(0,0,0).add(position);
    }

    public Planet(Vector3 pos, Team team) {
        this(pos,team,PlanetType.BASIC);
    }

    public void draw(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(model, environment);
        if (selected) {
            modelBatch.render(ring,environment);

            Vector3 to;
            Vector3 from = new Vector3(position);
            float distance;
            float c = padding/2f + thickness + type.radius;
            float d = 0;

            if (Main.target != null) {
                to = new Vector3(target.position.x,target.position.y, 0);
                d = padding/2f + thickness + target.type.radius;
                distance = to.dst(from) - c - d;
            }
            else {
                to = new Vector3(Main.mouseX, Main.mouseY, 0);
                distance = to.dst(from) - c;
            }
            Vector3 diff = new Vector3(to).sub(from);
            diff.nor();
            float angle = (float) (Math.atan2(diff.y,diff.x) * 180 / Math.PI);
            to.mulAdd(diff,c);
            if (target != null) {
                from.mulAdd(diff,-d);
            }
            to.scl(0.5f);
            band.transform.set(to.mulAdd(from,0.5f),new Quaternion(Vector3.Z,angle + 90), new Vector3(1,distance/thickness,1));
            modelBatch.render(band,environment);
        }
    }

    public void renderTargeted(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(ring,environment);
    }

    public void drawDecals(DecalBatch decalBatch) {
        progressBar.setWidth(4 * (float)progress);
        progressBar.setX(barPos.x + (2 * (float)progress) - 4);
        decalBatch.add(progressBar);
        int textLayers = 2;
        Random random = new Random(id);
        for (Team t : Team.values()) {
            int ordinal = t.ordinal();
            int count = units[ordinal];
            if (count == 0) {
                continue;
            }
            Decal unit = Main.unitDecals[ordinal];
            Vector3 unitPos = new Vector3(position);
            Vector3 unitOffset = new Vector3(Vector3.X);
            Vector3 unitId = new Vector3();
            unitOffset.scl(type.radius + 4f);
            for (int i = 0; i < Math.pow(count,0.3 + 0.014 * type.size) ; i++) {
                unitId.y = random.nextFloat(-0.5f,0.5f) * type.radius;
                unitOffset.rotate(Vector3.Y,37 + random.nextFloat(-4,4));
                unit.setPosition(new Vector3(unitOffset).rotate(Vector3.Y,Main.gameTime * 40).add(unitPos).add(unitId));
                decalBatch.add(unit);
                decalBatch.flush();
            }

            int length = 1 + (count < 10 ? 0 : (int) Math.log10(count));
            int j = count;
            float letterWidth = 3f;
            Vector3 p = new Vector3(position).add((length-1) * letterWidth / 2,type.radius * 1.3f + (t == team ? 6 : textLayers++ * 6),0);
            for (int i = 0; i < length; i++) {
                Decal num = Main.numbers[j % 10];
                j/=10;
                // for now im fine doing this bc there are so few letter draw calls
                num.setColor(t.colour);
                num.setPosition(p);
                p.add(-letterWidth,0,0);
                decalBatch.add(num);
                decalBatch.flush();
            }
        }
    }

    public void tick(double deltaTime) {
        if (occupied) {
            progress += deltaTime;
            if (progress > 2d) {
                progress -= 2d;
                boolean occupiers = false;
                int enemies = 0;
                int singleOrdinal = 0;
                for (Team t : Team.values()) {
                    if (t == team) { continue; }
                    int ordinal = t.ordinal();
                    units[ordinal] = Math.max(0, units[ordinal] - attackValues[team.ordinal()]);
                    if (units[ordinal] == 0) {
                        if (attackValues[ordinal] > 0) {
                            units[team.ordinal()] = Math.max(0, units[team.ordinal()] - attackValues[ordinal]);
                        }
                        attackValues[ordinal] = 0;
                    }
                    if (attackValues[ordinal] == 0) {
                        continue;
                    }
                    units[team.ordinal()] = Math.max(0, units[team.ordinal()] - attackValues[ordinal]);
                    occupiers = true;
                    enemies++;
                    singleOrdinal = ordinal;
                }
                occupied = occupiers;
                if (units[team.ordinal()] <= 0 && occupied) {
                    if (enemies == 1) {

                        setTeam(Team.values()[singleOrdinal]);
                    }
                    else {
                        setTeam(Team.NEUTRAL);
                    }
                }

            }
        }
        else {
            progress += deltaTime * 0 / type.rate;
            if (progress > 2d) {
                units[team.ordinal()] += (int) Math.floor(progress) * type.rate * type.rate * 2;
                progress -= 2d;
            }
        }
    }

    public boolean getCollision(Ray ray) {
        return Intersector.intersectRaySphere(ray,hitbox,type.radius,null);
    }

    public void toggleSelected() {
        if (Main.team == team) {
            selected = !selected;
            if (selected) {selectedList.add(this);} else {selectedList.remove(this);}
            model.getMaterial("main").set(selected ? ColorAttribute.createDiffuse(team.highlight) : ColorAttribute.createDiffuse(team.colour));
        }
    }

    public UnitCloud send(Planet target, int percent) {
        int sending = (int)(units[team.ordinal()] * percent / 100f);
        units[team.ordinal()] -= sending;
        return new UnitCloud(this,target,sending);
    }

    public void attack(int units, Team team) {
        this.units[team.ordinal()] += units;
        occupy();
    }

    public int getCount() {
        return units[team.ordinal()];
    }

    public void setTeam(Team newTeam) {
        team = newTeam;
        model.getMaterial("main").set(ColorAttribute.createDiffuse(team.colour));
        selected = false;
        progressBar.setColor(team.colour);
        if (newTeam != Team.NEUTRAL) {
            occupied = false;
        }
    }

    private static final float damageMultiplier = .2f;

    public void occupy() {
        occupied = true;
        int defence = units[team.ordinal()];
        int dOrdinal = team.ordinal();
        for (Team t : Team.values()) {
            if (t == team) continue;
            int ordinal = t.ordinal();
            float attackValue = units[ordinal];
            if (attackValue == 0) continue;
            double ratio = defence / attackValue;
            ratio = Math.pow(ratio, 1.6);
            if (ratio > 1) {
                attackValues[ordinal] = (int) ((1 - (1/ratio)) * defence * damageMultiplier);
                attackValues[dOrdinal] += (int) (((1/ratio)) * attackValue * damageMultiplier);
            }
            else {
                attackValues[ordinal] = (int) ((1 - ratio) * defence * damageMultiplier);
                attackValues[dOrdinal] += (int) ((ratio) * attackValue * damageMultiplier);
            }
        }
    }
}
