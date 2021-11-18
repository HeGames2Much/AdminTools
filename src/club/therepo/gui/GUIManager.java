package club.therepo.gui;

import com.google.common.collect.Maps;
import club.therepo.util.MessageTranslator;
import club.therepo.modules.Module;
import club.therepo.modules.ModuleLoader;
import club.therepo.util.GUIBuilder;
import club.therepo.util.ItemBuilder;
import club.therepo.util.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Map;
import java.util.UUID;

public class GUIManager {

    private Map<UUID, GUISession> sessions;
    private final MessageTranslator msg;
    private final ModuleLoader modules;

    private static GUIManager INSTANCE;

    private final String nopermLore, moduleSelectorInvName, clickInfoApplyOnYourself, clickInfoApplyOnOthersAsWell,
            playerSelectorInvName, playerSelectorItemLore,
            worldSelectorInvName, worldSelectorItemLore;

    public GUIManager() {
        INSTANCE = this;

        msg = MessageTranslator.getInstance();
        modules = ModuleLoader.getInstance();

        nopermLore = msg.getMessage("gui.moduleSelector.noPermLore");
        moduleSelectorInvName = msg.getMessage("gui.moduleSelector.invName");
        clickInfoApplyOnYourself = " ##"+msg.getMessage("gui.moduleSelector.mayOnlyChooseSelf");
        clickInfoApplyOnOthersAsWell = " ##"+msg.getMessage("gui.moduleSelector.mayChooseOtherPlayer");
        playerSelectorInvName = msg.getMessage("gui.playerSelector.invName");
        playerSelectorItemLore = msg.getMessage("gui.playerSelector.itemLore");
        worldSelectorInvName = msg.getMessage("gui.worldSelector.invName");
        worldSelectorItemLore = msg.getMessage("gui.worldSelector.itemLore");

        sessions = Maps.newHashMap();
    }

    public static GUIManager getInstance() {
        return INSTANCE;
    }

    public void openSession(Player p) {
        if(sessions.containsKey(p.getUniqueId())) {
            return;
        }
        sessions.put(p.getUniqueId(), new GUISession(p));
        p.openInventory(generateMenu(p));
    }

    public void closeSession(Player p) {
        if(sessions.containsKey(p.getUniqueId())) {
            p.closeInventory();
            sessions.remove(p.getUniqueId());
        }
    }

    public Inventory generateMenu(Player p) {
        GUIBuilder menu = new GUIBuilder(moduleSelectorInvName, modules.getModuleList().size()/9+1);
        menu.fill(ItemBuilder.WHITEPANE);
        for(int slot = 0, length = modules.getModuleList().size(); slot < length; slot++) {
            Module m = modules.getModuleList().get(slot);
            ItemBuilder item;
            if(p.hasPermission("admintools3.module."+m.getName()) || p.hasPermission("admintools3.module."+m.getName()+".self")) {
                item = new ItemBuilder(XMaterial.matchXMaterial(m.getMaterial()));
            } else {
                item = new ItemBuilder(XMaterial.BARRIER);
                item.addLore(nopermLore);
            }
            item.setName(m.getItemname());
            item.addLore(m.getItemlore());

            if(m.needsPlayer() && p.hasPermission("admintools3.module."+m.getName())) {
                item.addLore(clickInfoApplyOnOthersAsWell);
            } else {
                item.addLore(clickInfoApplyOnYourself);
            }

            menu.setItem(slot, item.build());
        }
        return menu.build();
    }

    @SuppressWarnings("deprecation")
    public Inventory generatePlayerSelector(Player p) {
        int rows = Bukkit.getOnlinePlayers().size()/9+1;
        if(rows > 6) {
            rows = 6;
            p.sendMessage(msg.getMessageAndReplace("chatmessages.tooManyPlayers",true, p,Bukkit.getOnlinePlayers().size()+""));
        }

        GUIBuilder menu = new GUIBuilder(playerSelectorInvName, rows);
        menu.fill(ItemBuilder.WHITEPANE);
        Player[] onlineList = new Player[Bukkit.getOnlinePlayers().size()];
        onlineList = Bukkit.getOnlinePlayers().toArray(onlineList);
        for(int slot = 0; slot < onlineList.length; slot++) {
            Player x = onlineList[slot];
            ItemStack i = new ItemBuilder(XMaterial.PLAYER_HEAD, x.getName()).addLore(playerSelectorItemLore).build();
            SkullMeta meta = (SkullMeta) i.getItemMeta();
            if(XMaterial.isNewVersion()) { // current
                meta.setOwningPlayer(x);
            } else { // legacy
                meta.setOwner(x.getName());
            }
            i.setItemMeta(meta);
            menu.setItem(slot, i);
        }
        return menu.build();
    }

    public Inventory generateWorldSelector(Player p) {
        int rows= Bukkit.getWorlds().size()/9+1;
        if(rows > 6) {
            rows = 6;
            p.sendMessage(msg.getMessageAndReplace("chatmessages.tooManyWorlds",true, p,Bukkit.getWorlds().size()+""));
        }

        GUIBuilder menu = new GUIBuilder(worldSelectorInvName, rows);
        menu.fill(ItemBuilder.WHITEPANE);
        for(int slot = 0; slot < Bukkit.getWorlds().size(); slot++) {
            World w = Bukkit.getWorlds().get(slot);
            XMaterial m = XMaterial.GRASS;
            if(w.getName().contains("nether")) m = XMaterial.NETHERRACK;
            else if(w.getName().contains("the_end")) m = XMaterial.END_STONE;
            menu.setItem(slot, new ItemBuilder(m, ChatColor.GREEN+w.getName()).addLore(worldSelectorItemLore).build());
        }
        return menu.build();
    }
}