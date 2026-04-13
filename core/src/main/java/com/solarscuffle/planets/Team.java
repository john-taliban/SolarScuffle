package com.solarscuffle.planets;

import com.badlogic.gdx.graphics.Color;

public enum Team {

    NEUTRAL(new Color(0x6f6f6fff),Color.LIGHT_GRAY),
    RED(new Color(0.65f,0f,0f,1f),Color.RED),
    YELLOW(new Color(0.65f,0.65f,0f,1f),Color.YELLOW),
    GREEN(Color.FOREST,Color.GREEN),
    BLUE(new Color(0f,0.45f,0.65f,1f),new Color(0f,0.65f,1f,1f));

    public Color colour;
    public Color highlight;
    public Color unit;

    Team(Color colour, Color highlight) {
        this.colour =  colour;
        this.highlight = highlight;
        unit = new Color(colour).lerp(highlight,0.2f);
    }

}
