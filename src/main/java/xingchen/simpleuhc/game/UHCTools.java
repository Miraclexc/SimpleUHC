package xingchen.simpleuhc.game;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class UHCTools {
    public static void spawnFirework(Location location) {
        Entity firework = location.getWorld().spawnEntity(location, EntityType.FIREWORK);
    }
}
