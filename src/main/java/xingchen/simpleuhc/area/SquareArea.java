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
}
