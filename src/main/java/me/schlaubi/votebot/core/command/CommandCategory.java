package me.schlaubi.votebot.core.command;

import lombok.Getter;

@Getter
public enum CommandCategory {

    VOTE("Vote"),
    GENERAL("General"),
    SETTINGS("Settings");

    private final String displayName;

    CommandCategory(String displayName) {
        this.displayName = displayName;
    }
}
