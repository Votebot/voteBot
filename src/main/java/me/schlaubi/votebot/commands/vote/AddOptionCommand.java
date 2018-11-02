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

import java.util.Arrays;
import java.util.LinkedList;
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
        if (vote.getOptions().size() != vote.getOptions().stream().distinct().count())
            return send(error(event.translate("command.addoption.dupe.title"), event.translate("command.addoption.dupe.description")));
        if (vote.getOptions().size() >= 10)
            return send(error(event.translate("command.create.tomanyoptions.title"), event.translate("command.create.tomanyoptions.description")));
        if (vote.getOptions().contains(option))
            return send(error(event.translate("command.addoption.duplication.title"), event.translate("command.addoption.duplication.description")));
        //Get unused vote id
        int voteId = vote.getOptions().size();
        //Find emote
        List<String> allEmotes = new LinkedList<>(Arrays.asList(Misc.EMOTES));
        allEmotes.removeAll(vote.getEmotes().keySet());
        String emote = allEmotes.get(ThreadLocalRandom.current().nextInt(allEmotes.size()));
        //Register emote in vote
        vote.getEmotes().put(emote, voteId);
        //Register option
        vote.getOptions().add(option);
        //Save vote
        vote.save();
        //Add emotes & update messages
        vote.crawlMessages().forEach(message -> {
            message.addReaction(emote).queue();
            SafeMessage.editMessage(message, vote.buildEmbed(message.getIdLong()));
        });
        return send(success(event.translate("command.addoption.success.title"), String.format(event.translate("command.addoption.success.description"), option)));
    }
}
