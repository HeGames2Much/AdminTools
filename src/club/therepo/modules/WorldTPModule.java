package club.therepo.modules;

import club.therepo.util.XMaterial;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WorldTPModule extends Module{

    public WorldTPModule() {
        super(true, true, "worldtp", XMaterial.ENDER_PEARL);
    }

    @Override
    public boolean execute(Player player, Player other, World world) {
        if(!super.execute(player, other, world)) {
            return false;
        }
        Location to = new Location(world,other.getLocation().getX(), other.getLocation().getY(),other.getLocation().getZ());
        other.teleport(to);
        return true;
    }
}