@echo off
echo Lancement de l'integration Backend...
git fetch newrepo
git checkout -b integrationBranche newrepo/integrationBranche
git merge AzizBack
echo Termin ! S'il y a des conflits, ne t'inquiete pas, l'assistant les resoudra.
pause
