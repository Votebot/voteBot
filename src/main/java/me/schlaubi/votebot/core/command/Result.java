package me.schlaubi.votebot.core.command;

import me.schlaubi.votebot.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

public class Result {

    private final Message message;

    public void sendMessage(TextChannel channel, int deleteAfter) {
        SafeMessage.sendMessage(channel, message, deleteAfter);
    }

    public Result(Message message) {
        this.message = message;
    }

    public Result(MessageBuilder builder) {
        this(builder.build());
    }

    public Result(String content) {
        this(new MessageBuilder().setContent(content));
    }

    public Result(MessageEmbed embed) {
        this(new MessageBuilder().setEmbed(embed));
    }

    public Result(EmbedBuilder embedBuilder) {
        this(embedBuilder.build());
    }
}
