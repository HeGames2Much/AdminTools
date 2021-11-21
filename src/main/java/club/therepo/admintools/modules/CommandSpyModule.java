package club.therepo.admintools.modules;

import club.therepo.admintools.AdminTools;
import club.therepo.admintools.util.PlayerDataStorage;
import club.therepo.admintools.util.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;
import java.util.UUID;

public class CommandSpyModule extends Module implements Listener {

    private final List<UUID> commandSpyEnabled;
    private final PlayerDataStorage pds;

    public CommandSpyModule() {
        super(false, false,false, "cmdspy", XMaterial.COMMAND_BLOCK);
        useDefaultMessageKeyFormat = false;
        pds = new PlayerDataStorage("commandspy.yml");
        commandSpyEnabled = pds.getAllData();
        Bukkit.getPluginManager().registerEvents(this, AdminTools.getInstance());
    }

    @Override
    public boolean execute(Player player, Player other, World world) {
        if(!super.execute(player, other, world)) {
            return false;
        }
        if(commandSpyEnabled.contains(player.getUniqueId())) {
            commandSpyEnabled.remove(player.getUniqueId());
            pds.getConfig().set(player.getUniqueId().toString(),false);
            player.sendMessage(msg.getMessage("module.cmdspy.message.toggleOff",true,player));
        } else {
            commandSpyEnabled.add(player.getUniqueId());
            pds.getConfig().set(player.getUniqueId().toString(),true);
            player.sendMessage(msg.getMessage("module.cmdspy.message.toggleOn",true,player));
        }
        pds.save();
        return true;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        for(UUID u : commandSpyEnabled) {
            Player player = Bukkit.getPlayer(u);
            if(player == null) continue;
            player.sendMessage(msg.getMessageAndReplace("module.cmdspy.message.commandUsed",false,event.getPlayer(),event.getPlayer().getName(),event.getMessage()));
        }
    }
}