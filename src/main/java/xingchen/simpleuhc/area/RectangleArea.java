package xingchen.simpleuhc.area;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class RectangleArea implements Area {
    protected int xRange;
    protected int yRange;

    public RectangleArea(int xRange, int yRange) {
        this.xRange = xRange;
        this.yRange = yRange;
    }

    @Override
    public boolean isInArea(Player player, Location origin) {
        if(player.getLocation().getWorld().equals(origin.getWorld())) {
            if(Math.abs(player.getLocation().getX() - origin.getX()) <= xRange && Math.abs(player.getLocation().getY() - origin.getY()) <= yRange) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setBorder(WorldBorder worldBorder) {

    }

    @Override
    public void spread(List<? extends Entity> entities, World world) {
        //TODO
    }


    public int getXRange() {
        return this.xRange;
    }

    public int getYRange() {
        return this.yRange;
    }
}
