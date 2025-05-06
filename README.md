# paperlink-legacy

A plugin for paper and velocity that allows you to send commands to your proxy via a command block.

## Usage

Use the command `proxycommand "[command]"` via command blocks or other
command sources to send it to the proxy.

For example, you can teleport players to a different server with a command block using
```
execute as @p run proxycommand "server SERVERNAME"
```

The plugin automatically tries to fetch the current available servers. To change the intervall or turn it of look at the automatically generated config.yml

**Current supported commands:**
`/server`

## Updates

This plugin has only basic functionality and limited updates (Legacy version). A fully fledged out version of this plugin is in the works.

### Credits

This project was heavily inspired by [michiruf/MCProxyCommand](https://github.com/michiruf/MCProxyCommand) and [KosmX/player_mover_velocity](https://github.com/KosmX/player_mover_velocity), although almost completely **written by ChatGPT**.
