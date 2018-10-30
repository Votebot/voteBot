package me.schlaubi.votebot.util;

import net.dv8tion.jda.core.EmbedBuilder;

public class EmbedUtil extends SafeMessage {
    /**
     * Creates an success embed
     * @param title The title of the embed
     * @param description The description of the embed
     * @return an EmbedBuilder
     */
    public static EmbedBuilder success(String title, String description) {
        return new EmbedBuilder().setDescription(description).setTitle(":white_check_mark: " + title).setColor(Colors.DARK_BUT_NOT_BLACK);
    }

    /**
     * Creates an error embed
     * @param title The title of the embed
     * @param description The description of the embed
     * @return an EmbedBuilder
     */
    public static EmbedBuilder error(String title, String description) {
        return new EmbedBuilder().setDescription(description).setTitle(":x: " + title).setColor(Colors.DARK_BUT_NOT_BLACK);
    }

    /*
     * Creates an error embed
     * @param event The event of the command where the error was thrown
     * @return an EmbedBuilder
     */
    /*public static EmbedBuilder error(CommandEvent event) {
        return error(event.translate("phrases.error.unknown.title"), event.translate("phrases.error.unknown.description"));
    }*/

    /**
     * Creates an info embed
     * @param title The title of the embed
     * @param description The description of the embed
     * @return an EmbedBuilder
     */
    public static EmbedBuilder info(String title, String description) {
        return new EmbedBuilder().setDescription(description).setTitle(":information_source: " + title).setColor(Colors.DARK_BUT_NOT_BLACK);
    }
}
