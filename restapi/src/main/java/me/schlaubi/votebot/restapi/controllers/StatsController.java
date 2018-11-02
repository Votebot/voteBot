package me.schlaubi.votebot.restapi.controllers;

import me.schlaubi.votebot.restapi.Launcher;
import me.schlaubi.votebot.restapi.entities.EntityProvider;
import me.schlaubi.votebot.restapi.entities.Statistics;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsController {

    @RequestMapping("/stats")
    public Statistics getStats() {
        EntityProvider provider = Launcher.getProvider();
        return new Statistics(provider.getGuildCount(), provider.getUserCount(), provider.getVoteCount());
    }
}
