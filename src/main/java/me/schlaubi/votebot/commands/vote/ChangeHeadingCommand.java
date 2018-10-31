package me.schlaubi.votebot.commands.vote;

import me.schlaubi.votebot.core.VoteManager;
import me.schlaubi.votebot.core.command.Command;
import me.schlaubi.votebot.core.command.CommandCategory;
import me.schlaubi.votebot.core.command.CommandEvent;
import me.schlaubi.votebot.core.command.Result;
import me.schlaubi.votebot.core.command.permission.Permissions;
import me.schlaubi.votebot.core.entities.Vote;

public class ChangeHeadingCommand extends Command {

    public ChangeHeadingCommand() {
        super(new String[] {"changeheading"}, CommandCategory.VOTE, Permissions.everyone(), "Let's you change the heading of a poll", "<heading>");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        if (args.length == 0)
            return sendHelp();
        VoteManager manager = event.getBot().getVoteManager();
        if (!manager.hasVote(event.getMember()))
            return send(error(event.translate("command.close.novote.title"), event.translate("command.close.novote.description")));
        Vote vote = manager.getVote(event.getMember());
        String heading = event.getArguments();
        if (heading.equals(vote.getHeading()))
            return send(error(event.translate("command.changeheading.duplication.title"), event.translate("command.changeheading.duplication.description")));
        vote.changeHeading(heading);
        vote.updateMessages();
        return send(success(event.translate("command.changeheading.success.title"), String.format(event.translate("command.changeheading.success.description"), heading)));
    }
}
