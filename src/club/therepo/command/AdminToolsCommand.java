package club.therepo.command;

import club.therepo.AdminTools;
import com.google.common.collect.Lists;
import club.therepo.gui.GUIManager;
import club.therepo.modules.Module;
import club.therepo.modules.ModuleLoader;
import club.therepo.util.MessageTranslator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.List;

public class AdminToolsCommand implements CommandExecutor, Listener {

    private final MessageTranslator msg;
    private final List<String> aliases;
    private final List<String> commandAliases;
    private final List<String> commandAliasesWithSlash;

    public AdminToolsCommand() {
        msg = MessageTranslator.getInstance();
        aliases = ModuleLoader.getInstance().getAliases();
        commandAliases = ModuleLoader.getInstance().getCommandAliases();
        commandAliasesWithSlash = Lists.newArrayListWithCapacity(commandAliases.size()*2);
        for(String alias : commandAliases) {
            commandAliasesWithSlash.add("/"+alias);
            commandAliasesWithSlash.add("/admintools:"+alias);
        }
        Bukkit.getPluginManager().registerEvents(this, AdminTools.getInstance());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(msg.getMessage("chatmessages.playerOnlyCommand",true, null));
            return true;
        }
        Player p = (Player) sender;
        if(!p.hasPermission("admintools.use")) {
            p.sendMessage(msg.getMessageAndReplace("chatmessages.noperm",true,p,"admintools.use"));
            return true;
        }

        label = label.toLowerCase();
        if(label.equals("admingui") || label.equals("a") || label.equals("admintools")) {
            if(args.length == 0) {
                GUIManager.getInstance().openSession(p);
            } else if(args.length == 1 && "version".equalsIgnoreCase(args[0])) {
                p.sendMessage(ChatColor.AQUA+"admintools v"+ AdminTools.getInstance().getDescription().getVersion());
                p.sendMessage(ChatColor.AQUA+"Author: Weiiswurst");
                p.sendMessage(ChatColor.AQUA+"Discord: https://discord.gg/YDkQbE7 (Support, Bugreports)");
                p.sendMessage(ChatColor.AQUA+"Wiki: https://weiiswurst.gitbook.io/admintools3/");
                p.sendMessage(ChatColor.AQUA+"This plugin uses XMaterials and bStats. More info at https://github.com/WeiiswurstDev/AdminTools3");
                p.sendMessage(ChatColor.AQUA+"Download links are on the Wiki Page as well.");
                p.sendMessage(ChatColor.GREEN+"Use /a to open the GUI. Use /a <module> [player] [world] or /<module> to execute a module directly.");
            } else if(args.length <= 3) {
                executeModule(p,args[0], Arrays.copyOfRange(args,1,args.length));
            }
        } else {
            executeModule(p,label,args);
        }

        return true;
    }

    @EventHandler
    public void aliasListener(PlayerCommandPreprocessEvent event) {
        String[] args = event.getMessage().split(" ");
        String alias = args[0].substring(1); //cut away /
        if(!commandAliases.contains(alias)) {
            return;
        }
        event.setCancelled(true);
        Player p = event.getPlayer();
        if(!p.hasPermission("admintools.use")) {
            p.sendMessage(msg.getMessageAndReplace("chatmessages.noperm",true,p,"admintools.use"));
            return;
        }
        executeModule(p, alias, Arrays.copyOfRange(args, 1, args.length));
    }

    private void executeModule(Player p, String moduleName, String[] args) {
        for(Module m : ModuleLoader.getInstance().getModuleList()) {
            if(m.getAliases().contains(moduleName)) {
                Player other = p;
                World w = null;
                if(args.length >= 1) {
                    if(m.needsPlayer())
                        for(Player x : Bukkit.getOnlinePlayers()) {
                            if(x.getName().equalsIgnoreCase(args[0])) {
                                other = x;
                                break;
                            }
                        }
                    else if(m.needsWorld()) {
                        w = Bukkit.getWorld(args[0]);
                    }
                }
                if(args.length >= 2 && m.needsPlayer() && m.needsWorld()) {
                    w = Bukkit.getWorld(args[1]);
                }
                m.execute(p,other,w);
                return;
            }
        }
        p.sendMessage(msg.getMessageAndReplace("chatmessages.moduleNotFound",true,p,moduleName));
    }
}
