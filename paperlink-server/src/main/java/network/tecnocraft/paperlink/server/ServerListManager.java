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
    private long lastUpdated = 0;

    private final long updateIntervalMs;
    private final long retryNoPlayerTicks;

    public ServerListManager(JavaPlugin plugin, int updateIntervalMinutes, int retryNoPlayerMinutes) {
        this.plugin = plugin;
        this.updateIntervalMs = updateIntervalMinutes < 0 ? -1 : updateIntervalMinutes * 60L * 1000L;
        this.retryNoPlayerTicks = retryNoPlayerMinutes < 0 ? -1 : retryNoPlayerMinutes * 60L * 20L;
    }

    public void requestServerListIfNeeded() {
        if (updateIntervalMs == -1) {
            plugin.getLogger().info("Update interval is disabled (-1). Skipping server list update.");
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastUpdated >= updateIntervalMs || cachedServers.isEmpty()) {
            requestServerList();
        } else {
            plugin.getLogger().info("Server list recently updated; skipping update.");
        }
    }

    public void requestServerList() {
        Player player = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (player == null) {
            plugin.getLogger().warning("Cannot request server list (no online players)." +
                    (retryNoPlayerTicks != -1 ? " Will retry soon." : " Retrying is disabled."));
            if (retryNoPlayerTicks != -1) {
                Bukkit.getScheduler().runTaskLater(plugin, this::requestServerList, retryNoPlayerTicks);
            }
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServers");

        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
        plugin.getLogger().info("Requested server list from proxy.");
    }

    public void updateCache(List<String> servers) {
        this.cachedServers = new ArrayList<>(servers);
        this.lastUpdated = System.currentTimeMillis();
        plugin.getLogger().info("Updated cached server list: " + servers);
    }

    public List<String> getCachedServers() {
        return Collections.unmodifiableList(cachedServers);
    }
} 