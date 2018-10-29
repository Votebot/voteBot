package me.schlaubi.votebot.core.command.permission;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.schlaubi.votebot.VoteBot;
import net.dv8tion.jda.core.entities.User;

@RequiredArgsConstructor
@Getter
public class UserPermissions {

    private final User user;

    public UserPermissions(long id) {
        this(VoteBot.getInstance().getShardManager().getUserById(id));
    }
}
