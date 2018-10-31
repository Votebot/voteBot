package me.schlaubi.votebot.commands.vote;

import me.schlaubi.votebot.core.VoteManager;
import me.schlaubi.votebot.core.command.Command;
import me.schlaubi.votebot.core.command.CommandCategory;
import me.schlaubi.votebot.core.command.CommandEvent;
import me.schlaubi.votebot.core.command.Result;
import me.schlaubi.votebot.core.command.permission.Permissions;
import me.schlaubi.votebot.core.entities.Vote;
import me.schlaubi.votebot.util.Misc;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AddOptionCommand extends Command {

    public AddOptionCommand() {
        super(new String[] {"addoption"}, CommandCategory.VOTE, Permissions.everyone(), "Let's you add another option to your vote", "<option>");
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
        if (vote.getAnswers().containsKey(option))
            return send(error(event.translate("command.addoption.duplication.title"), event.translate("command.addoption.duplication.description")));
        //Find emote
        List<String> allEmotes = Arrays.asList(Misc.EMOTES);
        allEmotes.removeAll(vote.getEmotes().values());
        String emote = allEmotes.get(ThreadLocalRandom.current().nextInt(allEmotes.size()));
        //Register emote in vote
        vote.getEmotes().put(emote, option);
        //Register option
        vote.getAnswers().put(option, 0);
        //Save vote
        vote.save();
        //Add emotes & update messages
        vote.crawlMessages().forEach(message -> {
            message.addReaction(emote).queue();
            message.editMessage(vote.buildEmbed(message.getIdLong()).build()).queue();
        });
        return send(success(event.translate("command.addoption.success.title"), String.format(event.translate("command.addoption.success.description"), option)));
    }
}
