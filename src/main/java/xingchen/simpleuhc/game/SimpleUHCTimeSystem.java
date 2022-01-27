package xingchen.simpleuhc.game;

import org.bukkit.World;
import xingchen.simpleuhc.language.UHCLanguage;

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
                    i.sendTitle(UHCLanguage.getInstance().translate("game.title.shrinkRemind"), UHCLanguage.getInstance().translate("game.title.shrinkRemind_sub"), 10, 70, 20);
                    i.sendMessage(UHCLanguage.getInstance().translate("game.title.shrinkRemind"));
                });
                this.shrinking = true;
            }
            World world = game.getWorld();
            double coefficient = 1 - (double) (time - setting.getShrinkTime()) / (setting.getLastTime() - setting.getShrinkTime()) * setting.getShrinkScale();
            setting.getArea().scaleBorder(world.getWorldBorder(), coefficient);
        }
    }
}
