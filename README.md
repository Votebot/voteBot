# VotBot
[INSERT BADGES HERE]

**Warning!**: Vote bot sometimes take same time to respond to requests like commands if that's the case please do not spam any command. If the bot is not answering after some seconds feel free to contact our support [here](https://support.hawkbot.cc) or [here](https://discord.gg/j9RCgsn)

## Intro
VoteBot is back again, and it's better than before (at least I tried to make it better than before) so make sure to take a look at the [features](#features) to see what's new. If you're just here to see the old version of the bot take a look at the [legacy branch](https://github.com/DRSchlaubi/voteBot/tree/legacy)

## Features
 - Ability to create votes that are displayed in [Embeds](https://discordapp.com/developers/docs/resources/channel#embed-object)
 - Ability to add and remove options after creating votes (`v!addoption`, `v!removeoption`)
 - Ability to vote via Discord Reactions
 - Ability to vote for more than one option (Limit can be specified by the creator)
 - Ability to change his previous vote (Limit can also be changed by the creator)
 - Ability to create a PieChart when the vote is finished
 - An interactive setup for creating votes (`v!create`)
 - Ability to specify default settings (`v!settings`) and use them via `v!quickcreate` or the `auto` keyword
 - Protection from removing VoteBot's reactions from the message to prevent manipulation
 - All user votes are getting displayed anonymously
 - Ability to change the title of the vote after its creation
 - Ability to have more than one message which listens for votes
 - The word `Ability` got mentioned exactly 10 times in this list and I am sorry for that
 
# FAQ
 - [Where can I invite the bot?](#where-can-i-invite-the-bot)
 - [Why does the bot need `MESSAGE_MANAGE` and `MESSAGE_EXT_EMOTES` permissions?](#why-does-the-bot-need-message_manage-and-message_ext_emotes-permissions)
 - [Why does the bot remove all reactions from votes?](#why-does-the-bot-remove-all-reactions-from-votes)
 - [Can I take a look at the source code?](#can-i-take-a-look-at-the-source-code)
 - [I found a bug where can I report it?](#i-found-a-bug-where-can-i-report-it)
 
### Where can I invite the bot?
Right [here](https://discordapp.com/api/oauth2/authorize?client_id=569936566965764126&permissions=288768&scope=bot)
### Why does the bot need `MESSAGE_MANAGE` and `MESSAGE_EXT_EMOTES` permissions?
`MESSAGE_MANAGE`: The bot removes every reaction from vote messages to keep the user votes anonymously and removes user input from the interactive setup.
`MESSAGE_EXT_EMOTES`: The most messages of the bot contains self design emotes which are saved on our Emotes guild.
### Why does the bot remove all reactions from votes?
The bot does that to keep user votes anonymously you can see the amount of votes directly next to the option text in the embed.
### Can I take a look at the source code?
Sure it's right [here](https://github.com/DRSchlaubi/votebot)
### I found a bug where can I report it?
You can report bugs on our [issue tracker](https://youtrack.schlaubi.me/newIssue?draftId=2-48)
