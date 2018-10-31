package me.schlaubi.votebot.core.graphics;

import lombok.Getter;

@Getter
public class PieTile {

    private final String tile;
    private final double percentage;

    public PieTile(String tile, double percentage) {
        this.percentage = percentage;
        var name =  tile.length() >= 10 ? tile.substring(0, 10) + "..." : tile;
        this.tile = name + " - " + percentage + "%";
    }
}
