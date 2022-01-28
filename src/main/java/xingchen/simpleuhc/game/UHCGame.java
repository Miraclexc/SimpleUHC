package xingchen.simpleuhc.game;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import xingchen.simpleuhc.area.AreaTools;
import xingchen.simpleuhc.config.Setting;
import xingchen.simpleuhc.language.UHCLanguage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UHCGame {
    private UHCSetting setting;

    private List<Player> players;
    private String worldName;

    private boolean gaming;
    private int maxPlayerNumber;
    private BukkitTask timer;
    private int currentTime;
    private UHCTimeSystem system;
    private UHCScoreboard scoreboard;

    public UHCGame(List<Player> players, @NotNull UHCSetting setting) {
        if(players == null) {
            this.players = new ArrayList<>();
        } else {
            this.players = players;
        }

        this.setting = setting;
        this.gaming = false;
        this.currentTime = 0;
        this.scoreboard = UHCScoreboard.createScoreboard();
    }

    /**
     * 对游戏中的每一个玩家执行对应操作
     *
     * @param consumer 操作流程
     */
    public void forPlayersInGame(Consumer<Player> consumer) {
        this.players.stream().forEach(i -> consumer.accept(i));
    }

    /**
     * 对游戏所在世界中的每一个玩家执行对应操作
     *
     * @param consumer 操作流程
     */
    public void forPlayersInWorld(Consumer<Player> consumer) {
        this.getPlayersInWorld().stream().forEach(i -> consumer.accept(i));
    }

    /**
     * 为游戏创建一个指定名字的新世界,并初始化
     */
    public void createWorld(String worldName) {
        this.forPlayersInGame(i -> i.sendMessage(UHCLanguage.getInstance().translate("game.message.aboutToBegin")));

        this.worldName = worldName;
        Setting.getInstance().getLogger().info(String.format(UHCLanguage.getInstance().translate("system.debug.createWorld"), worldName));
        WorldCreator creator = new WorldCreator(worldName);
        World world = Bukkit.getServer().createWorld(creator);
        world.setDifficulty(Difficulty.HARD);
        world.setPVP(true);
        world.setGameRule(GameRule.KEEP_INVENTORY, false);
        world.setGameRule(GameRule.DO_MOB_LOOT, true);
        world.setGameRule(GameRule.MOB_GRIEFING, true);
        this.setting.getCentre().setWorld(world);
        world.getWorldBorder().setCenter(this.setting.getCentre());
        this.setting.getArea().setBorder(world.getWorldBorder());
        world.setSpawnLocation(this.setting.getCentre());
    }

    /**
     * 将游戏所在世界中的玩家根据指定配置分散到各世界区域
     */
    public void spreadPlayers() {
        World world = this.getWorld();
        this.setting.getArea().spread(this.players, world);
    }

    /**
     * 开始游戏并将玩家传送到对应世界
     *
     * @return 游戏是否成功开始
     */
    public boolean start(Plugin plugin) {
        if(gaming) {
            return false;
        }
        if(timer != null) {
            timer.cancel();
        }
        this.gaming = true;
        this.maxPlayerNumber = this.getPlayers().size();
        this.scoreboard.clearScores();
        this.scoreboard.init();
        this.spreadPlayers();
        this.forPlayersInGame(i -> {
            i.setGameMode(GameMode.SURVIVAL);
            i.setScoreboard(scoreboard.getScoreboard());
            UHCTools.initPlayer(i);
            i.sendTitle(UHCLanguage.getInstance().translate("game.title.begin"), UHCLanguage.getInstance().translate("game.title.begin_sub"), 10, 70, 20);
        });
        this.currentTime = 0;
        this.system = new SimpleUHCTimeSystem();
        UHCGame uhcgame = this;
        this.timer = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            scoreboard.setTime(setting.lastTime - currentTime);
            scoreboard.setPlayerNumber(players.size(), maxPlayerNumber);
            if(uhcgame.currentTime >= uhcgame.setting.getLastTime()) {
                int index = UHCGameManager.getInstance().getIndexFromGame(uhcgame);
                uhcgame.timer.cancel();
                uhcgame.timer = null;
                UHCGameManager.getInstance().finishGame(index);
                return;
            }
            uhcgame.system.update(uhcgame, uhcgame.currentTime);
            uhcgame.currentTime++;
        }, 0, Setting.TIMEUNITS);

        return true;
    }

    /**
     * 对游戏进行结算和告示(并不会自动执行{@link #unloadWorld()}方法)
     *
     * @return 是否成功结束游戏
     */
    public boolean settle() {
        if(!this.gaming) {
            return false;
        }
        this.forPlayersInGame(player -> {
            player.sendMessage(UHCLanguage.getInstance().translate("game.message.survived"));
            IntStream.range(0, 5).forEach(i -> {
                UHCTools.spawnFirework(player.getLocation());
            });
        });
        String alivePlayers = this.players.stream().map(i -> i.getName()).collect(Collectors.joining(", "));
        this.forPlayersInWorld(player -> player.sendMessage(String.format(UHCLanguage.getInstance().translate("game.message.currentPlayers"), alivePlayers)));

        if(timer != null) {
            timer.cancel();
            timer = null;
        }

        return true;
    }

    /**
     * 将世界中的玩家都传送至大厅{@link Setting#getLobby()}并删除世界
     *
     * @return 被删除世界的名字
     */
    public String unloadWorld() {
        if(this.worldName == null) {
            return null;
        }
        //将还在世界中的玩家传送到大厅出生点
        this.forPlayersInWorld(i -> {
            World lobby = Bukkit.getServer().getWorld(Setting.getInstance().getLobby());
            i.teleport(lobby.getSpawnLocation());
            i.setGameMode(Setting.getInstance().getGamemodeWhenFinished());
            i.setScoreboard(Bukkit.getServer().getScoreboardManager().getMainScoreboard());
            UHCTools.initPlayer(i);
        });

        File worldDictionary = Bukkit.getServer().getWorld(this.worldName).getWorldFolder();
        Bukkit.getServer().unloadWorld(this.worldName, false);
        AreaTools.deleteWorld(worldDictionary);
        Setting.getInstance().getLogger().info(String.format(UHCLanguage.getInstance().translate("system.debug.deleteWorld"), this.worldName));
        this.scoreboard.clearObjective();

        return this.worldName;
    }

    /**
     * 将玩家传送到游戏所在世界的出生点
     *
     * @param player 待传送的玩家
     */
    public void teleportPlayerToWorld(Player player) {
        World world = this.getWorld();
        player.teleport(world.getSpawnLocation());
    }

    /**
     * 获取游戏所在世界的所有玩家
     */
    public List<Player> getPlayersInWorld() {
        return Bukkit.getServer().getWorld(this.worldName).getPlayers();
    }

    /**
     * 获取正在游戏中的所有玩家(不包含观察者)
     */
    public List<Player> getPlayers() {
        return this.players;
    }

    /**
     * 获取游戏对应的世界名
     */
    public String getWorldName() {
        return this.worldName;
    }

    /**
     * 获取游戏对应的世界
     */
    public World getWorld() {
        World world = Bukkit.getServer().getWorld(this.worldName);
        return world;
    }

    /**
     * 获取本局游戏配置
     */
    public UHCSetting getSetting() {
        return this.setting;
    }

    /**
     * 获取本局游戏的计分版
     */
    public UHCScoreboard getScoreboard() {
        return this.scoreboard;
    }

    /**
     * 判断游戏是否已经开始
     */
    public boolean isGaming() {
        return this.gaming;
    }

    public void setGaming(boolean gaming) {
        this.gaming = gaming;
    }
}
