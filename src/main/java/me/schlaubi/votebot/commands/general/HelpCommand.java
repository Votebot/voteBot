package me.schlaubi.votebot.commands.general;

import me.schlaubi.votebot.core.command.*;
import me.schlaubi.votebot.core.command.permission.Permissions;
import me.schlaubi.votebot.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;

import java.util.Comparator;
import java.util.stream.Collectors;

import static me.schlaubi.votebot.util.FormatUtil.formatCommand;

public class HelpCommand extends Command {

    public HelpCommand() {
        super(new String[]{"help", "h", "?"}, CommandCategory.GENERAL, Permissions.everyone(), "Displays a list of all commands", "[command]");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        if (args.length == 0)
            return send(formatCommandList(event));
        if (!event.getBot().getCommandManager().getCommandAssociations().containsKey(args[0])) {
            return send(error(event.translate("command.help.notfound.title"), event.translate("command.help.notfound.description")));
        }
        Command command = event.getBot().getCommandManager().getCommandAssociations().get(args[0]);
        return send(formatCommand(command));
    }

    private EmbedBuilder formatCommandList(CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(":information_source: " + event.translate("command.help.title"))
                .setColor(Colors.DARK_BUT_NOT_BLACK)
                .setDescription(String.format(event.translate("command.help.description"), "v!"));
        for (CommandCategory commandCategory : CommandCategory.class.getEnumConstants()) {
            String formattedCategory = formatCategory(commandCategory, event.getBot().getCommandManager());
            if (!"".equals(formattedCategory))
                builder.addField(commandCategory.getDisplayName(), formattedCategory, false);
        }
        return builder;
    }

    private String formatCategory(CommandCategory commandCategory, CommandManager manager) {
        StringBuilder stringBuilder = new StringBuilder();
        manager.getCommandAssociations().values().stream().distinct().sorted(Comparator.comparing(Command::getName)).filter(command -> command.getCategory() == commandCategory).collect(Collectors.toList()).forEach(command -> stringBuilder.append("`").append(command.getName()).append("`, "));
        if (stringBuilder.toString().contains(","))
            stringBuilder.replace(stringBuilder.lastIndexOf(","), stringBuilder.lastIndexOf(",") + 1, "");
        return stringBuilder.toString();
    }
}
