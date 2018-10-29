package me.schlaubi.votebot.listener;

import me.schlaubi.votebot.VoteBot;
import me.schlaubi.votebot.util.EmbedUtil;
import me.schlaubi.votebot.util.SafeMessage;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class SelfMentionListener {

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onSelfMention(GuildMessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equals(event.getGuild().getSelfMember().getAsMention()))
            SafeMessage.sendMessage(event.getChannel(), EmbedUtil.info(":v: Hey, I'm VoteBot", String.format("My **prefix** on this **guild** is **`%s`**.", VoteBot.getInstance().getGuildCache().get(event.getGuild()).getPrefix())), 30);
    }
}
