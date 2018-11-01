package me.schlaubi.votebot.commands.general;

import me.schlaubi.votebot.core.command.Command;
import me.schlaubi.votebot.core.command.CommandCategory;
import me.schlaubi.votebot.core.command.CommandEvent;
import me.schlaubi.votebot.core.command.Result;
import me.schlaubi.votebot.core.command.permission.Permissions;

public class AboutCommand extends Command {

    public AboutCommand() {
        super(new String[]{"about", "information"}, CommandCategory.GENERAL, Permissions.everyone(), "Displays some information about VoteBot", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return send(
                info(event.translate("command.info.title"),
                        event.translate("command.info.description"))
                        .addField(event.translate("command.info.invite"), "[votebot.schlaubi.me/invite](https://votebot.schlaubi.me/invite)", false)
                        .addField(event.translate("command.info.support"), "[discord.gg/xTryQU](https://discord.gg/xTryQU)", true)
                        .addField(event.translate("command.info.donate"), "[paypal.me/schlaubiboy](https://paypal.me/schlaubiboy)", true)
                        .addField(event.translate("command.info.translate"), "[i18n.votebot.schlaubi.me](https://i18n.votebot.schlaubi.me)", false)
                        .addField(event.translate("command.info.source"), "[github.com/DRSchlaubi/voteBot](https://github.com/DRSchlaubi/votebot)", true)
                        .setFooter(event.translate("command.info.footer"), null)

        );
    }
}
