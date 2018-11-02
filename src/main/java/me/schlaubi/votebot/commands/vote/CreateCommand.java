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
import net.dv8tion.jda.core.entities.Message;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class CreateCommand extends Command {

    public CreateCommand() {
        super(new String[]{"create", "add", "make", "vote", "poll"}, CommandCategory.VOTE, Permissions.everyone(), "Creates a new vote", "<heading>|<option1>|<option2>|[option3]|...");
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
        //Look for invalid thingies
        options = options.stream().filter(option -> !option.equals("")).collect(Collectors.toList());
        if (options.size() <= 1)
            return send(error(event.translate("command.create.tolessoptions.title"), event.translate("command.create.tolessoptions.description")));
        if (options.size() > 10)
            return send(error(event.translate("command.create.tomanyoptions.title"), event.translate("command.create.tomanyoptions.description")));
        //Create random emotes
        ThreadLocalRandom generator = ThreadLocalRandom.current();
        List<String> availableEmote = new ArrayList<>(Arrays.asList(Misc.EMOTES));
        Map<String, Integer> emotes = new HashMap<>();
        int voteIdCounter = 0;
        for (String ignored : options) {
            int index = generator.nextInt(availableEmote.size());
            String emote = availableEmote.get(index);
            availableEmote.remove(index);
            emotes.put(emote, voteIdCounter);
            voteIdCounter++;
        }
        Message voteMessage = sendMessageBlocking(event.getChannel(), info("Creating ...", "Please wait while the vote is beeing created "));
        Vote vote = manager.createVote(event.getMember(), heading, options, emotes, voteMessage);
        vote.addEmotes(voteMessage);
        SafeMessage.editMessage(voteMessage, vote.buildEmbed(voteMessage.getIdLong()));
        return send(success(event.translate("command.create.created.title"), event.translate("command.create.created.description")));
    }
}
