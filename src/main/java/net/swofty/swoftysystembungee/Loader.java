package net.swofty.swoftysystembungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public final class Loader extends Plugin implements Listener {

    private static Loader plugin;

    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerListener(this, this);
    }

    @EventHandler
    public void playerConnectEvent(PostLoginEvent event) throws IOException {

        ProxiedPlayer player = event.getPlayer();

        File userdata = new File(getDataFolder(), File.separator + "PlayerDatabase");
        File f = new File(userdata, File.separator + player.getUniqueId() + ".yml");
        if (!userdata.exists()) {
            userdata.mkdirs();
        }
        if (!f.exists()) {
            f.createNewFile();
        }

        try {
            Configuration playerData = ConfigurationProvider.getProvider(YamlConfiguration.class).load(f);

            playerData.set("connected", true);
            playerData.set("name", player.getName());

            try {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(playerData, new File(userdata, File.separator + player.getUniqueId() + ".yml"));
            } catch (Exception e) {
                getProxy().getLogger().warning("Could not save " + f.getAbsolutePath());
            }

            getProxy().getLogger().info("Successfully gave authorization to " + player.getUniqueId() + " (" + player.getName() + ")");
        } catch (FileNotFoundException e) {
            getProxy().getLogger().warning("Could not find " + f.getAbsolutePath() + " during connection");
        }
    }

    @EventHandler
    public void playerDisconnectedEvent(net.md_5.bungee.api.event.PlayerDisconnectEvent event) throws IOException {

        ProxiedPlayer player = event.getPlayer();

        File userdata = new File(getDataFolder(), File.separator + "PlayerDatabase");
        File f = new File(userdata, File.separator + player.getUniqueId() + ".yml");
        try {
            Configuration playerData = ConfigurationProvider.getProvider(YamlConfiguration.class).load(f);

            playerData.set("connected", false);
            playerData.set("name", player.getName());

            try {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(playerData, new File(userdata, File.separator + player.getUniqueId() + ".yml"));
            } catch (Exception e) {
                getProxy().getLogger().warning("Could not save " + f.getAbsolutePath());
            }
            getProxy().getLogger().info("Successfully removed authorization from " + player.getUniqueId() + " (" + player.getName() + ")");
        } catch (FileNotFoundException e) {
            getProxy().getLogger().warning("Could not find " + f.getAbsolutePath() + " during disconnect");
        }
    }
}
