package me.schlaubi.votebot.core.events;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.ReadyEvent;

public class AllShardsLoadedEvent extends ReadyEvent {
    public AllShardsLoadedEvent(JDA api, long responseNumber) {
        super(api, responseNumber);
    }
}
