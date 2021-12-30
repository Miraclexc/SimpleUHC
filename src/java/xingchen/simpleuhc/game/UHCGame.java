package xingchen.simpleuhc.game;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class UHCGame {
    private UHCSetting setting;

    private List<Player> players;
    private String worldName;

    private boolean gaming;

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
        this.gaming = true;
        this.spreadPlayers();
        this.players.stream().forEach(i -> {
            i.sendTitle("游戏开始", "请努力生存到最后吧", 10, 70, 20);
        });
        plugin.getServer().getScheduler().runTaskLater(plugin, i -> {

        }, this.setting.getLastTime() * 1000);

        return true;
    }
}
