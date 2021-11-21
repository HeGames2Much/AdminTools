package club.therepo.admintools.modules;

import club.therepo.admintools.util.XMaterial;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SetSpawnModule extends Module {

    public SetSpawnModule() {
        super(false, false,false, "setspawn", XMaterial.DIRT);
    }

    @Override
    public boolean execute(Player player, Player other, World world) {
        if(!super.execute(player, other, world)) {
            return false;
        }
        player.getWorld().setSpawnLocation(player.getLocation());
        return true;
    }

}