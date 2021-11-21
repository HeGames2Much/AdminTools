package club.therepo.admintools.modules;

import club.therepo.admintools.util.Configuration;
import club.therepo.admintools.util.MessageTranslator;
import club.therepo.admintools.util.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Module {

    protected final boolean needsWorld, needsPlayer, needsPlayerOnly;
    protected final String name;
    protected final Material material;

    protected String permissionBase, permissionSelf;

    protected String itemname, itemlore;

    protected int cooldown;
    protected final Map<UUID, Long> onCooldown;

    protected final MessageTranslator msg;

    protected boolean useDefaultMessageKeyFormat = true;
    protected boolean registerCommands;
    protected boolean showMessageToTarget;

    protected List<String> aliases;

    protected Module(boolean needsWorld, boolean needsPlayer, boolean needsPlayerOnly, String name, XMaterial material) {
        this.needsWorld = needsWorld;
        this.needsPlayer = needsPlayer;
        this.needsPlayerOnly = needsPlayerOnly;
        this.name = name;
        this.material = material.parseMaterial();

        this.permissionBase = "admintools.module."+name;
        this.permissionSelf = permissionBase+".self";

        this.msg = MessageTranslator.getInstance();

        this.itemname = msg.getMessage("module."+name+".item.name");
        this.itemlore = msg.getMessage("module."+name+".item.lore");

        this.cooldown = Configuration.get().getInt("module."+name+".cooldown",0);
        this.onCooldown = new HashMap<>();

        this.registerCommands = Configuration.get().getBoolean("module."+name+".registerCommands",true);
        this.showMessageToTarget = Configuration.get().getBoolean("module."+name+".showMessageToTarget",true);

        aliases = Configuration.get().getStringList("module."+name+".aliases");
        aliases.add(name);
    }

    public boolean execute(Player player, Player other, World world) {
        if(!player.hasPermission(permissionBase)) {
            if(player.hasPermission(permissionSelf)) {
                if(player.getUniqueId() != other.getUniqueId()) {
                    player.sendMessage(msg.getMessageAndReplace("chatmessages.noperm",true,player,permissionBase));
                    return false;
                }
            } else {
                if(player.getUniqueId() != other.getUniqueId()) {
                    player.sendMessage(msg.getMessageAndReplace("chatmessages.noperm",true,player,permissionBase));
                } else {
                    player.sendMessage(msg.getMessageAndReplace("chatmessages.noperm",true,player,permissionSelf));
                }
                return false;
            }
        }
        if(needsWorld && world == null) {
            player.sendMessage(msg.getMessage("chatmessages.missingWorld", true, player));
            return false;
        }

        if(needsPlayer && (other == null || !other.isOnline())) {
            player.sendMessage(msg.getMessage("chatmessages.missingPlayer", true, player));
            return false;
        }

        if(needsPlayerOnly && (other == null || !other.isOnline())) {
            player.sendMessage(msg.getMessage("chatmessages.missingPlayer", true, player));
            return false;
        }

        if(cooldown > 0 && !player.hasPermission("admintools.bypass.cooldown") && !player.hasPermission("admintools.bypass.cooldown."+name)) {
            if(onCooldown.containsKey(player.getUniqueId())) {
                if(Instant.now().isBefore(Instant.ofEpochSecond(onCooldown.get(player.getUniqueId())))) {
                    player.sendMessage(msg.getMessage("chatmessages.onCooldown", true, player));
                    return false;
                }
            }
            onCooldown.put(player.getUniqueId(), Instant.now().getEpochSecond());
        }
        if(useDefaultMessageKeyFormat) {
            if(needsPlayer && player.getUniqueId() != other.getUniqueId()) {
                if(showMessageToTarget) {
                    other.sendMessage(msg.getMessageAndReplace("module." + name + ".message.appliedByOther", true, player, player.getName()));
                }
                player.sendMessage(msg.getMessageAndReplace("module."+name+".message.applyToOther", true,player, other.getName()));
            } else if(needsPlayerOnly && player.getUniqueId() != other.getUniqueId()) {
                player.sendMessage(msg.getMessageAndReplace("module."+name+".message.applyToOther", true,player, other.getName()));
            } else {
                player.sendMessage(msg.getMessage("module." + name + ".message.applyToSelf", true, player));
            }
        }

        if(Configuration.get().getBoolean("log-module-usage")) {
            String logmessage = "[LOG] Player "+player.getName()+" used the module "+name;
            if(other != null) logmessage += " on the player "+other.getName();
            if(world != null) logmessage += " in the world "+world.getName();
            Bukkit.getConsoleSender().sendMessage(logmessage);
        }
        return true;
    }

    public boolean needsWorld() {
        return needsWorld;
    }

    public boolean needsPlayer() {
        return needsPlayer;
    }

    public boolean needsPlayerOnly() {
        return needsPlayerOnly;
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public String getItemname() {
        return itemname;
    }

    public String getItemlore() {
        return itemlore;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public boolean shouldRegisterCommands() {
        return registerCommands;
    }
}