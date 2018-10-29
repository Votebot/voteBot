package me.schlaubi.votebot.commands.settings;

import me.schlaubi.votebot.core.command.Command;
import me.schlaubi.votebot.core.command.CommandCategory;
import me.schlaubi.votebot.core.command.CommandEvent;
import me.schlaubi.votebot.core.command.Result;
import me.schlaubi.votebot.core.command.permission.Permissions;
import me.schlaubi.votebot.core.entities.Guild;

public class PrefixCommand extends Command {

    public PrefixCommand() {
        super(new String[]{"prefix", "setprefix"}, CommandCategory.SETTINGS, Permissions.everyone(), "Lets you set the guild-specific prefix", "[prefix]");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        final Guild databaseGuild = event.getDatabaseGuild();

        if (args.length == 0) {
            return send(info(event.translate("command.prefix.title"), String.format(event.translate("command.prefix.description"), databaseGuild.getPrefix())));
        }

        databaseGuild.setPrefix(args[0]);

        return send(success(event.translate("command.prefix.set.title"), String.format(event.translate("command.prefix.set.description"), args[0])));
    }
}
