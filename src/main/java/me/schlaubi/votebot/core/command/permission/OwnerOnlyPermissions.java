package me.schlaubi.votebot.core.command.permission;

import me.schlaubi.votebot.VoteBot;
import net.dv8tion.jda.core.entities.Guild;

public class OwnerOnlyPermissions implements Permissions {

    @Override
    public boolean isCovered(UserPermissions userPermissions, Guild guild) {
        return VoteBot.getInstance().getConfiguration().getLongList("owners").contains(userPermissions.getUserId());
    }
}
