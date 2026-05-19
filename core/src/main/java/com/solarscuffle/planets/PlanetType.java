package com.solarscuffle.planets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

public enum PlanetType {

    BASIC(10,1),
    LARGE(20,2);

    public final float size;
    public final float radius;
    public final int rate;
    public Model ring;
    private PlanetType(float size, int rate) {
        this.size = size;
        this.radius = size/2;
        this.rate = rate;
    }

    public void generateRing() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder partBuilder = modelBuilder.part("ring", GL20.GL_TRIANGLE_STRIP, VertexAttributes.Usage.Position,new Material("main", ColorAttribute.createDiffuse(Color.WHITE)));
        float circumfrence = (2 + radius) * 2 * 3.14159265358979323f;
        int ticks = 4;// (int) circumfrence;
        double radians = 6.28318530718d / ticks;
        for (int i = 0; i <= ticks; i++) {
            float x = (float) Math.sin(i * radians);
            //System.out.println(radius);
            float y = (float) Math.cos(i * radians);
            //System.out.println(y);
            partBuilder.vertex(new MeshPartBuilder.VertexInfo().setPos(x * (1 + 2),y * (1 + 2),0));
            partBuilder.vertex(new MeshPartBuilder.VertexInfo().setPos(x * (1 + 4),y * (1 + 4),0));
        }
        ring = modelBuilder.end();
    }
}
