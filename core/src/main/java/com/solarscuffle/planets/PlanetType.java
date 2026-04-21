package com.solarscuffle.planets;

import java.awt.Color;

public enum PlanetType {

    BASIC(10,1),
    LARGE(20,2);

    public final float size;
    public final float radius;
    public final int rate;
    private PlanetType(float size, int rate) {
        this.size = size;
        this.radius = size/2;
        this.rate = rate;
    }
}
