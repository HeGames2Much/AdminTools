package club.therepo.admintools.modules;

import club.therepo.admintools.AdminTools;
import club.therepo.admintools.util.PlayerDataStorage;
import club.therepo.admintools.util.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.UUID;

public class MuteModule extends Module implements Listener {

    private final List<UUID> mutedPlayers;
    private final PlayerDataStorage pds;

    public MuteModule() {
        super(false, true, "mute", XMaterial.RED_TERRACOTTA);
        useDefaultMessageKeyFormat = false;
        pds = new PlayerDataStorage("muted.yml");
        mutedPlayers = pds.getAllData();

        Bukkit.getPluginManager().registerEvents(this, AdminTools.getInstance());
    }

    @Override
    public boolean execute(Player player, Player other, World world) {
        if(other.hasPermission("admintools.bypass.mute")) {
            player.sendMessage(msg.getMessageAndReplace("module.mute.message.bypass",true,player, other.getName()));
            return false;
        }
        if(!super.execute(player, other, world)) {
            return false;
        }
        if(mutedPlayers.contains(other.getUniqueId())) {
            mutedPlayers.remove(other.getUniqueId());
            pds.getConfig().set(other.getUniqueId().toString(),false);
            other.sendMessage(msg.getMessage("module.mute.message.toggleOff",true,other));
            if(!other.getName().equals(player.getName())) {
                other.sendMessage(msg.getMessageAndReplace("module.mute.message.toggledOffByOther",true, player,player.getName()));
                player.sendMessage(msg.getMessageAndReplace("module.mute.message.toggledOffForOther",true, player,other.getName()));
            }
        } else {
            mutedPlayers.add(other.getUniqueId());
            pds.getConfig().set(other.getUniqueId().toString(),true);
            other.sendMessage(msg.getMessage("module.mute.message.toggleOn",true));
            if(!other.getName().equals(player.getName())) {
                other.sendMessage(msg.getMessageAndReplace("module.mute.message.toggledOnByOther", true, player,player.getName()));
                player.sendMessage(msg.getMessageAndReplace("module.mute.message.toggledOnForOther",true, player,other.getName()));
            }
        }
        return true;
    }

    @EventHandler
    public void chat(AsyncPlayerChatEvent event) {
        if(mutedPlayers.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(msg.getMessage("module.mute.message.stillMuted",true, event.getPlayer()));
        }
    }

}