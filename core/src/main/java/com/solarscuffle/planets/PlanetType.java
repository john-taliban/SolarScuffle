package com.solarscuffle.planets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.solarscuffle.Main;

import static com.solarscuffle.Main.padding;
import static com.solarscuffle.Main.thickness;

public enum PlanetType {

    BASIC(10,1,100),
    LARGE(20,2,50);

    public final float size;
    public final float radius;
    public final int rate;
    public Model ring;
    public final int startingUnits;
    private PlanetType(float size, int rate, int startingUnits) {
        this.size = size;
        this.radius = size/2;
        this.rate = rate;
        this.startingUnits = startingUnits;
    }

    private PlanetType(float size, int rate) {
        this(size,rate,0);
    }


    public void generateRing() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        Material material = new Material("main", ColorAttribute.createDiffuse(new Color(Main.UI_COLOUR)));
        material.set(new IntAttribute(IntAttribute.CullFace,0));
        MeshPartBuilder partBuilder = modelBuilder.part("ring", GL20.GL_TRIANGLE_STRIP, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,material);
        float circumfrence = (thickness /2 + padding + radius) * 2 * 3.14159265358979323f;
        int ticks = (int) (circumfrence / 2f);
        short count = -1;
        double radians = 6.28318530718d / ticks;
        for (int i = 0; i <= ticks; i++) {
            float x = (float) Math.sin(i * radians);
            float y = (float) Math.cos(i * radians);
            partBuilder.vertex(new MeshPartBuilder.VertexInfo().setPos(x * (radius + padding),y * (radius + padding),0).setNor(0,0,1));
            partBuilder.index(count++);
            partBuilder.vertex(new MeshPartBuilder.VertexInfo().setPos(x * (radius + padding + thickness),y * (radius + padding + thickness),0).setNor(0,0,1));
            partBuilder.index(count++);
        }
        // add extra vertex bc this is a triangle strip
        float x = (float) Math.sin(radians);
        float y = (float) Math.cos(radians);
        partBuilder.vertex(new MeshPartBuilder.VertexInfo().setPos(x * (radius + padding),y * (radius + padding),0).setNor(0,0,-1));
        partBuilder.index(count++);
        ring = modelBuilder.end();
    }
}
