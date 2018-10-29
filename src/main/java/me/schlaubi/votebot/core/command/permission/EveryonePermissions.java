package me.schlaubi.votebot.core.command.permission;

public class EveryonePermissions implements Permissions {

    @Override
    public boolean isCovered(UserPermissions userPermissions, net.dv8tion.jda.core.entities.Guild guild) {
        return true;
    }
}
