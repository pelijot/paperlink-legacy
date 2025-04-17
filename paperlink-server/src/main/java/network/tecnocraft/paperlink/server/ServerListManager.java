package network.tecnocraft.paperlink.server;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerListManager {

    private final JavaPlugin plugin;
    private List<String> cachedServers = new ArrayList<>();
    private long lastUpdated = 0; // Timestamp in milliseconds clearly

    private static final long UPDATE_INTERVAL_MS = 3600 * 1000; // 1 hour

    public ServerListManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void requestServerListIfNeeded() {
        long now = System.currentTimeMillis();
        if (now - lastUpdated >= UPDATE_INTERVAL_MS || cachedServers.isEmpty()) {
            requestServerList();
        } else {
            plugin.getLogger().info("Server list recently updated; skipping update.");
        }
    }

    public void requestServerList() {
        Player player = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (player == null) {
            plugin.getLogger().warning("Cannot request server list (no online players). Will retry soon.");
            Bukkit.getScheduler().runTaskLater(plugin, this::requestServerList, 20L * 30);
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServers");

        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
        plugin.getLogger().info("Requested server list from proxy.");
    }

    public void updateCache(List<String> servers) {
        this.cachedServers = new ArrayList<>(servers);
        this.lastUpdated = System.currentTimeMillis(); // Clearly store last update time
        plugin.getLogger().info("Updated cached server list: " + servers);
    }

    public List<String> getCachedServers() {
        return Collections.unmodifiableList(cachedServers);
    }
}
