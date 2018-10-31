package me.schlaubi.votebot.restapi.controllers;

import me.schlaubi.votebot.restapi.Launcher;
import me.schlaubi.votebot.restapi.entities.Vote;
import me.schlaubi.votebot.restapi.exceptions.RessourceNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/votes")
public class GetVoteControllers {

    @RequestMapping("/{messageID}")
    public Vote getVoteByMessageId(@PathVariable(name = "messageID") long messageId) {
        Vote vote = Launcher.getProvider().getVoteByMessage(messageId);
        return checkVote(vote);
    }

    @RequestMapping("/byMember")
    public Vote getVoteByMember(@RequestParam("guild") long guildId, @RequestParam("user") long authorId) {
        Vote vote = Launcher.getProvider().getVoteByMember(guildId, authorId);
        return checkVote(vote);
    }

    private Vote checkVote(Vote vote) {
        if (vote == null)
            throw new RessourceNotFoundException();
        return vote;
    }
}
