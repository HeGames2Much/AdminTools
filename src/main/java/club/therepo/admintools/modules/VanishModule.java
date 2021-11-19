package club.therepo.admintools.modules;

import club.therepo.admintools.AdminTools;
import club.therepo.admintools.util.Configuration;
import club.therepo.admintools.util.PlayerDataStorage;
import club.therepo.admintools.util.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.UUID;

public class VanishModule extends Module implements Listener {

    private final List<UUID> vanishedPlayers;
    private final AdminTools plugin;
    private final PlayerDataStorage pds;

    private final String joinMessage, leaveMessage;

    public VanishModule() {
        super(false, true, "vanish", XMaterial.POTION);
        useDefaultMessageKeyFormat = false;
        plugin = AdminTools.getInstance();
        pds = new PlayerDataStorage("vanished.yml");
        vanishedPlayers = pds.getAllData();

        joinMessage = ChatColor.translateAlternateColorCodes('&', Configuration.get().getString("join-leave-messages.join"));
        leaveMessage = ChatColor.translateAlternateColorCodes('&',Configuration.get().getString("join-leave-messages.leave"));

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean execute(Player player, Player other, World world) {
        if(!super.execute(player, other, world)) {
            return false;
        }
        if(vanishedPlayers.contains(other.getUniqueId())) {
            vanishedPlayers.remove(other.getUniqueId());
            pds.getConfig().set(other.getUniqueId().toString(),false);
            other.sendMessage(msg.getMessage("module.vanish.message.toggleOff",true, player));

            for(Player p : Bukkit.getOnlinePlayers()) {
                if(!p.canSee(other)) {
                    p.showPlayer(plugin, other);
                    p.sendMessage(joinMessage.replaceAll("%s",other.getName()));
                }
            }

        } else {
            vanishedPlayers.add(other.getUniqueId());
            pds.getConfig().set(other.getUniqueId().toString(),true);
            other.sendMessage(msg.getMessage("module.vanish.message.toggleOn",true,player));

            for(Player p : Bukkit.getOnlinePlayers()) {
                if(!p.hasPermission("admintools.vanish.bypass")) {
                    p.hidePlayer(plugin, other);
                    p.sendMessage(leaveMessage.replaceAll("%s",other.getName()));
                }
            }
        }
        return true;
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {
        if(event.getPlayer().hasPermission("admintools.bypass.vanish")) return;

        for(UUID vanished : vanishedPlayers) {
            Player vanishedPlayer = Bukkit.getPlayer(vanished);
            if(vanishedPlayer != null) event.getPlayer().hidePlayer(plugin, vanishedPlayer);
        }
    }

}