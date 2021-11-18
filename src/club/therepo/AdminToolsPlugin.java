package club.therepo;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public class AdminToolsPlugin {
    public final AdminTools plugin;

    AdminToolsPlugin(AdminTools plugin) {
        this.plugin = plugin;
    }

    void enable() {
        plugin.getLogger().info("AdminTools is starting up!");
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveDefaultConfig();
    }

    void disable() {
        plugin.getLogger().info("AdminTools is shutting down!");
        HandlerList.unregisterAll(plugin);
        Bukkit.getScheduler().cancelTasks(plugin);
        plugin.getLogger().info("Goodbye!");
    }
}