// ProxyCommand.java
package network.tecnocraft.paperlink.server;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class ProxyCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final ServerListManager serverListManager;

    public ProxyCommand(JavaPlugin plugin, ServerListManager serverListManager) {
        this.plugin = plugin;
        this.serverListManager = serverListManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Usage: /proxycommand server <server-name> [player-selector/player-name]");
            return true;
        }

        String subCmd = args[0].toLowerCase();
        if (!subCmd.equals("server")) {
            sender.sendMessage("Unknown subcommand.");
            return true;
        }

        String targetServer = args[1];

        // Clearly use cached servers for validation
        if (!serverListManager.getCachedServers().contains(targetServer)) {
            sender.sendMessage("Server '" + targetServer + "' not found!");
            sender.sendMessage("Available servers: " + serverListManager.getCachedServers());
            return true;
        }

        Collection<? extends Player> targetedPlayers;

        if (args.length >= 3) {
            String selector = args[2];
            if (selector.startsWith("@")) {
                try {
                    targetedPlayers = Bukkit.selectEntities(sender, selector).stream()
                            .filter(entity -> entity instanceof Player)
                            .map(entity -> (Player) entity)
                            .toList();
                } catch (IllegalArgumentException e) {
                    sender.sendMessage("Invalid selector syntax.");
                    return true;
                }
            } else {
                Player p = Bukkit.getPlayerExact(selector);
                if (p == null) {
                    sender.sendMessage("Player not found!");
                    return true;
                }
                targetedPlayers = List.of(p);
            }
        } else {
            if (sender instanceof Player playerSender) {
                targetedPlayers = List.of(playerSender);
            } else {
                sender.sendMessage("Console must specify a target player!");
                return true;
            }
        }

        if (targetedPlayers.isEmpty()) {
            sender.sendMessage("No matching players found!");
            return true;
        }

        for (Player p : targetedPlayers) {
            sendPlayerToServer(p, targetServer);
        }

        sender.sendMessage("Sent " + targetedPlayers.size() + " player(s) to server " + targetServer + ".");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return switch (args.length) {
            case 1 -> List.of("server");
            case 2 -> serverListManager.getCachedServers(); // Clearly use cached servers here
            case 3 -> {
                List<String> selectors = List.of("@p", "@a", "@r", "@s");
                List<String> playerNames = Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .toList();
                yield Stream.concat(selectors.stream(), playerNames.stream()).toList();
            }
            default -> List.of();
        };
    }

    private void sendPlayerToServer(Player player, String server) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);

            out.writeUTF("Connect");
            out.writeUTF(server);

            player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
