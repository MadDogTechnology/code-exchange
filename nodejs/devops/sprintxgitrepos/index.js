var fs = require("fs");
var lodash = require('lodash/core');

var token = JSON.parse(fs.readFileSync("auth.json")).token;
if (!token) {
    return;
}

var log = fs.createWriteStream('sprint-x-gitrepos.txt', {flags : 'w'});
var GitHubApi = require("github");

var github = new GitHubApi({
    // optional
    debug: false,
    protocol: "https",
    host: "api.github.com",
    pathPrefix: "",
    headers: {
        "user-agent": "Mad-Dog-Bots"
    },
    followRedirects: false, // default: true; there's currently an issue with non-get redirects, so allow ability to disable follow-redirects
    timeout: 5000
});

// user token
github.authenticate({
    type: "token",
    token: token,
});

lodash.forEach(fs.readFileSync("git-repos.txt").toString().split('\n'), function (line) {
    var reponame = line.trim();
    console.log(reponame);

    github.repos.getCommits(
        {
            owner: "MadDogTechnology",
            sha: "develop", // SHA or BRANCH to start listing commits from
            repo: reponame,
            since: new Date("2017/04/06").toISOString(), // 2017-04-06T04:00:00.000Z
            until: new Date("2017/04/20").toISOString(), // 2017-04-20T04:00:00.000Z
            page: 1,
            per_page: 10,
        }, function (err, res) {
            if (!err && res.data.length > 0) {
                lodash.forEach(res.data, function(adata){
                    var message = adata.commit.message;
                    // ignoring branch cut commit messages, which are meaningless for this filtering
                    if (message.indexOf('dropping changes') < 0 
                        && message.indexOf('Version freeze for') < 0 
                        && message.indexOf('Bump releaseNum for') < 0
                    ) {
                        friendlydate = new Date(adata.commit.committer.date).toLocaleString();
                        console.log(reponame + ":" + adata.commit.author.name + ":" + friendlydate + ":" + adata.commit.message);
                        log.write(reponame + '\n');
                        return false; // break;
                    }
                });
            }
            // console.log(err, JSON.stringify(res));
        });
});

