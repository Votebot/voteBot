package me.schlaubi.votebot.core.command.permission;


import net.dv8tion.jda.core.entities.Guild;

public interface Permissions {


    boolean isCovered(UserPermissions userPermissions, Guild guild);

    String getIdentifier();

    static Permissions ownerOnly() {
        return new OwnerOnlyPermissions();
    }

    static Permissions adminOnly() {
        return new AdminOnlyPermissions();
    }

    static Permissions everyone() {
        return new EveryonePermissions();
    }
}
