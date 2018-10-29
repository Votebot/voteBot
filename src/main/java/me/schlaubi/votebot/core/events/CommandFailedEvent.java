package me.schlaubi.votebot.core.events;

import lombok.Getter;
import me.schlaubi.votebot.core.command.Command;
import me.schlaubi.votebot.core.command.CommandEvent;

@Getter
public class CommandFailedEvent extends CommandExecutedEvent {

    private final Throwable throwable;

    public CommandFailedEvent(CommandEvent event, Command command, Throwable throwable) {
        super(event, command);
        this.throwable = throwable;
    }
}
