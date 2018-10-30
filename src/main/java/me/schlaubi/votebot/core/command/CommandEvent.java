package me.schlaubi.votebot.core.command;

import lombok.Getter;
import me.schlaubi.votebot.VoteBot;
import me.schlaubi.votebot.core.command.permission.UserPermissions;
import me.schlaubi.votebot.core.entities.Guild;
import me.schlaubi.votebot.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

@Getter
public class CommandEvent extends GuildMessageReceivedEvent {

    private final Command command;
    private final VoteBot bot;
    private final String[] args;
    private final String invocation;
    private final Guild databaseGuild;
    private final User databaseUser;

    protected CommandEvent(GuildMessageReceivedEvent event, Command command, VoteBot bot, String[] args, String invocation) {
        super(event.getJDA(), event.getResponseNumber(), event.getMessage());
        this.command = command;
        this.bot = bot;
        this.args = args;
        this.invocation = invocation;
        this.databaseGuild = bot.getGuildCache().get(guild);
        this.databaseUser = bot.getUserCache().get(getAuthor());
    }

    public String getArguments() {
        return String.join(" ", args);
    }

    public String translate(String key) {
        return bot.getTranslationManager().getLocale(databaseUser).translate(key);
    }

    public UserPermissions getUserPermissions() {
        return databaseUser.getPermissions();
    }
}
