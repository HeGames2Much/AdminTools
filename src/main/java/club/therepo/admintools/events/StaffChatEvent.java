package club.therepo.admintools.events;

import club.therepo.admintools.util.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class StaffChatEvent implements Listener {

    private final String format, prefix;

    public StaffChatEvent() {
        format = ChatColor.translateAlternateColorCodes('&', Configuration.get().getString("staffchat.format"));
        prefix = ChatColor.translateAlternateColorCodes('&',Configuration.get().getString("staffchat.prefix")).toLowerCase();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if(!event.getPlayer().hasPermission("admintools.staffchat.send")) {
            return;
        }
        if(!event.getMessage().toLowerCase().startsWith(prefix)) {
            return;
        }
        String message = event.getMessage().replaceAll(prefix,"");
        message = format.replaceAll("%message%",message).replaceAll("%player%",event.getPlayer().getName());
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p.hasPermission("admintools.staffchat.receive"))
                p.sendMessage(message);
        }
        event.setCancelled(true);
    }

}
