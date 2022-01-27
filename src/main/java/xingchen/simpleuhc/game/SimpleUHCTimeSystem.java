package xingchen.simpleuhc.game;

import org.bukkit.World;

public class SimpleUHCTimeSystem implements UHCTimeSystem {
    protected boolean shrinking;

    public SimpleUHCTimeSystem() {
        this.shrinking = false;
    }

    @Override
    public void update(UHCGame game, int time) {
        UHCSetting setting = game.getSetting();
        if(setting.getShrinkTime() >= 0 && setting.getShrinkScale() > 0 && time >= setting.getShrinkTime()) {
            if(!this.shrinking) {
                game.forPlayersInWorld(i -> {
                    i.sendTitle("边界开始收缩了", "请尽快前往安全区域", 10, 70, 20);
                    i.sendMessage("边界开始收缩了");
                });
                this.shrinking = true;
            }
            World world = game.getWorld();
            double coefficient = 1 - (double) (time - setting.getShrinkTime()) / (setting.getLastTime() - setting.getShrinkTime()) * setting.getShrinkScale();
            setting.getArea().scaleBorder(world.getWorldBorder(), coefficient);
        }
    }
}
