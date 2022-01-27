package xingchen.simpleuhc.area;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class RectangleArea implements Area {
    protected int xRange;
    protected int zRange;
    protected int centreX;
    protected int centreZ;

    public RectangleArea(int xRange, int zRange, int centreX, int centreZ) {
        this.xRange = xRange;
        this.zRange = zRange;
        this.centreX = centreX;
        this.centreZ = centreZ;
    }

    @Override
    public boolean isInArea(Player player, Location origin) {
        if(player.getLocation().getWorld().equals(origin.getWorld())) {
            if(Math.abs(player.getLocation().getX() - origin.getX()) <= this.getCentreX() && Math.abs(player.getLocation().getZ() - origin.getZ()) <= this.getCentreZ()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setBorder(WorldBorder worldBorder) {

    }

    @Override
    public void scaleBorder(WorldBorder worldBorder, double coefficient) {

    }

    @Override
    public void spread(List<? extends Entity> entities, World world) {
        entities.stream().forEach(entity -> {
            int randomX = (int) ((Math.random() - 0.5) * ((double) this.getXRange() * 9 / 10));
            int randomZ = (int) ((Math.random() - 0.5) * ((double) this.getZRange() * 9 / 10));
            AreaTools.randomTeleport(entity, world, this.getCentreX() + randomX, this.getCentreZ() + randomZ);
        });
    }


    public int getXRange() {
        return this.xRange;
    }

    public int getZRange() {
        return this.zRange;
    }

    public int getCentreX() {
        return this.centreX;
    }

    public int getCentreZ() {
        return this.centreZ;
    }
}
