package xingchen.simpleuhc.game;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import xingchen.simpleuhc.config.Setting;

import java.util.ArrayList;
import java.util.List;
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
            players = new ArrayList<Player>();
        } else {
            this.players = players;
        }
        this.setting = setting;
        this.gaming = false;
    }

    public void broadcast(String message) {
        this.players.stream().forEach(i -> {
            i.sendMessage(message);
        });
    }

    public void createWolrd(String worldName) {
        this.broadcast("游戏即将开始……");

        WorldCreator creator = new WorldCreator(worldName);
        World world = Bukkit.getServer().createWorld(creator);
        world.setDifficulty(Difficulty.HARD);
        world.setPVP(true);
        this.setting.getCentre().setWorld(world);
        world.getWorldBorder().setCenter(this.setting.getCentre());
        this.setting.getArea().setBorder(world.getWorldBorder());
        world.setSpawnLocation(this.setting.getCentre());
    }

    public void spreadPlayers() {
        World world = Bukkit.getServer().getWorld(worldName);
        setting.getArea().spread(this.players, world);
    }

    public boolean start(Plugin plugin) {
        if(gaming) {
            return false;
        }
        if(endTask != null) {
            endTask.cancel();
        }
        this.gaming = true;
        this.spreadPlayers();
        this.players.stream().forEach(i -> {
            i.sendTitle("游戏开始", "请努力生存到最后吧", 10, 70, 20);
        });
        endTask = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            //TODO 不确定是否在同一线程
            settle(true);
        }, this.setting.getLastTime() * 1000);

        return true;
    }

    public boolean settle(boolean isTimeout) {
        if(!this.gaming) {
            return false;
        }
        this.players.stream().forEach(player -> {
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

    public String unloadWorld() {
        if(this.worldName == null) {
            return null;
        }
        //将还在世界中的玩家传送到大厅出生点
        if(!this.players.isEmpty()) {
            this.players.stream().forEach(i -> {
                World lobby = Bukkit.getServer().getWorld(Setting.getInstance().getLobby());
                i.teleport(lobby.getSpawnLocation());
            });
        }

        Bukkit.getServer().unloadWorld(this.worldName, false);

        return this.worldName;
    }
}
