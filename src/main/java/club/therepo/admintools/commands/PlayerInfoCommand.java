package club.therepo.admintools.commands;

import club.therepo.admintools.AdminTools;
import club.therepo.admintools.util.MessageTranslator;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerInfoCommand implements CommandExecutor {

    private final MessageTranslator msg;
    private final List<String> lines;

    private final Economy economy;
    private final boolean papiInstalled;

    public PlayerInfoCommand() {
        msg = MessageTranslator.getInstance();
        papiInstalled = msg.isPapiEnabled();
        lines = msg.getConfiguration().getStringList("playerInfo.lines");

        Logger logger = AdminTools.getInstance().getLogger();

        if(!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            economy = null;
            logger.log(Level.WARNING, msg.getMessage("chatmessages.vaultNotFound"));
        } else {
            RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
            if(rsp == null) {
                economy = null;
                logger.log(Level.WARNING, msg.getMessage("chatmessages.economyNotFound"));
            } else {
                economy = rsp.getProvider();
                logger.log(Level.INFO, msg.getMessageAndReplace("chatmessages.economyFound",false,null,economy.getName()));
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player senderPlayer = null;
        if(sender instanceof  Player) {
            senderPlayer = (Player) sender;
        }
        if (!sender.hasPermission("admintools.playerInfo")) {
            sender.sendMessage(msg.getMessageAndReplace("chatmessages.noperm", true, senderPlayer,"admintools.playerInfo"));
            return true;
        }

        if(args.length != 1) {
            sender.sendMessage(msg.getMessageAndReplace("chatmessages.syntax",true,senderPlayer,"/pinfo <name>"));
            return true;
        }
        Player player = getPlayerByName(args[0]);
        if(player == null) {
            sender.sendMessage(msg.getMessage("chatmessages.missingPlayer",true,senderPlayer));
            return true;
        }

        for(String line : lines) {
            if(!papiInstalled)
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',line
                        .replaceAll("%player_name%", player.getName())
                        .replaceAll("%player_displayname%", player.getDisplayName())
                        .replaceAll("%player_uuid%", player.getUniqueId().toString())
                        .replaceAll("%player_health%", player.getHealth() + "/" + player.getAttribute(Attribute.GENERIC_MAX_HEALTH))
                        .replaceAll("%player_food_level%", player.getFoodLevel() + "/20")
                        .replaceAll("%player_item_in_hand%", player.getInventory().getItemInMainHand().getType().toString())
                        .replaceAll("%player_gamemode%", ChatColor.stripColor(msg.getMessage("gui.gamemodeSelector." + player.getGameMode().toString().toLowerCase())))));
            else
                sender.sendMessage(PlaceholderAPI.setPlaceholders(player,line));
        }
        if(economy != null) {
            sender.sendMessage(msg.getMessageAndReplace("playerInfo.optionalBalance",false, player,economy.getBalance(player)+""));
        }

        return true;
    }

    private Player getPlayerByName(String name) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.getName().equalsIgnoreCase(name)) return player;
        }
        return null;
    }
}
