let fs = require("fs");
let lodash = require('lodash/core');
let Promise = require("bluebird");

let token = JSON.parse(fs.readFileSync("auth.json")).token;
if (!token) {
    return;
}
let log = fs.createWriteStream('sprint-x-gitrepos.txt', { flags: 'w' });
let GitHubApi = require("github");

let github = new GitHubApi({
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

function processLine(line) {
    return new Promise((resolve, reject) => {
        let reponame = line.trim();
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
                if (err) {
                    reject(err);
                } else {
                    lodash.forEach(res.data, function (adata) {
                        let message = adata.commit.message;
                        // ignoring branch cut commit messages, which are meaningless for this filtering
                        if (message.indexOf('dropping changes') < 0
                            && message.indexOf('Version freeze for') < 0
                            && message.indexOf('Bump releaseNum for') < 0
                        ) {
                            friendlydate = new Date(adata.commit.committer.date).toLocaleString();
                            let result = `${reponame},${adata.commit.author.name},${friendlydate},${adata.commit.message}`;
                            resolve(result);
                            log.write(reponame + '\n');
                            return false; // break;
                        } else {
                            resolve();
                        }
                    });
                }
            });
    });

};

Promise.promisifyAll(fs);

fs.readFileAsync("git-repos.txt").then(contents => {

    Promise.all(contents.toString().split('\n').map(processLine)).then(results => {
        results.forEach(result => {
            if (result) {
                console.log(`${result}`);
            }
        })
    });


}).catch(function (e) {
    console.error(e.stack);
});
