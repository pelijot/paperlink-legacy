package network.tecnocraft.paperlink.server;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.Arrays;
import java.util.List;

public class PaperLinkServerPlugin extends JavaPlugin implements PluginMessageListener {

    private ServerListManager serverListManager;

    @Override
    public void onEnable() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);

        saveDefaultConfig();
        int updateMinutes = getConfig().getInt("update-interval-minutes", 60);
        int retryMinutes = getConfig().getInt("retry-no-player-minutes", 1);

        this.serverListManager = new ServerListManager(this, updateMinutes, retryMinutes);

        ProxyCommand cmd = new ProxyCommand(this, serverListManager);
        this.getCommand("proxycommand").setExecutor(cmd);
        this.getCommand("proxycommand").setTabCompleter(cmd);

        Bukkit.getScheduler().runTaskLater(this, serverListManager::requestServerList, 20L * 10);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();

        if (subchannel.equals("GetServers")) {
            String serverListStr = in.readUTF();
            List<String> servers = Arrays.asList(serverListStr.split(", "));
            serverListManager.updateCache(servers);
        }
    }

    public ServerListManager getServerListManager() {
        return serverListManager;
    }
}
