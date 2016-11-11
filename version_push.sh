#!/bin/bash

git config --global user.name $GIT_NAME
git config --global user.email $GIT_EMAIL

git config credential.helper "store --file=.git/credentials"
echo "https://${GH_TOKEN}:@github.com" > .git/credentials
git config --global push.default simple

rev=$(git rev-parse --short HEAD)

git add .
git commit -am "From Travis Generated credentials for $TRAVIS_TAG - $rev"
git push --set-upstream origin HEAD:develop