package club.therepo.admintools.modules;

import club.therepo.admintools.util.Configuration;
import club.therepo.admintools.util.XMaterial;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class HealModule extends Module {

    private final boolean fillFoodBar;

    public HealModule() {
        super(false, true, false,"heal", XMaterial.GOLDEN_APPLE);
        fillFoodBar = Configuration.get().getBoolean("module.heal.fillFoodBar");
    }

    @Override
    @SuppressWarnings("deprecation") //just for legacy support!
    public boolean execute(Player player, Player other, World world) {
        if(!super.execute(player, other, world)) {
            return false;
        }
        if(XMaterial.isNewVersion()) {
            other.setHealth(other.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        } else {
            other.setHealth(other.getMaxHealth());
        }
        if(fillFoodBar) other.setFoodLevel(20);
        return true;
    }
}