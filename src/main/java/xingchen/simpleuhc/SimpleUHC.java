package xingchen.simpleuhc;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xingchen.simpleuhc.command.UHCCommand;
import xingchen.simpleuhc.config.Setting;
import xingchen.simpleuhc.event.UHCListener;

public class SimpleUHC extends JavaPlugin {
    private static SimpleUHC INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        Setting.init(this);
        UHCCommand command = new UHCCommand();
        Bukkit.getPluginCommand("spuhc").setExecutor(command);
        Bukkit.getPluginCommand("spuhc").setTabCompleter(command);
        Bukkit.getServer().getPluginManager().registerEvents(new UHCListener(), this);
    }

    public static SimpleUHC getInstance() {
        return INSTANCE;
    }
}
