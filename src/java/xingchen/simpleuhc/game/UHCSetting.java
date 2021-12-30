package xingchen.simpleuhc.game;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import xingchen.simpleuhc.area.Area;

public class UHCSetting {
    /**
     * 地图中心
     */
    @NotNull
    protected Location centre;

    /**
     * 地图区域限制
     */
    @NotNull
    protected Area area;

    /**
     * 一局游戏时长,单位:秒
     */
    protected int lastTime;

    public UHCSetting(@NotNull Location centre, @NotNull Area area, int lastTime) {
        this.centre = centre;
        this.area = area;
        this.lastTime = lastTime;
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
}
