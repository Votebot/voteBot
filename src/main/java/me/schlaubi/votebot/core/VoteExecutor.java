package me.schlaubi.votebot.core;

import lombok.RequiredArgsConstructor;
import me.schlaubi.votebot.core.cache.VoteCache;
import me.schlaubi.votebot.core.entities.Vote;
import me.schlaubi.votebot.util.NameThreadFactory;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
@SuppressWarnings("unused")
public class VoteExecutor {

    private final VoteCache cache;
    private final ExecutorService executor = Executors.newCachedThreadPool(new NameThreadFactory("VoteExecutor"));

    @SubscribeEvent
    private void onMessageReactionAdd(GuildMessageReactionAddEvent event) {
        executor.execute(() -> onReaction(event));
    }

    @SubscribeEvent
    private void onMessageReactionRemove(GuildMessageReactionRemoveEvent event) {
        executor.execute(() -> onReactionRemove(event));
    }

    private void onReactionRemove(GuildMessageReactionRemoveEvent event) {
        final User user = event.getUser();
        final long messageId = event.getMessageIdLong();
        if (!cache.isPollMessage(messageId))
            return;
        if (!event.getReaction().isSelf())
            return;
        Vote vote = cache.getVote(messageId);
        final MessageReaction.ReactionEmote reactionEmote = event.getReactionEmote();
        if (vote.getEmotes().containsKey(reactionEmote.getName()))
            event.getChannel().addReactionById(messageId, reactionEmote.getName()).queue();
    }

    private void onReaction(GuildMessageReactionAddEvent event) {
        final User user = event.getUser();
        if (user.isBot() || !cache.isPollMessage(event.getMessageIdLong()))
            return;
        if (event.getGuild().getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_MANAGE))
            event.getReaction().removeReaction(user).queue();
        Vote vote = cache.getVote(event.getMessageIdLong());
        String emoteName = event.getReaction().getReactionEmote().getName();
        if (!vote.getEmotes().containsKey(emoteName))
            return;
        if (!vote.isPermitted(user)) {
            user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("You can only change your opinion 3 times.").queue());
            return;
        }
        vote.vote(vote.getEmotes().get(emoteName), user);
        vote.updateMessages();
    }


}
