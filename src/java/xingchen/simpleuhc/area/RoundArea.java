package xingchen.simpleuhc.area;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class RoundArea implements Area {
    protected int r;

    public RoundArea(int r) {
        this.r = r;
    }

    @Override
    public boolean isInArea(Player player, Location origin) {
        if(player.getLocation().getWorld().equals(origin.getWorld())) {
            if(player.getLocation().distance(origin) <= r) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setBorder(WorldBorder worldBorder) {
        worldBorder.setSize(r);
    }

    @Override
    public void spread(List<? extends Entity> entities, World world) {
        entities.stream().forEach(entity -> {
            int randomx = (int) Math.random() * r;
            int randomz = (int) Math.random() * r;
            Location location = new Location(world, randomx, world.getMinHeight(), randomz);
            int y = world.getChunkAt(location).getChunkSnapshot().getHighestBlockYAt(randomx, randomz);
            if(y < world.getMinHeight()) {
                location.getBlock().setType(Material.GRASS_BLOCK);
                location.add(0, 1, 0);
            } else {
                location.setY(y + 1);
            }
            entity.teleport(location);
        });
    }


    public int getR() {
        return this.r;
    }
}
