package me.schlaubi.votebot.commands.vote;

import me.schlaubi.votebot.core.VoteManager;
import me.schlaubi.votebot.core.command.Command;
import me.schlaubi.votebot.core.command.CommandCategory;
import me.schlaubi.votebot.core.command.CommandEvent;
import me.schlaubi.votebot.core.command.Result;
import me.schlaubi.votebot.core.command.permission.Permissions;
import me.schlaubi.votebot.core.entities.Vote;
import me.schlaubi.votebot.util.Misc;

public class RemoveOptionCommand extends Command {

    public RemoveOptionCommand() {
        super(new String[] {"removeoption", "ro"}, CommandCategory.VOTE, Permissions.everyone(), "Let's you remove an option from a vote", "<option>");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        if (args.length == 0)
            return sendHelp();
        VoteManager manager = event.getBot().getVoteManager();
        if (!manager.hasVote(event.getMember()))
            return send(error(event.translate("command.close.novote.title"), event.translate("command.close.novote.description")));
        Vote vote = manager.getVote(event.getMember());
        String option = event.getArguments();
        if (!vote.getAnswers().containsKey(option))
            return send(error(event.translate("command.removeoption.invalid.title"), event.translate("command.removeoption.invalid.description")));
        //Find options emote
        String emote = Misc.getValueByKey(vote.getEmotes(), option);
        //Unregister option && emote
        vote.getAnswers().remove(option);
        vote.getEmotes().remove(option);
        vote.getUserVotes().entrySet().parallelStream().forEach((entry) -> {
            if (entry.getValue().equals(option))
                vote.getUserVotes().remove(entry.getKey());
        });
        vote.crawlMessages().forEach(message -> {
            message.getTextChannel().removeReactionById(message.getIdLong(), emote);
            message.editMessage(vote.buildEmbed(message.getIdLong()).build()).queue();
        });
        return send(success(event.translate("command.removeoption.success.title"), String.format(event.translate("command.removeoption.success.description"), option)));
    }
}
