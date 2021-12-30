package xingchen.simpleuhc.config;

import java.io.File;
import java.net.URL;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import xingchen.simpleuhc.SimpleUHC;
import xingchen.simpleuhc.area.Area;
import xingchen.simpleuhc.area.RectangleArea;
import xingchen.simpleuhc.area.RoundArea;
import xingchen.simpleuhc.game.UHCSetting;

public class Setting {
    private Plugin plugin;
    private Logger logger;
    private FileConfiguration configFile;

    protected static Setting INSTANCE;

    /**
     * 地图中心
     */
    protected Location centre;

    /**
     * 地图区域限制
     */
    protected Area area;

    /**
     * 一局游戏时长,单位:秒
     */
    protected int lastTime;

    /**
     * 能同时进行的最大游戏数
     */
    protected int maxGameNumber;

    public Setting(Plugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();

        /*if(!this.checkCongig("config.yml")) {
            this.logger.info("配置文件未创建，正在创建...");
            plugin.saveDefaultConfig();
        } else {
            this.logger.info("配置文件已经创建，开始加载...");
        }*/

        this.configFile = this.plugin.getConfig();

        if(this.configFile != null) {
            this.centre = this.configFile.getLocation("centreLocation", new Location(Bukkit.getServer().getWorlds().get(0), 0, 0, 0));
            int r = this.configFile.getInt("r", 0);
            if(r != 0) {
                this.area = new RoundArea(r);
            }
            this.lastTime = this.configFile.getInt("latTime", 600);
            this.maxGameNumber = this.configFile.getInt("maxGameNumber", 10);
        }
    }

    /**
     * 检查配置目录下指定文件是否存在
     */
    public boolean checkCongig(String configName) {
        File file = new File(this.plugin.getDataFolder(), configName);
        if(file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取配置目录下指定文件
     */
    public File getFile(String path) {
        File file = new File(this.plugin.getDataFolder(), path);
        if(!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * 获取插件jar路径
     */
    public URL getJarURL() {
        return SimpleUHC.class.getProtectionDomain().getCodeSource().getLocation();
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public FileConfiguration getConfigFile() {
        return this.configFile;
    }

    public void setConfigFile(FileConfiguration configFile) {
        this.configFile = configFile;
    }

    public UHCSetting generalSetting() {
        return new UHCSetting(this.centre.clone(), this.area, this.lastTime);
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

    public int getMaxGameNumber() {
        return this.maxGameNumber;
    }

    public void setMaxGameNumber(int maxGameNumber) {
        this.maxGameNumber = maxGameNumber;
    }

    public static void init(Plugin plugin) {
        if(INSTANCE == null) {
            INSTANCE = new Setting(plugin);
        }
    }

    public static Setting getInstance() {
        return INSTANCE;
    }
}
