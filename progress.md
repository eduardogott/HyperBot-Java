## Commands from Python left to add:
> Commands in `>` are not in Python, so they are yet to be implemented. 

### apis.py
!country - Retrieves info about a country
!apod - Gets NASA APOD.

### customcommands.py
!customcommand - Creates a custom command
!ccdelete - Delete a custom command
!cclist - Lists custom commands
!ccedit - Edit a custom command

### dm.py
No commands yet - Implement some commands in dm

### economy.py
!daily - Claims daily bonus
!work - Works for X time
!balance - Checks someone balance
!pay - Send someone money
!shop - Open shop
!buy - Buy an item
!inventory - See one's inventory
!use - Use (consume) an item
!equip - (un)equip an item
!eco - Change balances
!giveitem - Give somebody an item
!takeitem - Takes somebody item
!clearinventory - Clear one's inventory
!createitem - Creates an item
!deleteitem - Deletes an item

### giveaway.py
!gcreate - Interactively create a giveaway
!greroll - Rerolls a giveaway
!gend - Ends a giveaway (with a winner)
!gcancel - Cancels a giveaway (without a winner)

### help.py
!help - duh
!about - duh

### leveling.py
!level - Displays user level
!rank - Displays user place on the leaderboard
!leaderboard - Displays the leaderboard
!expadmin - Manages somebody's exp

### moderation.py
!ticketban - Forbids someone of creating tickets
!unticketban - Removes the ticket prohibiton
!ban - Bans someone from the guild
!unban - Unbans someone from the guild
!timeout - Timeout someone on the guild
!untimeout - Untimeout someone on the guild
!kick - Kicks someone from the guild
!clear - Clears the chat
!slowmode - Applies a slowmode to the chat
!setnickname - Sets a user's nickname/display name
!history - Checks the punishment history for someone
!staffhistory - Checks the punishments applied by someone
!clearhistory - Clears someone history
!clearstaffhistory - Clears staff history
!lock - Lock the current channel
!unlock - Unlocks the current channel
!warn - Apply a warning to someone, that when they reach X warnings, a punishment is automatically applied.
!unwarn - Removes someone's last warn
!warnings - Shows a members warnings
> !clearwarnings - Clears all warnings for a member
> !clearallbans - Clears all bans in the guild
> !clearalltimeouts - Clears all timeouts in the guild
> !clearallwarnings - Clears all warnings in the guild
> !clearallticketbans - Clears all ticketsbans in the guild
> !clearallpunishments - Clears all punishments in the guild (i.e. executes the last 4 commands)

### music.py
#### Setup
> !voicechannels - Remove or adds channels in which music will be played
> !djrole - Sets the DJ role
> !reset - Resets ALL the settings

#### Playback
!play - Plays a song
!join - Summons the bot
!leave - Kicks the bot
!insert - Places a song first in the queue, not skipping the current
!playnow - Plays the song immediately, skipping the current

#### TrackState
!backwards - Seek backwards by x seconds
!forwards - Seek forward by x seconds
!pause - Pauses the song
!resume - Resumes the playback
!volume - Changes the volume of the track (0-200)

#### QueueState
!reverse - Reverses the queue
!shuffle - Shuffles the queue
!sort - Sorts the queue by name, length or artist
!move - Move a song in the queue
!swap - Swap two songs in the queue
!previous - Go to the previous song
!skip - Forceskip if DJ, voteskip if not
!voteskip - Forces a voteskip
> !forceskip - Forces a skip
!loop - Loops the current song
!loopqueue - Loops the queue
!random - A random index will be picked each time
!clear - Clears the queue
!remove - Removes a track from the queue
!removedupes - Removes all duplicates from the queue

#### Informations
> !nexttrack - Info about the next track
> !nowplaying - Info about the current track
> !lasttrack - Info about the last track
> !queue - Info abt the queue
> !albuminfo - straightforward
> !artistinfo - straightforward
> !lyrics - straightforward
> !songinfo - straightforward

#### Settings
> !maxlength - Maximum track length
> !minlength - Minimum track length
> !voteskip toggle - Toggles voteskip
> !voteskip amount - Amount (%) of votes to skip
> !maxplaylistlength - straightforward
> !maxplaylisttracks - straightforward
> !maxusertracks - straightforward
> !maxuserlength - straightforward
> !textannounce toggle - Toggles text announcing
> !textannounce text - Changes text announcing, with placeholders
> !textannounce autodelete - Automatically delete the announcement after x seconds
> !defaultvolume - Sets the bot default volume
> !blacklist - Blacklists a song/author
> !unblacklist - Removes the song/author from the blacklist
> !removeafterplayed - Remove the song from the queue after playing (disables permanent queue)
 
#### Permissions
> !permissions role|user|everyone list - Lists the permissions of said group/person
> !permissions role|user|everyone set permission false|true - Changes someone permission
> - List of permissions: queue.clear; queue.remove; queue.shuffle; queue.edit; player.play; player.skip; player.insert; player.wind; player.loop; player.random; client.leave; client.volume; blacklist.add; blacklist.remove; admin.settings

#### General
> !donate - straightforward
> !leaderboard - Shows most played musics and members with most time spent with the bot

### profiles.py
!edit title|birthday|nickname|aboutme|color|image|social - Edits your profile
!profile - Shows someone profile
!rep - +reps someone, like in Steam
!nextbirthdays - Lists upcoming birthdays
!adminedit - Edits someone profile

### roles.py
> !register - Registers with given roles

### tempchannels.py
!tempchannel - Creates a tempchannel
!tcdelete - Deletes a channel
!tcadd - Adds someone to the channel
!tcremove - Removes someone from the channel
!tctranscript - Creates a transcript of all channel messages (signed)
!tcadmin - Manages a channel

### tickets.py
!ticket - Creates a ticket
!tclose - Closes a ticket
!topen - Reopens a ticket
!tdelete - Deletes a closed ticket
!tadd - Adds someone to a ticket
!tremove - Removes someone from a ticket
!trename - Renames a ticket
!ttranscript - Transcripts a ticket

### Others
> !changelog - Lists bot changelol (duh)
> !channels - Remove or add channels in which bot commands will be listened