# JHyperBot (Pre-Alpha)
JHyperBot is a Discord Bot firstly written by [me](https://github.com/eduardogott/) in Python, now being written in Java using [Javacord](https://github.com/Javacord/Javacord). It's goal is to have the maximum amount of useful and 4fun commands, while still retaining it's simplicity and beauty.

> [!IMPORTANT]
> For a list of commands and features, head to [commands](COMMANDS.MD).
> In the bot's current state, it shall only run in ONE guild per instance (token). If it runs in more than one, features WILL break.

## Contents
- [Configuration](#configuration)
- [Running](#running)
- [Observations](#observations)
- [Contribution](#contribution)

## Usage
### Configuration
*Skip to 3. if you know how to generate a bot token, and 4. if you know about intents*

1. Go to Discord's [developer portal](https://discord.com/developers/applications), and click on "New application"

2. Go on your newly created application, on the **Bot** tab generate a new token

3. Choose the intents you want the bot to have. If the intent is disabled, the bot will NOT have access to the feature.

4. In the [resources folder](./src/main/resources/) create a new file called config.properties, and paste the token in the BOT_TOKEN item.

Example (config.properties):
```properties
BOT_TOKEN=example.token-here_f2a299bca24926
```
5. You can then get the invite either running the bot and copying it from info.txt in [logs](./logs), ~~running the bot with `--invite`~~ or going in OAuth2 in the applications portal, marking "bot" and below marking the wanted permissions (usually, Administrator).

> [!TIP]
> If you want to translate or edit the bot's messages, duplicate [messages_default.json](./src/main/resources/messages_default.json), and then edit the messages. Do NOT edit or delete messages_default.json. Modifying it can render the bot unfunctional.
>
> To apply the changes, go on config.properties, create a field named "LANGUAGE", and its value should be set to the exact name of the json file. Example: `LANGUAGE=messages_default.json`

> [!CAUTION]
> Do NOT share your token with anyone, nor upload it to GitHub or repl.it. ANYONE with your token can run the bot in your place, as well as edit it's properties.
>
> If the token gets public, IMMEDIATELY change it, because if the bot has the right permissions, someone with access can easily do a ban wave in all servers the bot is in.

### Running
#### Windows
Run it using executing [run.bat](run.bat) or running `mvn exec:java -e` on cmd.
> [!NOTE]
> If you want the log messages to be color coded in the console, you might need to run it with `-DLOG4J_SKIP_JANSI=false`! Thats why run.bat exists.
#### Linux/macOS/WSL
~~Run it using executing run.sh or running `mvn exec:java -e` on terminal.~~ *run.sh not yet implemented.* 

> [!WARNING]
> Not tested on Linux, WSL nor MacOS.



## Info
### Observations
Slash commands are NOT yet implemented, and there are no plans to implement it until the port and commands to be added ([here](./progress.md)) are done.

The bot's default prefix is `!`, but it can be changed on config.properties. Add a field called PREFIX, with the wanted prefix. Example: `PREFIX="!"`

### Contribution
`Can I contribute with the project?`

**Absolutely!** You are invited to contribute and have your name in the contributors below! Feel free to work on it the way you want, as it is under [MIT License](./LICENSE).
