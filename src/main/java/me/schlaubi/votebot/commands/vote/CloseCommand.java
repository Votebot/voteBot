package me.schlaubi.votebot.commands.vote;

import lombok.extern.log4j.Log4j2;
import me.schlaubi.votebot.core.VoteManager;
import me.schlaubi.votebot.core.command.Command;
import me.schlaubi.votebot.core.command.CommandCategory;
import me.schlaubi.votebot.core.command.CommandEvent;
import me.schlaubi.votebot.core.command.Result;
import me.schlaubi.votebot.core.command.permission.Permissions;
import me.schlaubi.votebot.core.entities.Vote;
import me.schlaubi.votebot.core.graphics.PieChart;
import me.schlaubi.votebot.core.graphics.PieTile;
import me.schlaubi.votebot.util.SafeMessage;
import net.dv8tion.jda.core.MessageBuilder;
import org.knowm.xchart.style.Styler;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class CloseCommand extends Command {

    public CloseCommand() {
        super(new String[]{"close", "delete", "stop", "remove"}, CommandCategory.VOTE, Permissions.everyone(), "Let's you close a vote", "[messageId]");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        Vote target;
        VoteManager manager = event.getBot().getVoteManager();
        if (args.length == 0)
            if (manager.hasVote(event.getMember()))
                target = manager.getVote(event.getMember());
            else
                return send(error(event.translate("command.close.novote.title"), event.translate("command.close.novote.description")));
        else {
            try {
                long pollMessageId = Long.parseLong(args[0]);
                if (!manager.getCache().isPollMessage(pollMessageId))
                    return send(error(event.translate("command.close.novoteid.title"), event.translate("command.close.novoteid.description")));
                target = manager.getCache().getVote(pollMessageId);
                if (target.getAuthorId() != event.getAuthor().getIdLong() && !Permissions.adminOwnly().isCovered(event.getUserPermissions(), event.getGuild()))
                    return send(error(event.translate("command.close.nopermission.title"), event.translate("command.close.nopermission.description")));
            } catch (NumberFormatException e) {
                return send(error(event.translate("command.close.invalidnumber.title"), event.translate("command.close.invalidnumber.description")));
            }
        }
        manager.closeVote(target);
        List<PieTile> tiles = new ArrayList<>();
        var allVotes = target.getUserVotes().size();
        if (target.getUserVotes().isEmpty())
            return send("No votes");
        target.getAnswers().forEach((option, answers) -> tiles.add(new PieTile(option, ((double) answers / allVotes))));
        try {
            PieChart chart = new PieChart(target.getHeading(), tiles.toArray(new PieTile[0]));
            File chartFile = chart.encode();
            SafeMessage.sendFile(event.getChannel(), new MessageBuilder().setContent(event.translate("command.close.finished")).build(), chartFile);
            chartFile.delete();
        } catch (IOException | FontFormatException e) {
            log.warn("[ChartGenerator] Error while closing chart", e);
            return send(error(event.translate("command.close.unknownerror.title"), event.translate("command.close.unknownerror.description")));
        }
        return null;
    }

}
