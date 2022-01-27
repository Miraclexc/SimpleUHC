package xingchen.simpleuhc.game;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class UHCTools {
    public static void spawnFirework(Location location) {
        Entity firework = location.getWorld().spawnEntity(location, EntityType.FIREWORK);
    }

    /**
     * 用于回复满传送后玩家的状态
     */
    public static void initPlayer(Player player) {
        player.getInventory().clear();
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setFoodLevel(20);
        player.setLevel(0);
        player.setExp(0);
    }
}
