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
            double randomR = Math.random() * r;
            double randomAngle = Math.random() * 2 * Math.PI;
            AreaTools.randomTeleport(entity, world, (int) (randomR * Math.cos(randomAngle)), (int) (randomR * Math.sin(randomAngle)));
        });
    }


    public int getR() {
        return this.r;
    }
}
