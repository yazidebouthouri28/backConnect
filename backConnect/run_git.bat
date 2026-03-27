@echo off
git fetch newrepo > git_fetch.log 2>&1
git checkout -b integrationBranche newrepo/integrationBranche > git_checkout.log 2>&1
git merge AzizBack > git_merge.log 2>&1
