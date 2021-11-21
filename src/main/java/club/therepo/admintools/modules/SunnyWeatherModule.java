package club.therepo.admintools.modules;

import club.therepo.admintools.util.XMaterial;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SunnyWeatherModule extends Module{

    public SunnyWeatherModule() {
        super(true,false,false,"weather", XMaterial.WATER_BUCKET);
        useDefaultMessageKeyFormat = false;
    }

    @Override
    public boolean execute(Player player, Player other, World world) {
        if(!super.execute(player,other,world)) {
            return false;
        }
        if(world.hasStorm()) {
            world.setStorm(false);
            player.sendMessage(msg.getMessageAndReplace("module.weather.message.toggleOff",true,player,world.getName()));
        } else {
            world.setStorm(true);
            player.sendMessage(msg.getMessageAndReplace("module.weather.message.toggleOn",true,player,world.getName()));
        }
        return false;
    }
}