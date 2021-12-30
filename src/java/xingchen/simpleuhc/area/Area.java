package xingchen.simpleuhc.area;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public interface Area {
    public boolean isInArea(Player player, Location origin);

    public void setBorder(WorldBorder worldBorder);

    public void spread(List<? extends Entity> entities, World world);
}
