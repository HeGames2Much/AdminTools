package club.therepo.admintools.modules;

import club.therepo.admintools.AdminTools;
import club.therepo.admintools.util.ItemBuilder;
import club.therepo.admintools.util.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class PunishUserModule extends Module implements Listener {

    private final String invName;
    private final Inventory punishuserSelector;

//    private final PlayerDataStorage pds;

    public static UUID punishuserList;

    public static Integer chat_offences;
    public static Integer hack_offences;
    public static Integer total_offences;

    public PunishUserModule() {
        super(false, false, true,"pu", XMaterial.BARRIER);
        useDefaultMessageKeyFormat = false;

        invName = msg.getMessage("gui.punishuserSelector.invName");
        punishuserSelector = Bukkit.createInventory(null, 54, invName);
        punishuserSelector.setItem(1,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(2,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(3,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(5,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(6,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(7,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(10,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(12,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(14,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(16,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(19,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(21,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(23,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(25,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(28,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(30,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(32,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(34,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(36,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(38,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(40,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(42,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(42,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(43,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(44,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(45,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(46,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(47,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(48,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(50,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(51,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(52,ItemBuilder.BLACKPANE);
        punishuserSelector.setItem(53,ItemBuilder.BLACKPANE);
        Bukkit.getPluginManager().registerEvents(this, AdminTools.getInstance());
    }

    @Override
    public boolean execute(Player player, Player other, World world) {
        if (other.getName().contains(player.getName())) {

            chat_offences = AdminTools.plugin.plugin.getDatabase().getOffencesChat(other.getName());

            if (chat_offences < 1) {
                chat_offences = 0;
            }

            if(!super.execute(player, other, world)) {
                return false;
            }


            ItemStack i = new ItemBuilder(XMaterial.PLAYER_HEAD, other.getName()).build();
            SkullMeta meta = (SkullMeta) i.getItemMeta();
            meta.setOwningPlayer(other);
            i.setItemMeta(meta);

            player.openInventory(punishuserSelector);
            punishuserSelector.setItem(0,new ItemBuilder(XMaterial.EMERALD, msg.getMessage("module.pu.use.0")).addLore(msg.getMessage("module.pu.lore.0")).build());
            punishuserSelector.setItem(4, i);
            punishuserSelector.setItem(8,new ItemBuilder(XMaterial.DIAMOND, msg.getMessage("module.pu.use.8")).addLore(msg.getMessage("module.pu.lore.8")).build());
            punishuserSelector.setItem(9,new ItemBuilder(XMaterial.BOOK, msg.getMessage("module.pu.use.9")).addLore(msg.getMessage("module.pu.lore.9")).build());
            punishuserSelector.setItem(11,new ItemBuilder(XMaterial.BOOK, msg.getMessage("module.pu.use.11")).addLore(msg.getMessage("module.pu.lore.11")).build());
            punishuserSelector.setItem(13,new ItemBuilder(XMaterial.BOOK, msg.getMessage("module.pu.use.13")).addLore(msg.getMessage("module.pu.lore.13")).build());
            punishuserSelector.setItem(15,new ItemBuilder(XMaterial.BOOK, msg.getMessage("module.pu.use.15")).addLore(msg.getMessage("module.pu.lore.15")).build());
            punishuserSelector.setItem(17,new ItemBuilder(XMaterial.BOOK, msg.getMessage("module.pu.use.17")).addLore(msg.getMessage("module.pu.lore.17")).build());
            if (player.hasPermission("admintool.punish.use")) {
                punishuserSelector.setItem(18, new ItemBuilder(XMaterial.GREEN_STAINED_GLASS_PANE, msg.getMessage("module.pu.use.18"))
                        .addLore(msg.getMessage("module.pu.lore.18").replace("%offences.chat%", chat_offences.toString())).build());
            } else {
                punishuserSelector.setItem(18, new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE, msg.getMessage("gui.punishuserSelector.noPermLore")).build());
            }

            if (player.hasPermission("admintool.punish.use")) {
                punishuserSelector.setItem(20, new ItemBuilder(XMaterial.GREEN_STAINED_GLASS_PANE, msg.getMessage("module.pu.use.20"))
                        .addLore(msg.getMessage("module.pu.lore.20")).build());
            } else {
                punishuserSelector.setItem(20, new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE, msg.getMessage("gui.punishuserSelector.noPermLore")).build());
            }

            if (player.hasPermission("admintool.punish.use")) {
                punishuserSelector.setItem(22, new ItemBuilder(XMaterial.GREEN_STAINED_GLASS_PANE, msg.getMessage("module.pu.use.22"))
                        .addLore(msg.getMessage("module.pu.lore.22")).build());
            } else {
                punishuserSelector.setItem(22, new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE, msg.getMessage("gui.punishuserSelector.noPermLore")).build());
            }

            if (player.hasPermission("admintool.punish.use")) {
                punishuserSelector.setItem(24, new ItemBuilder(XMaterial.GREEN_STAINED_GLASS_PANE, msg.getMessage("module.pu.use.24"))
                        .addLore(msg.getMessage("module.pu.lore.24")).build());
            } else {
                punishuserSelector.setItem(24, new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE, msg.getMessage("gui.punishuserSelector.noPermLore")).build());
            }

            if (player.hasPermission("admintool.mute.temp")) {
                punishuserSelector.setItem(26, new ItemBuilder(XMaterial.BLUE_STAINED_GLASS_PANE, msg.getMessage("module.pu.use.26"))
                        .addLore(msg.getMessage("module.pu.lore.26")).build());
            } else {
                punishuserSelector.setItem(26, new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE, msg.getMessage("gui.punishuserSelector.noPermLore")).build());
            }

            if (player.hasPermission("admintool.punish.use")) {
                punishuserSelector.setItem(27, new ItemBuilder(XMaterial.YELLOW_STAINED_GLASS_PANE, msg.getMessage("module.pu.use.27"))
                        .addLore(msg.getMessage("module.pu.lore.27")).build());
            } else {
                punishuserSelector.setItem(27, new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE, msg.getMessage("gui.punishuserSelector.noPermLore")).build());
            }

            if (player.hasPermission("admintool.punish.use")) {
                punishuserSelector.setItem(31, new ItemBuilder(XMaterial.YELLOW_STAINED_GLASS_PANE, msg.getMessage("module.pu.use.31"))
                        .addLore(msg.getMessage("module.pu.lore.31")).build());
            } else {
                punishuserSelector.setItem(31, new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE, msg.getMessage("gui.punishuserSelector.noPermLore")).build());
            }

            if (player.hasPermission("admintool.punish.use")) {
                punishuserSelector.setItem(29, new ItemBuilder(XMaterial.YELLOW_STAINED_GLASS_PANE, msg.getMessage("module.pu.use.29"))
                        .addLore(msg.getMessage("module.pu.lore.29")).build());
            } else {
                punishuserSelector.setItem(29, new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE, msg.getMessage("gui.punishuserSelector.noPermLore")).build());
            }

            if (player.hasPermission("admintool.punish.use")) {
                punishuserSelector.setItem(33, new ItemBuilder(XMaterial.YELLOW_STAINED_GLASS_PANE, msg.getMessage("module.pu.use.33"))
                        .addLore(msg.getMessage("module.pu.lore.33")).build());
            } else {
                punishuserSelector.setItem(33, new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE, msg.getMessage("gui.punishuserSelector.noPermLore")).build());
            }

            if (player.hasPermission("admintool.mute.temp")) {
                punishuserSelector.setItem(35, new ItemBuilder(XMaterial.BLUE_STAINED_GLASS_PANE, msg.getMessage("module.pu.use.35"))
                        .addLore(msg.getMessage("module.pu.lore.35")).build());
            } else {
                punishuserSelector.setItem(35, new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE, msg.getMessage("gui.punishuserSelector.noPermLore")).build());
            }
            punishuserSelector.setItem(37,new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE, msg.getMessage("module.pu.use.37")).addLore(msg.getMessage("module.pu.lore.37")).build());
            punishuserSelector.setItem(39,new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE, msg.getMessage("module.pu.use.39")).addLore(msg.getMessage("module.pu.lore.39")).build());
            punishuserSelector.setItem(41,new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE, msg.getMessage("module.pu.use.41")).addLore(msg.getMessage("module.pu.lore.41")).build());
            punishuserSelector.setItem(43,new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE, msg.getMessage("module.pu.use.43")).addLore(msg.getMessage("module.pu.lore.43")).build());
            punishuserSelector.setItem(49,new ItemBuilder(XMaterial.BARRIER, msg.getMessage("module.pu.use.49")).addLore(msg.getMessage("module.pu.lore.49")).build());
        } else {
            player.sendMessage(msg.getMessage("gui.punishuserSelector.noDoSilly", true, player));
        }

        punishuserList = other.getPlayer().getUniqueId();

        return true;
    }

    @EventHandler
    public void click(InventoryClickEvent event) {
        if(!invName.equals(event.getView().getTitle())) return;
        event.setCancelled(true);
        if(!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null) return;

        switch (event.getCurrentItem().getType()) {
            case GREEN_STAINED_GLASS_PANE:

                Player other = Bukkit.getPlayer(punishuserList);
                if(other == null) return;

                chat_offences = AdminTools.plugin.plugin.getDatabase().getOffencesChat(other.getName());
                hack_offences = AdminTools.plugin.plugin.getDatabase().getOffencesHack(other.getName());
                total_offences = AdminTools.plugin.plugin.getDatabase().getOffencesTotal(other.getName());

                AdminTools.plugin.plugin.getDatabase().setOffencesChat(other.getPlayer(), chat_offences+1, hack_offences+0, total_offences+1);

                other.setGameMode(GameMode.CREATIVE);
                punishuserList = null;

                player.sendMessage(msg.getMessage("module.pu.message.survival", true, player));
                break;
            default:
                break;
        }
        player.closeInventory();
    }

    @EventHandler
    private void guiClose(InventoryCloseEvent event){
        if(!invName.equals(event.getView().getTitle())) return;

        if(!(event.getPlayer() instanceof Player)) return;

        Player other = Bukkit.getPlayer(punishuserList);
        if(other == null) return;

        other.setGameMode(GameMode.CREATIVE);
        punishuserList = null;

    }

}