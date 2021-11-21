package club.therepo.admintools.modules;

import club.therepo.admintools.util.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class WorkbenchModule extends Module {

    public WorkbenchModule() {
        super(false, false,false, "workbench", XMaterial.CRAFTING_TABLE);
    }

    @Override
    public boolean execute(Player player, Player other, World world) {
        if(!super.execute(player, other, world)) {
            return false;
        }
        player.openInventory(Bukkit.createInventory(player, InventoryType.WORKBENCH));
        return true;
    }

}