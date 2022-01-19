package xingchen.simpleuhc.config;

import java.io.File;
import java.net.URL;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import xingchen.simpleuhc.SimpleUHC;
import xingchen.simpleuhc.area.Area;
import xingchen.simpleuhc.area.RoundArea;
import xingchen.simpleuhc.game.UHCSetting;

public class Setting {
    public static final int TIMEUNITS = 20;

    private Plugin plugin;
    private Logger logger;
    private FileConfiguration configFile;

    protected static Setting INSTANCE;

    /**地图中心*/
    protected Location centre;

    /**地图区域限制*/
    protected Area area;

    /**一局游戏时长,单位:秒*/
    protected int lastTime;

    /**能同时进行的最大游戏数*/
    protected int maxGameNumber;

    /**游戏大厅*/
    protected String lobby;

    /**游戏结算后多久将玩家传送出游戏，单位：tick*/
    protected int delayedTime;

    /**游戏结束后,给玩家设置的游戏模式*/
    protected GameMode gamemodeWhenFinished;

    public Setting(Plugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();

        if(!this.checkCongig("config.yml")) {
            this.logger.info("配置文件未创建，正在创建...");
            plugin.saveDefaultConfig();
        } else {
            this.logger.info("配置文件已经创建，开始加载...");
        }

        this.configFile = this.plugin.getConfig();

        if(this.configFile != null) {
            this.centre = this.configFile.getLocation("centreLocation", new Location(Bukkit.getServer().getWorlds().get(0), 0, 0, 0));
            int size = this.configFile.getInt("size", 0);
            if(size != 0) {
                this.area = new RoundArea(2 * size);
            }
            this.lastTime = this.configFile.getInt("latTime", 600);
            this.maxGameNumber = this.configFile.getInt("maxGameNumber", 10);
            this.lobby = this.configFile.getString("lobby", null);
            this.delayedTime  = this.configFile.getInt("delayedTime", 5000);
            this.gamemodeWhenFinished  = GameMode.valueOf(this.configFile.getString("gamemodeWhenFinished", "ADVENTURE"));
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
     * 删除文件夹
     * @param directoryToBeDeleted 待删除的文件夹
     * @return 是否删除成功
     */
    public boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
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

    public String getLobby() {
        return this.lobby;
    }

    public void setLobby(String lobby) {
        this.lobby = lobby;
    }

    public int getDelayedTime() {
        return this.delayedTime;
    }

    public void setDelayedTime(int delayedTime) {
        this.delayedTime = delayedTime;
    }

    public GameMode getGamemodeWhenFinished() {
        return this.gamemodeWhenFinished;
    }

    public void setGamemodeWhenFinished(GameMode gamemodeWhenFinished) {
        this.gamemodeWhenFinished = gamemodeWhenFinished;
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