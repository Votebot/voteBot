package me.schlaubi.votebot.core.events;

import me.schlaubi.votebot.core.command.Command;
import me.schlaubi.votebot.core.command.CommandEvent;

public class CommandPermissionViolationEvent extends CommandExecutedEvent {


    public CommandPermissionViolationEvent(CommandEvent event, Command command) {
        super(event, command);
    }
}
