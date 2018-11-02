let express = require('express');
let router = express.Router();
let superagent = require('superagent');

router.get('/', function (req, res) {
    res.redirect('/');
});

router.get('/:id', function (req, res) {
    let voteId = req.params.id;

    superagent.get(`http://localhost:8080/votes/${voteId}`).then(data => {
        let voteOptions = Object.keys(data.body.answers);
        let emotes = [];
        let votes = [];

        Object.keys(data.body.emotes).forEach(emote => {
            emotes[data.body.emotes[emote]] = emote;
        });

        voteOptions.forEach(option => {
            let obj = {
                name: option,
                count: data.body.answers[option],
                emote: emotes[option]
            };
            votes.push(obj);
        });

        let obj = {
            voteTitle: data.body.heading,
            voteHeading: data.body.heading,
            votes: votes,
            user: {}
        };

        let identifier = '254892085000405004'; // TODO: FIX ID

        superagent.get(`https://discordapp.com/api/users/${identifier}`)
            .set('Authorization', 'Bot NTA3NjA4NTIyMzk4MTA1NjEw.DrzK9w.WCiL6OZDWwLjSFzog8wWNaolUZQ') // TODO: CHANGE TOKEN
            .then(discord_data => {
                console.log(discord_data.body);
                obj.user.avatar = `https://cdn.discordapp.com/avatars/${identifier}/${discord_data.body.avatar}.png`;
                obj.user.name = discord_data.body.username;
                obj.user.discriminator = discord_data.body.discriminator;
                obj.user.id = discord_data.body.id;
                res.render('votes', obj);
            }).catch(error => {
            console.log(error);
        });
    }).catch(err => {
        console.log(err);
        res.redirect('/');
    });
});

module.exports = router;
