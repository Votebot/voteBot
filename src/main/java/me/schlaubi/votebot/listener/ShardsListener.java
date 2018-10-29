package me.schlaubi.votebot.listener;

import me.schlaubi.votebot.VoteBot;
import me.schlaubi.votebot.core.events.AllShardsLoadedEvent;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class ShardsListener {

    private int tempLoadedShards = 0;

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onReady(ReadyEvent event) {
        tempLoadedShards++;
        ShardManager shardManager = VoteBot.getInstance().getShardManager();
        if (tempLoadedShards == shardManager.getShardsTotal())
            VoteBot.getInstance().getEventManager().handle(new AllShardsLoadedEvent(event.getJDA(), 200));
    }
}
