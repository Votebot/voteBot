package me.schlaubi.votebot.core.command.permission;


import net.dv8tion.jda.core.entities.Guild;

public interface Permissions {


    boolean isCovered(UserPermissions userPermissions, Guild guild);

    static Permissions ownerOnly() {
        return new OwnerOnlyPermissions();
    }

    static Permissions adminOwnly() {
        return new AdminOnlyPermissions();
    }

    static Permissions everyone() {
        return new EveryonePermissions();
    }
}
