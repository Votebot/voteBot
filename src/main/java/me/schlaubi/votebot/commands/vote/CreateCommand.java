package me.schlaubi.votebot.commands.vote;

import me.schlaubi.votebot.core.VoteManager;
import me.schlaubi.votebot.core.command.Command;
import me.schlaubi.votebot.core.command.CommandCategory;
import me.schlaubi.votebot.core.command.CommandEvent;
import me.schlaubi.votebot.core.command.Result;
import me.schlaubi.votebot.core.command.permission.Permissions;
import me.schlaubi.votebot.core.entities.Vote;
import me.schlaubi.votebot.util.Misc;
import net.dv8tion.jda.core.entities.Message;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class CreateCommand extends Command {

    public CreateCommand() {
        super(new String[]{"create", "add", "make"}, CommandCategory.GENERAL, Permissions.everyone(), "Creates a new vote", "<heading>|<option1>|<option2>|[option3]|...");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        String[] voteArgs = String.join(" ", Arrays.asList(args)).split("\\|");
        if (voteArgs.length < 3)
            return sendHelp();
        VoteManager manager = event.getBot().getVoteManager();
        if (manager.hasVote(event.getMember()))
            return send(error(event.translate("command.create.alreadycreated.title"), event.translate("command.create.alreadycreated.description")));
        String heading = voteArgs[0];
        List<String> options = Arrays.asList(voteArgs).subList(1, voteArgs.length);
        if (options.size() < 1)
            return send(error(event.translate("command.create.tolessoptions.title"), event.translate("command.create.tolessoptions.description")));
        //Create random emotes
        ThreadLocalRandom generator = ThreadLocalRandom.current();
        List<String> availableEmote = new ArrayList<>(Arrays.asList(Misc.EMOTES));
        Map<String, String> emotes = new HashMap<>();
        for (String option : options) {
            int index = generator.nextInt(availableEmote.size());
            String emote = availableEmote.get(index);
            availableEmote.remove(index);
            emotes.put(option, emote);
        }
        Vote vote = manager.createVote(event.getMember(), heading, options, emotes, event.getMessage());
        Message voteMessage = sendMessageBlocking(event.getChannel(), info("Creating ...", "Please wait while the vote is beeing created "));
        emotes.values().forEach(emote -> voteMessage.addReaction(emote).complete());
        voteMessage.editMessage(vote.buildEmbed().build()).queue();
        return send(success(event.translate("command.create.created.title"), event.translate("command.create.created.description")));
    }
}
