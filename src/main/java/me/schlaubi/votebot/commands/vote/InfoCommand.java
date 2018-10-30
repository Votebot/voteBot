package me.schlaubi.votebot.commands.vote;

import me.schlaubi.votebot.core.VoteManager;
import me.schlaubi.votebot.core.command.Command;
import me.schlaubi.votebot.core.command.CommandCategory;
import me.schlaubi.votebot.core.command.CommandEvent;
import me.schlaubi.votebot.core.command.Result;
import me.schlaubi.votebot.core.command.permission.Permissions;
import me.schlaubi.votebot.core.entities.Vote;
import net.dv8tion.jda.core.entities.Message;

public class InfoCommand extends Command {

    public InfoCommand() {
        super(new String[]{"info", "view", "stats"}, CommandCategory.VOTE, Permissions.everyone(), "Displays the stats of the current vote", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        VoteManager manager = event.getBot().getVoteManager();
        if (!manager.hasVote(event.getMember()))
            return send(error(event.translate("command.close.novote.title"), event.translate("command.close.novote.description")));
        Vote vote = manager.getVote(event.getMember());
        Message message = sendMessageBlocking(event.getChannel(), info(event.translate("command.info.loading.title"), event.translate("command.info.loading.description")));
        vote.addEmotes(message);
        vote.addMessage(message);
        message.editMessage(vote.buildEmbed().build()).queue();
        return null;
    }
}
