package me.schlaubi.votebot.listener;

import lombok.extern.log4j.Log4j2;
import me.schlaubi.votebot.core.events.CommandExecutedEvent;
import me.schlaubi.votebot.core.events.CommandFailedEvent;
import me.schlaubi.votebot.core.events.CommandPermissionViolationEvent;
import me.schlaubi.votebot.util.Colors;
import me.schlaubi.votebot.util.EmbedUtil;
import me.schlaubi.votebot.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

@SuppressWarnings("unsused")
@Log4j2
public class CommandLogger {

    @SubscribeEvent
    private void onCommandExecution(CommandExecutedEvent executedEvent) {
        log.debug(String.format("[Command] Command %s got executed by %s on guild %s(%d)", executedEvent.getCommand().getName(), executedEvent.getAuthor().getName(), executedEvent.getGuild().getName(), executedEvent.getGuild().getIdLong()));
    }

    @SubscribeEvent
    private void onCommandFail(CommandFailedEvent failEvent) {
        log.error(String.format("[Command] Command %s threw an error %s on guild %s(%d)", failEvent.getCommand().getName(), failEvent.getAuthor().getName(), failEvent.getGuild().getName(), failEvent.getGuild().getIdLong()), failEvent.getThrowable());
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(":no_entry_sign: " + failEvent.translate("phrases.error.internal"))
                .setDescription(String.format("We're sorry, but an internal error occured\n```%s```", failEvent.getThrowable().getClass().getCanonicalName() + ": " + failEvent.getThrowable().getMessage()))
                .setColor(Colors.DARK_BUT_NOT_BLACK);
        SafeMessage.sendMessage(failEvent.getChannel(), builder);
    }

    @SubscribeEvent
    private void onPermissionViolations(CommandPermissionViolationEvent noPermissionEvent) {
        String permission = noPermissionEvent.getCommand().getPermissions().toString();
        EmbedBuilder builder = EmbedUtil.error(noPermissionEvent.translate("phrases.nopermission.title"), noPermissionEvent.translate(String.format("phrases.nopermission.%s", permission)));
        SafeMessage.sendMessage(noPermissionEvent.getChannel(), builder);
    }
}
