package club.therepo.admintools.modules;

import club.therepo.admintools.AdminTools;
import club.therepo.admintools.util.Configuration;
import club.therepo.admintools.util.ItemBuilder;
import club.therepo.admintools.util.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.Collection;

public class ChatClearModule extends Module implements Listener {

    private final String invName;
    private final Inventory clearchatSelector;

    private final int messageAmount;


    public ChatClearModule() {
        super(false, false,false, "chatclear", XMaterial.STRUCTURE_VOID);
        invName = msg.getMessage("gui.chatclear.invName");

        clearchatSelector = Bukkit.createInventory(null, InventoryType.HOPPER, invName);
        clearchatSelector.setItem(0,ItemBuilder.BLACKPANE);
        clearchatSelector.setItem(1,new ItemBuilder(XMaterial.IRON_BLOCK, msg.getMessage("gui.chatclear.forYou")).build());
        clearchatSelector.setItem(2,ItemBuilder.BLACKPANE);
        clearchatSelector.setItem(3,new ItemBuilder(XMaterial.GOLD_BLOCK, msg.getMessage("gui.chatclear.forAll")).build());
        clearchatSelector.setItem(4,ItemBuilder.BLACKPANE);
        Bukkit.getPluginManager().registerEvents(this, AdminTools.getInstance());
        int messageAmount = Configuration.get().getInt("module.chatclear.messageAmount",100);
        if(messageAmount > 256) messageAmount = 256;
        this.messageAmount = messageAmount;
    }

    @Override
    public boolean execute(Player player, Player other, World world) {
        if(!super.execute(player, other, world)) {
            return false;
        }
        player.openInventory(clearchatSelector);
        return true;
    }


    @EventHandler
    public void click(InventoryClickEvent event) {
        if(!invName.equals(event.getView().getTitle())) return;
        event.setCancelled(true);
        if(!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        if(event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null) return;

        switch(event.getCurrentItem().getType()) {
            case IRON_BLOCK:
                ccPlayer(player);
                player.closeInventory();
                break;
            case GOLD_BLOCK:
                player.closeInventory();
                if(player.hasPermission("admintools.module.chatclear"))
                    ccAll(player);
                else
                    player.sendMessage(msg.getMessageAndReplace("chatmessages.noperm",true,player,"admintools.module.chatclear"));
                break;
            default:
                break;
        }
    }

    private void ccPlayer(Player p) {
        Bukkit.getScheduler().runTaskAsynchronously(AdminTools.getInstance(), () -> {
            for(int i = 0; i < messageAmount; i++) {
                p.sendMessage(" ");
            }
            p.sendMessage(msg.getMessage("module.chatclear.message.clearedForYou",true,p));
        });
    }

    private void ccAll(Player player) {
        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        final Server server = Bukkit.getServer();
        Bukkit.getScheduler().runTaskAsynchronously(AdminTools.getInstance(), () -> {
            for(Player p : players){
                for(int i = 0; i < messageAmount; i++) {
                    p.sendMessage(" ");
                }
            }
            player.sendMessage(msg.getMessage("module.chatclear.message.clearedForAll",true,player));
            server.broadcastMessage(msg.getMessageAndReplace("module.chatclear.message.clearedByPlayer",true,player,player.getName()));
        });
    }
}