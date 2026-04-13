package com.solarscuffle.planets;

import java.awt.Color;

public enum PlanetType {

    BASIC(10,1),
    LARGE(20,2);

    public float size;
    public int rate;
    private PlanetType(float size, int rate) {
        this.size = size;
        this.rate = rate;
    }
}
