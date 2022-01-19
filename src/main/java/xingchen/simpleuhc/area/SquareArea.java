package xingchen.simpleuhc.area;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Entity;

import java.util.List;

public class SquareArea extends RectangleArea {
    public SquareArea(int sideLength) {
        super(sideLength, sideLength);
    }

    @Override
    public void setBorder(WorldBorder worldBorder) {
        worldBorder.setSize(getXRange());
    }

    @Override
    public void spread(List<? extends Entity> entities, World world) {
        entities.stream().forEach(entity -> {
            int randomx = (int) Math.random() * this.getXRange();
            int randomz = (int) Math.random() * this.getXRange();
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
}
