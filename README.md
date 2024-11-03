# MonitorBlock Plugin

A Minecraft plugin that lets you watch specific blocks and get Discord alerts when they are used or destroyed.

## Table of Contents

- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
  - [Commands](#commands)
  - [Permissions](#permissions)
- [Configuration](#configuration)
- [Building from Source](#building-from-source)
- [License](#license)

---

## Features

- **Watch Blocks:** Use a special wand to choose blocks to watch.
- **Multiple Blocks:** Watch many blocks with your own names.
- **Action Types:** Notices right-clicks, left-clicks, block breaks, and explosions.
- **Discord Alerts:** Sends messages to Discord when something happens to the blocks.
- **Nice Messages:** Messages look clean and organized in Discord.

## Installation

1. **Build the Plugin:**
   - You need to build the plugin yourself from the source code, or get it from https://modrinth.com/plugin/monitorblock/versions.

2. **Place in Plugins Folder:**
   - After building, put the `jar` file into your server's `plugins` folder.

3. **Set Up the Webhook:**
   - Open the config.yml file where the webhook URL is set.
   - Find the line that has `webhookUrl` and change it to your Discord webhook URL.

4. **Start the Server:**
   - Restart your Minecraft server.
   - The plugin should now work.

## Usage

### Commands

- **`/monitorblock <name>`**
  - **What it does:** Gives you a MonitorBlock wand with the name you choose.
  - **How to use:** Type `/monitorblock baitbase1`
  - **Example:** `/monitorblock BaitBase1`

### How to Watch Blocks

1. **Get the Wand:**
   - Use the `/monitorblock <name>` command to get your wand.

2. **Choose Blocks to Watch:**
   - Hold the wand and right-click on the blocks you want to watch.
   - Each block you click will be saved with the name you gave.

3. **Use the Watched Blocks:**
   - When someone interacts with the watched blocks (like right-clicking, left-clicking, breaking, or causing an explosion), you will get a Discord message.

4. **Get Alerts:**
   - You will see a message in your Discord server with details about what happened. 
   - ![This is an example of what it should look like](https://github.com/user-attachments/assets/96c971bd-0382-40ad-811b-921f75e40ce1)


### Permissions

- **`monitorblock.use`**
  - **What it does:** Lets you use the `/monitorblock` command.
  - **Who can use it:** Server operators (ops) by default, and players with the monitor.use permission.

## Configuration

You need to set your Discord webhook URL in the config.yml.

1. **Run the server, open monitorblock folder within plugins and enter config.yml :**
   - Find the part of the code where `webhook-url` is set (within the `config.yml` file.

2. **Set the Webhook URL:**
   - Replace `YOUR_WEBHOOK_URL` value with your Discord webhook link.
   - It should look like this:
`
webhook-url: "YOUR_DISCORD_WEBHOOK_URL" `


# Note
- this is intended for minecraft 1.21.1. Join https://discord.gg/weezers and dm forniee#0000 if you'd like to request a version higher or lower.
