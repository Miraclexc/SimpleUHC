package xingchen.simpleuhc.game;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import xingchen.simpleuhc.area.AreaTools;
import xingchen.simpleuhc.config.Setting;

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
    private BukkitTask endTask;

    public UHCGame(List<Player> players, @NotNull UHCSetting setting) {
        if(players == null) {
            this.players = new ArrayList<>();
        } else {
            this.players = players;
        }

        this.setting = setting;
        this.gaming = false;
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
        this.worldName = worldName;
        Setting.getInstance().getLogger().info("创建世界:" + worldName);
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
        World world = Bukkit.getServer().getWorld(worldName);
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
        if(endTask != null) {
            endTask.cancel();
        }
        this.gaming = true;
        this.spreadPlayers();
        this.forPlayersInGame(i -> {
            //TODO 玩家各项属性初始化
            i.setGameMode(GameMode.SURVIVAL);
            i.getInventory().clear();
            i.setHealth(i.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            i.sendTitle("游戏开始", "请努力生存到最后吧", 10, 70, 20);
        });
        endTask = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            //TODO 不确定是否在同一线程
            settle(true);
        }, this.setting.getLastTime() * Setting.TIMEUNITS);

        return true;
    }

    /**
     * 对游戏进行结算和告示(并不会自动执行{@link #unloadWorld()}方法)
     *
     * @param isTimeout 游戏是否是超时导致的结束
     *
     * @return 是否成功结束游戏
     */
    public boolean settle(boolean isTimeout) {
        if(!this.gaming) {
            return false;
        }
        this.forPlayersInGame(player -> {
            player.sendMessage("游戏结束，你成功生存到了最后。");
            player.sendMessage("目前仍活着的玩家：" + this.players.stream().map(i -> i.getName()).collect(Collectors.joining(", ")));
            IntStream.range(0, 5).forEach(i -> {
                UHCTools.spawnFirework(player.getLocation());
            });
        });

        if(!isTimeout) {
            endTask.cancel();
            endTask = null;
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
            i.getInventory().clear();
            i.setHealth(i.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        });

        File worldDictionary = Bukkit.getServer().getWorld(this.worldName).getWorldFolder();
        Bukkit.getServer().unloadWorld(this.worldName, false);
        AreaTools.deleteWorld(worldDictionary);
        Setting.getInstance().getLogger().info("删除世界:" + this.worldName);

        return this.worldName;
    }

    /**
     * 将玩家传送到游戏所在世界的出生点
     *
     * @param player 待传送的玩家
     */
    public void teleportPlayerToWorld(Player player) {
        World world = Bukkit.getServer().getWorld(this.worldName);
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
     * 判断游戏是否已经开始
     */
    public boolean isGaming() {
        return this.gaming;
    }

    public void setGaming(boolean gaming) {
        this.gaming = gaming;
    }
}
