# JHyperBot (Pre-Alpha)
JHyperBot is a Discord Bot firstly written by [me](github.com/eduardogott/) in Python, now being ported to Java. It's goal is to have the maximum amount of useful and 4fun commands, while still retaining it's simplicity and beauty.
> For a list of commands and features, head to [COMMANDS](COMMANDS.MD) 

## Contents
- [Configuration](#configuration)
- [Running](#running)
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
5. You can then get the invite either running the bot and copying it from info.txt in [logs](./src/logs), ~~running the bot with `--invite`~~ or going in OAuth2 in the applications portal, marking "bot" and below marking the wanted permissions (usually, Administrator).

> [!CAUTION]
> Do NOT share your token with anyone, nor upload it to GitHub or repl.it. ANYONE with your token can run the bot in your place, as well as edit it's properties.
> If the token gets public, IMMEDIATELY change it, because if the bot has the right permissions, someone with access can easily do a ban wave in all servers the bot is in.

### Running
#### Windows
Run it using executing run.bat or running `mvn exec:java -e` on cmd.
> [!INFO] if you want the log messages to be color coded in the console, you have to run it with `-DLOG4J_SKIP_JANSI=false`!
#### Linux/macOS/WSL
~~Run it using executing run.sh or running `mvn exec:java -e` on terminal.~~

> [!WARNING]
> Not tested on Linux, WSL nor MacOS.
### Contribution
`Can I contribute with the project?`

**Absolutely!** You are invited to contribute and have your name in the contributors below! Feel free to work on it the way you want, as it is under [MIT License](./LICENSE).
