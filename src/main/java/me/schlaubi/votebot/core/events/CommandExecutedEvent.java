package me.schlaubi.votebot.core.events;

import me.schlaubi.votebot.core.command.Command;
import me.schlaubi.votebot.core.command.CommandEvent;

public class CommandExecutedEvent extends CommandEvent {

    public CommandExecutedEvent(CommandEvent event, Command command) {
        super(event, command, event.getBot(), event.getArgs(), event.getInvocation());
    }
}
