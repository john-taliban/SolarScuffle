package com.solarscuffle.planets;

import com.badlogic.gdx.graphics.Color;

public enum Team {

    NEUTRAL(Color.LIGHT_GRAY),
    RED(Color.RED),
    YELLOW(Color.YELLOW),
    GREEN(Color.GREEN),
    BLUE(Color.BLUE);

    public Color colour;

    Team(Color colour) {
        this.colour =  colour;
    }

}
