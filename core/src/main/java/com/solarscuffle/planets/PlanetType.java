package com.solarscuffle.planets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
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
        MeshPartBuilder partBuilder = modelBuilder.part("ring", GL20.GL_TRIANGLE_STRIP, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,new Material("main", ColorAttribute.createEmissive(Color.WHITE)));
        float circumfrence = (2 + radius) * 2 * 3.14159265358979323f;
        int ticks = (int) circumfrence * 2;
        double radians = 360d / ticks / 180d * 3.14159265358979323d;
        for (int i = 0; i < ticks; i++) {
            float x = (float) Math.sin(i * radians);
            float y = (float) Math.cos(i * radians);
            partBuilder.vertex(new MeshPartBuilder.VertexInfo().setPos(x * (radius + 2),y * (radius + 2),0).setNor(0,0,1));
            partBuilder.vertex(new MeshPartBuilder.VertexInfo().setPos(x * (radius + 2),y * (radius + 2),0).setNor(0,0,1));
        }
        ring = modelBuilder.end();
    }
}
