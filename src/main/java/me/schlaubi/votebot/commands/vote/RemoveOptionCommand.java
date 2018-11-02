package me.schlaubi.votebot.commands.vote;

import me.schlaubi.votebot.core.VoteManager;
import me.schlaubi.votebot.core.command.Command;
import me.schlaubi.votebot.core.command.CommandCategory;
import me.schlaubi.votebot.core.command.CommandEvent;
import me.schlaubi.votebot.core.command.Result;
import me.schlaubi.votebot.core.command.permission.Permissions;
import me.schlaubi.votebot.core.entities.Vote;
import me.schlaubi.votebot.util.Misc;
import me.schlaubi.votebot.util.SafeMessage;
import net.dv8tion.jda.core.utils.Helpers;

public class RemoveOptionCommand extends Command {

    public RemoveOptionCommand() {
        super(new String[] {"removeoption", "ro"}, CommandCategory.VOTE, Permissions.everyone(), "Let's you remove an option from a vote", "<option/index>");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        if (args.length == 0)
            return sendHelp();
        VoteManager manager = event.getBot().getVoteManager();
        if (!manager.hasVote(event.getMember()))
            return send(error(event.translate("command.close.novote.title"), event.translate("command.close.novote.description")));
        Vote vote = manager.getVote(event.getMember());
        if (vote.getOptions().size() == 2)
            return send(error(event.translate(""), event.translate("")));
        String option = event.getArguments();
        if (vote.getOptions().size() != vote.getOptions().stream().distinct().count())
            return send(error(event.translate("command.removeoption.dupe.title"), event.translate("command.removeoption.dupe.description")));
        if (!vote.getOptions().contains(option))
            return send(error(event.translate("command.removeoption.invalid.title"), event.translate("command.removeoption.invalid.description")));
        //Find voteId
        int voteId;
        if (Helpers.isNumeric(option))
            voteId = Integer.parseInt(args[0]);
        else
            voteId = vote.getOptions().indexOf(option);
        if (voteId == -1)
            return send(error(event.translate("command.removeoption.invalidid.title"), event.translate("command.removeoption.invalidid.description")));
        //Find options emote
        String emote = Misc.getValueByKey(vote.getEmotes(), voteId);
        //Unregister option && emote
        vote.getOptions().remove(option);
        vote.getEmotes().remove(emote);
        vote.getUserVotes().entrySet().parallelStream().forEach((entry) -> {
            if (entry.getValue().equals(voteId)) {
                vote.getUserVotes().remove(entry.getKey());
                vote.getVoteCounts().remove(entry.getKey());
            }
        });
        //Update reactions registrations
        vote.getEmotes().entrySet().parallelStream().filter(entry -> entry.getValue() > voteId).forEach(entry -> {
            vote.getEmotes().remove(entry.getKey());
            vote.getEmotes().put(entry.getKey(), entry.getValue() - 1);
        });
        //Update userVotes
        vote.getUserVotes().entrySet().parallelStream().filter(entry -> entry.getValue() > voteId).forEach(entry -> {
            vote.getUserVotes().remove(entry.getKey());
            vote.getUserVotes().put(entry.getKey(), entry.getValue() - 1);
        });
        vote.crawlMessages().forEach(message -> {
            message.getTextChannel().removeReactionById(message.getIdLong(), emote);
            SafeMessage.editMessage(message, vote.buildEmbed(message.getIdLong()));
        });
        return send(success(event.translate("command.removeoption.success.title"), String.format(event.translate("command.removeoption.success.description"), option)));
    }
}
