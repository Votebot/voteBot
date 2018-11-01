let express = require('express');
let router = express.Router();
let superagent = require('superagent');

router.get('/', function (req, res, next) {
    res.redirect('/');
});

router.get('/:id', function (req, res, next) {
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

            console.log(obj);

            votes.push(obj);
        });

        let obj = {
            voteTitle: data.body.heading,
            voteHeading: data.body.heading,
            votes: votes
        };

        res.render('votes', obj);
    }).catch(err => {
        console.log(err);
        res.redirect('/');
    });
});

module.exports = router;
