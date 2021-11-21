package club.therepo.admintools;

import club.therepo.admintools.commands.AdminToolsCommand;
import club.therepo.admintools.commands.PlayerInfoCommand;
import club.therepo.admintools.database.DataBaseManager;
import club.therepo.admintools.database.SQLite;
import club.therepo.admintools.util.*;
import com.google.common.collect.Lists;
import club.therepo.admintools.events.AdminChatEvent;
import club.therepo.admintools.events.JoinQuitEvent;
import club.therepo.admintools.events.StaffChatEvent;
import club.therepo.admintools.gui.GUIManager;
import club.therepo.admintools.modules.ModuleLoader;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Level;

public final class AdminTools extends JavaPlugin {

    public static AdminToolsPlugin plugin;

    private static AdminTools INSTANCE;

    public static final String PLUGIN_NAME = "Admintools";

    private List<PlayerDataStorage> toSave;
    DataBaseManager database;

    @Override
    public void onEnable() {
        //Fired when the server starts and enables all plugins
        plugin = new AdminToolsPlugin(this);

        AdminTools.INSTANCE = this;
        getDataFolder().mkdirs();
        Configuration.setup();
        toSave = Lists.newArrayList();
        new MessageTranslator(Configuration.get().getString("language"));
        new ModuleLoader();
        new GUIManager();

        getCommand("admingui").setExecutor(new AdminToolsCommand());
        getCommand("playerinfo").setExecutor(new PlayerInfoCommand());

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new AdminChatEvent(), this);
        pm.registerEvents(new StaffChatEvent(), this);

        if(Configuration.get().getBoolean("join-leave-messages.replace-messages")) {
            pm.registerEvents(new JoinQuitEvent(), this);
        }

        if(!pm.isPluginEnabled("ProtocolLib")) {
            plugin.plugin.getLogger().log(Level.WARNING, MessageTranslator.getInstance().getMessage("chatmessages.protocolLibNotFound"));
        }
        if(!XMaterial.isNewVersion()) {
            plugin.plugin.getLogger().log(Level.INFO, MessageTranslator.getInstance().getMessage("chatmessages.legacyVersion"));
        }

        Metrics metrics = new Metrics(this, 13354);

        metrics.addCustomChart(new Metrics.SimplePie("used_language", () -> Configuration.get().getString("language", "en")));

        metrics.addCustomChart(new Metrics.SimplePie("custom_join_and_leave_messages", () -> Configuration.get().getString("join-leave-messages.replace-messages", "false")));

        UpdateChecker.init(this, 97712).requestUpdateCheck().whenComplete((result, exception) -> {
            if (result.requiresUpdate()) {
                this.getLogger().info(String.format("An update is available! %s %s may be downloaded on SpigotMC", plugin.plugin.getName(), result.getNewestVersion()));
                return;
            }

            UpdateChecker.UpdateReason reason = result.getReason();
            if (reason == UpdateChecker.UpdateReason.UP_TO_DATE) {
                this.getLogger().info(String.format("Your version of %s (%s) is up to date!", plugin.plugin.getName(), result.getNewestVersion()));
            } else if (reason == UpdateChecker.UpdateReason.UNRELEASED_VERSION) {
                this.getLogger().info(String.format("Your version of %s (%s) is more recent than the one publicly available. Are you on a development build?", plugin.plugin.getName(), result.getNewestVersion()));
            } else {
                this.getLogger().warning(String.format("Could not check for a new version of %s. Reason: %s", plugin.plugin.getName(), reason));
            }
        });
        this.database = new SQLite(this);
        this.database.load();

        plugin.enable();
    }

    @Override
    public void onDisable() {
        //Fired when the server stops and disables all plugins

        for(PlayerDataStorage pds : toSave) {
            pds.save();
        }
        plugin.plugin.getLogger().log(Level.INFO, "AdminTools was disabled.");

        plugin.disable();
    }

    public DataBaseManager getDatabase() { return this.database; }

    public static AdminTools getInstance() {
        return INSTANCE;
    }

    public String getConfigFolderPath() {
        return "plugins//AdminTools";
    }

    public void addDataStorage(PlayerDataStorage pds) {
        toSave.add(pds);
    }
}
