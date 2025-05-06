package network.tecnocraft.paperlink.proxy;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

@Plugin(id = "paperlink-proxy", name = "Paperlink - Proxy", version = "1.1", authors = {"Pelijot"})
public class PaperLinkProxyPlugin {

    private final ProxyServer server;

    private static final MinecraftChannelIdentifier BUNGEECORD_CHANNEL =
            MinecraftChannelIdentifier.from("bungeecord:main");

    @Inject
    public PaperLinkProxyPlugin(ProxyServer server, @DataDirectory java.nio.file.Path dataDirectory) {
        this.server = server;
        server.getChannelRegistrar().register(BUNGEECORD_CHANNEL);
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (!(event.getSource() instanceof Player player)) return;
        if (!event.getIdentifier().equals(BUNGEECORD_CHANNEL)) return;

        try {
            var data = event.dataAsDataStream();
            String action = data.readUTF();
            if (action.equals("Connect")) {
                String targetServer = data.readUTF();
                server.getServer(targetServer).ifPresentOrElse(target ->
                        player.createConnectionRequest(target).fireAndForget(), () ->
                        player.sendMessage(net.kyori.adventure.text.Component.text("Server "+targetServer+" does not exist!"))
                );
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}