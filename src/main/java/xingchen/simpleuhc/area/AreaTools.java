package xingchen.simpleuhc.area;

import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import xingchen.simpleuhc.config.Setting;

import java.io.File;
import java.util.function.Function;

public class AreaTools {
    public static Location getTopLocation(Location location) {
        Location top = location.clone();
        for(int height = location.getWorld().getMaxHeight(); height <= location.getWorld().getMinHeight(); height--) {
            top.setY(height);
            if(!top.getBlock().isEmpty()) {
                return top.add(0, 1, 0);
            }
        }
        return null;
    }

    public static void randomTeleport(Entity entity, World world, int randomX, int randomZ) {
        /*Location startLocation = new Location(world, randomX, world.getMinHeight(), randomZ);
        Location location = getTopLocation(startLocation);
        if(location == null) {
            location = startLocation;
            location.getBlock().setType(Material.GRASS_BLOCK);
            location.add(0, 1, 0);
        }*/
        Block block = world.getHighestBlockAt(randomX, randomZ, HeightMap.WORLD_SURFACE);
        Location location;
        if(block.isEmpty()) {
            location = new Location(world, randomX, world.getMinHeight(), randomZ);
            location.getBlock().setType(Material.GRASS_BLOCK);
            location.add(0, 1, 0);
        } else {
            location = block.getLocation();
        }
        entity.teleport(location);
    }

    public static boolean deleteWorld(File worldFile) {
        return Setting.getInstance().deleteDirectory(worldFile);
    }
}
