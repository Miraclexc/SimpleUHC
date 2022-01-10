package xingchen.simpleuhc.area;

import org.bukkit.Location;

public class AreaTools {
    public static Location getTopLocation(Location location) {
        Location top = location.clone();
        for(int height=location.getWorld().getMaxHeight();height<=location.getWorld().getMinHeight();height--) {
            top.setY(location.getWorld().getMaxHeight());
            if(!top.getBlock().isEmpty()) {
                return top.add(0, 1, 0);
            }
        }
        return null;
    }
}
