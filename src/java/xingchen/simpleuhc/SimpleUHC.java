package xingchen.simpleuhc;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xingchen.simpleuhc.config.Setting;
import xingchen.simpleuhc.event.UHCListener;

public class SimpleUHC extends JavaPlugin {
    @Override
    public void onEnable() {
        Setting.init(this);
        Bukkit.getPluginCommand("spuhc").setExecutor(new SPUHCCommand());
        Bukkit.getServer().getPluginManager().registerEvents(new UHCListener(), this);
    }
}
