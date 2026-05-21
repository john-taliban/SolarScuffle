package com.solarscuffle;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector3;
import com.solarscuffle.planets.Planet;
import com.solarscuffle.planets.Team;

import java.util.Random;

public class UnitCloud {

    private static int count;

    private Vector3 home;
    private Vector3 position;
    private int units;
    private float progess = 0;
    private float distance;
    private Team team;
    private Vector3 destination;
    private int id;
    private Decal unit;

    public UnitCloud(Planet from, Planet to, int units) {
        this.home = new Vector3(from.position);
        this.position = new Vector3(from.position);
        this.destination = new Vector3(to.position);
        this.units = units;
        this.team = from.team;
        this.distance = home.dst(destination);
        unit = Decal.newDecal(2f,2f,new TextureRegion(Main.square));
        unit.setColor(team.unit);
        id = count++;
    }

    public boolean tick(float deltaTime) {
        progess += deltaTime * 7;
        position = new Vector3(home).lerp(destination,progess/distance);
        return progess >= distance;
    }

    public void draw(DecalBatch decalBatch, float deltaTime) {

        Vector3 unitPos = new Vector3(position);
        Vector3 unitOffset = new Vector3(3,0,0);
        Vector3 unitId = new Vector3();
        Random random = new Random(id);
        for (int i = 0; i < Math.pow(units,0.4) ; i++) {
            unitId.y = random.nextFloat(-1.5f,1.5f);
            unitOffset.rotate(Vector3.Y,37 + random.nextFloat(-4,4));
            unit.setPosition(new Vector3(unitOffset).rotate(Vector3.Y,Main.gameTime * 40).add(unitPos).add(unitId));
            decalBatch.add(unit);
            decalBatch.flush();
        }
        int length = 1 + (units < 10 ? 0 : (int) Math.log10(units));
        int j = units;
        float letterWidth = 3f;
        Vector3 p = new Vector3(position).add((length-1) * letterWidth / 2, 6,0);
        for (int i = 0; i < length; i++) {
            Decal num = Main.numbers[j % 10];
            j/=10;
            num.setColor(team.colour);
            num.setPosition(p);
            p.add(-letterWidth,0,0);
            decalBatch.add(num);
            decalBatch.flush();
        }
    }
}
