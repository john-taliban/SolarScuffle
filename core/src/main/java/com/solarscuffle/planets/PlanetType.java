package com.solarscuffle.planets;

import java.awt.Color;

public enum PlanetType {

    BASIC(10,1);

    public float size;
    public float rate;
    private PlanetType(float size, float rate) {
        this.size = size;
        this.rate = rate;
    }
}
