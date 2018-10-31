package me.schlaubi.votebot.util;

import me.schlaubi.votebot.core.command.Command;
import me.schlaubi.votebot.core.command.SubCommand;
import me.schlaubi.votebot.core.translation.TranslationLocale;
import net.dv8tion.jda.core.EmbedBuilder;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FormatUtil extends EmbedUtil{

    /**
     * Formats the helpmessage for a command
     * @param command The command
     * @return an EmbedBuilder
     */
    public static EmbedBuilder formatCommand(Command command) {
        return info(command.getName() + " - Help", formatUsage(command));
    }

    private static String formatUsage(Command command) {
        StringBuilder stringBuilder = new StringBuilder();
        return addUsages(stringBuilder, command).toString();
    }

    private static StringBuilder addUsages(StringBuilder stringBuilder, Command command) {
        stringBuilder.append("Command aliases: `").append(Arrays.toString(command.getAliases()).replace("[", "").replace("]", "")).append("`\n");
        stringBuilder.append("Description: `").append(command.getDescription()).append("`").append("\n");
        stringBuilder.append("Usage: `").append(buildUsage(command)).append("`\n");
        command.getSubCommandAssociations().values().stream().distinct().sorted(Comparator.comparing(Command::getName)).forEach(subCommand -> stringBuilder.append(buildUsage(subCommand)).append("\n"));
        return stringBuilder;
    }

    private static String buildUsage(Command command) {
        if (command instanceof SubCommand) {
            SubCommand subCommand = ((SubCommand) command);
            return "v!" + subCommand.getMainCommand().getName() + " " + subCommand.getName() + " " + subCommand.getUsage() + " - " + subCommand.getDescription();
        }
        return "v!" + command.getName() + " " + command.getUsage();
    }

    public static String formatLocales(List<TranslationLocale> locales) {
        StringBuilder stringBuilder = new StringBuilder();
        locales.forEach(locale -> stringBuilder.append(String.format("%s (%s)",  locale.getLocale().toLanguageTag(), locale.getLanguageName())).append("\n"));
        return stringBuilder.toString();
    }
}
