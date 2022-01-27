package xingchen.simpleuhc.game;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import xingchen.simpleuhc.area.Area;

public class UHCSetting {
    /**地图中心*/
    @NotNull
    protected Location centre;

    /**地图区域限制*/
    @NotNull
    protected Area area;

    /**一局游戏时长,单位:秒*/
    protected int lastTime;

    /**多长时间后开始缩小边界,小于0则不缩小边界,单位:秒*/
    protected int shrinkTime;

    /**边界缩小的缩放程度(用于控制最小范围)*/
    protected double shrinkScale;

    public UHCSetting(@NotNull Location centre, @NotNull Area area, int lastTime, int shrinkTime, double shrinkScale) {
        this.centre = centre;
        this.area = area;
        this.lastTime = lastTime;
        this.shrinkTime = shrinkTime;
        this.shrinkScale = shrinkScale;
    }

    public UHCSetting(@NotNull Location centre, @NotNull Area area, int lastTime) {
        this(centre, area, lastTime, 0, 0);
    }

    public Location getCentre() {
        return this.centre;
    }

    public void setCentre(Location centre) {
        this.centre = centre;
    }

    public Area getArea() {
        return this.area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public int getLastTime() {
        return this.lastTime;
    }

    public void setLastTime(int lastTime) {
        this.lastTime = lastTime;
    }

    public int getShrinkTime() {
        return this.shrinkTime;
    }

    public void setShrinkTime(int shrinkTime) {
        this.shrinkTime = shrinkTime;
    }

    public double getShrinkScale() {
        return this.shrinkScale;
    }

    public void setShrinkScale(double shrinkScale) {
        this.shrinkScale = shrinkScale;
    }
}
