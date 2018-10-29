package me.schlaubi.votebot.core.command;

import lombok.Getter;
import lombok.Setter;
import me.schlaubi.votebot.core.command.permission.Permissions;
import me.schlaubi.votebot.util.FormatUtil;

public abstract class SubCommand extends Command {

    @Getter @Setter
    private Command mainCommand;

    public SubCommand(String[] aliases, Permissions permissions, String description, String usage) {
        super(aliases, null, permissions, description, usage);
    }

    @Override
    protected Result sendHelp() {
        return send(FormatUtil.formatCommand(mainCommand));
    }
}
