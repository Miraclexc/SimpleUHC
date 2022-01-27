package xingchen.simpleuhc.area;

import org.bukkit.WorldBorder;

public class SquareArea extends RectangleArea {
    public SquareArea(int sideLength, int centreX, int centreZ) {
        super(sideLength, sideLength, centreX, centreZ);
    }

    @Override
    public void setBorder(WorldBorder worldBorder) {
        super.setBorder(worldBorder);
        worldBorder.setSize(this.getXRange());
    }

    @Override
    public void scaleBorder(WorldBorder worldBorder, double coefficient) {
        worldBorder.setSize(coefficient * this.getXRange());
    }
}
