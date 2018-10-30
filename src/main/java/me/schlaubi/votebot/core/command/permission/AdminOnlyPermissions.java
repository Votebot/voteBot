package me.schlaubi.votebot.core.command.permission;


import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;

public class AdminOnlyPermissions implements Permissions {

    @Override
    public boolean isCovered(UserPermissions userPermissions, Guild guild) {
        return guild.getMemberById(userPermissions.getUserId()).hasPermission(Permission.MANAGE_SERVER);
    }
}
