package me.schlaubi.votebot.core.graphics;

import lombok.Getter;

@Getter
public class PieTile {

    private final String tile;
    private final double percentage;

    public PieTile(String tile, double percentage) {
        this.percentage = percentage;
        this.tile = tile + " - " + (percentage * 100) + "%";
    }
}
