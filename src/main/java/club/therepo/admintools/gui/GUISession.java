package club.therepo.admintools.gui;

import club.therepo.admintools.AdminTools;
import club.therepo.admintools.modules.Module;
import club.therepo.admintools.modules.ModuleLoader;
import club.therepo.admintools.util.MessageTranslator;
import club.therepo.admintools.util.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.meta.SkullMeta;

public class GUISession implements Listener {

    private Player player;

    private Module selected;

    private Player selectedPlayer;
    private World selectedWorld;

    private boolean closed = false;

    public GUISession(Player player) {
        this.player = player;
        Bukkit.getServer().getPluginManager().registerEvents(this, AdminTools.getInstance());
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(closed) return;
        if(e.getWhoClicked().getUniqueId() != player.getUniqueId()) return;
        e.setCancelled(true);

        if(e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) return;

        if(MessageTranslator.getInstance().getMessage("gui.moduleSelector.invName",false,player).equals(e.getView().getTitle())) {
            for(Module m : ModuleLoader.getInstance().getModuleList()) {
                if(e.getCurrentItem().getItemMeta().getDisplayName().equals(m.getItemname())) {
                    selected = m;

                    // Linksclick - Auf DICH ausführen
                    if(e.getAction() == InventoryAction.COLLECT_TO_CURSOR || e.getAction() == InventoryAction.PICKUP_ALL) {
                        if(player.hasPermission("admintools3.module."+m.getName()) || player.hasPermission("admintools3.module."+m.getName()+".self")){
                            selectedPlayer = player;
                            if(selected.needsWorld()) {
                                closed = true;
                                player.openInventory(GUIManager.getInstance().generateWorldSelector(player));
                                closed = false;
                            } else {
                                player.closeInventory();
                                selected.execute(player, selectedPlayer, null);
                            }
                        } else {
                            player.sendMessage(MessageTranslator.getInstance().getMessageAndReplace("chatmessages.noperm",true,player,"admintools3.module."+m.getName()+".self"));
                            player.closeInventory();
                        }
                        // Rechtsclick - Auf ANDERE ausführen
                    } else if(e.getAction() == InventoryAction.PICKUP_HALF) {
                        if(player.hasPermission("admintools3.module."+m.getName())) {
                            closed = true;
                            player.openInventory(GUIManager.getInstance().generatePlayerSelector(player));
                            closed = false;
                        } else {
                            //player.sendMessage(MessageTranslator.getInstance().getMessageAndReplace("chatmessages.noperm",true,player,"admintools3.module."+m.getName()));
                            player.closeInventory();
                            selected.execute(player, player, null);
                        }
                    }
                }
            }
        } else if(MessageTranslator.getInstance().getMessage("gui.playerSelector.invName",false,player).equals(e.getView().getTitle())) {
            if(e.getCurrentItem().getType() != XMaterial.PLAYER_HEAD.parseMaterial()) return;
            SkullMeta clickedMeta = (SkullMeta) e.getCurrentItem().getItemMeta();
            selectedPlayer = getFromSkull(clickedMeta); // legacy compatibility
            if(selected.needsWorld()) {
                closed = true;
                player.openInventory(GUIManager.getInstance().generateWorldSelector(player));
                closed = false;
            } else {
                player.closeInventory();
                selected.execute(player,selectedPlayer,null);
            }
        } else if(MessageTranslator.getInstance().getMessage("gui.worldSelector.invName",false,player).equals(e.getView().getTitle())) {
            String name = e.getCurrentItem().getItemMeta().getDisplayName();
            for(World w : Bukkit.getWorlds()) {
                if(w.getName().equals(name.substring(2))) {
                    selectedWorld = w;
                    player.closeInventory();
                    selected.execute(player,selectedPlayer,selectedWorld);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if(closed) return;
        if(e.getPlayer().getUniqueId() == player.getUniqueId()) {
            closed = true;
            GUIManager.getInstance().closeSession(player);
        }
    }

    @SuppressWarnings("deprecation")
    private Player getFromSkull(SkullMeta meta) {
        if(XMaterial.isNewVersion()) {
            return Bukkit.getPlayer(meta.getOwningPlayer().getUniqueId());
        } else { // legacy
            return Bukkit.getPlayer(meta.getOwner());
        }
    }
}