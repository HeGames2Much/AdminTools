package club.therepo.admintools.modules;

import club.therepo.admintools.util.XMaterial;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class EnderchestModule extends Module {


    public EnderchestModule() {
        super(false, true, "enderchest", XMaterial.ENDER_CHEST);
    }

    @Override
    public boolean execute(Player player, Player other, World world) {
        if(!super.execute(player, other, world)) {
            return false;
        }
        player.openInventory(other.getEnderChest());
        return true;
    }

}