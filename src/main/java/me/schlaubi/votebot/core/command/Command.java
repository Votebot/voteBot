package me.schlaubi.votebot.core.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.schlaubi.votebot.core.command.permission.Permissions;
import me.schlaubi.votebot.util.EmbedUtil;
import me.schlaubi.votebot.util.FormatUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public abstract class Command extends EmbedUtil {

    private final String[] aliases;
    private final CommandCategory category;
    private final Permissions permissions;
    private final String description;
    private final String usage;
    private final Map<String, SubCommand> subCommandAssociations = new HashMap<>();

    public abstract Result run(String[] args, CommandEvent event);

    protected void registerSubCommand(SubCommand command) {
        command.setMainCommand(this);
        for (String alias : command.getAliases())
            subCommandAssociations.put(alias, command);
    }

    public String getName() {
        return aliases[0];
    }

    protected Result sendHelp() {
        return this.send(FormatUtil.formatCommand(this));
    }

    protected Result send(String content) {
        return new Result(content);
    }

    protected Result send(MessageEmbed embed) {
        return new Result(embed);
    }

    protected Result send(EmbedBuilder embedBuilder) {
        return new Result(embedBuilder);
    }

}
