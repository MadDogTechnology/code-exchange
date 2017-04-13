var fs = require("fs");
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

fs.readFileSync("git-repos.txt").toString().split('\n').forEach(function (line) {
    var reponame = line.trim();
    console.log(reponame);

    github.repos.getCommits(
        {
            owner: "MadDogTechnology",
            sha: "develop", // SHA or BRANCH to start listing commits from
            repo: reponame,
            since: "2017-04-06T00:00:00Z",
            until: "2017-05-01T00:00:00Z",
            page: "1",
            per_page: 10
        }, function (err, res) {
            if (!err && res.data.length > 0) {
                for (var i = 0, len = res.data.length; i < len; i++) {
                    var message = res.data[i].commit.message;
                    // ignoring branch cut meaningless names
                    if (message.indexOf('dropping changes') < 0 && message.indexOf('Version freeze for') < 0 && message.indexOf('Bump releaseNum for') < 0 && message.indexOf("Merge branch 'develop' of") < 0) {
                        console.log(reponame + ":" + res.data[i].commit.author.name + ":" + res.data[i].commit.committer.date + ":" + res.data[i].commit.message);
                        log.write(reponame + '\n');
                        break;
                    }
                }
                
            }
            // console.log(err, JSON.stringify(res));
        });
});

