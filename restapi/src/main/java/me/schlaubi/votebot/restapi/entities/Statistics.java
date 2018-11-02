package me.schlaubi.votebot.restapi.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Statistics {

    private Integer guilds;
    private Integer users;
    private Integer votes;
}
