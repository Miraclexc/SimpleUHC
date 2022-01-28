package xingchen.simpleuhc.game.room;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xingchen.simpleuhc.SimpleUHC;
import xingchen.simpleuhc.config.Setting;
import xingchen.simpleuhc.game.UHCGame;
import xingchen.simpleuhc.game.UHCGameManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RoomImpl implements Room {
    protected String name;
    protected int maxPlayerNumber;
    protected String password;
    protected UUID owner;
    protected List<UUID> players;

    public RoomImpl(String name, String password) {
        this.maxPlayerNumber = Setting.getInstance().getGameMaxPlayer();

        this.name = name;
        this.password = password;

        this.players = new ArrayList<>(this.maxPlayerNumber);
    }

    public RoomImpl(String name) {
        this(name, "");
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public int getMaxPlayerNumber() {
        return this.maxPlayerNumber;
    }

    @Override
    public void setMaxPlayerNumber(int number) {
        this.maxPlayerNumber = number;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public UUID getOwner() {
        return this.owner;
    }

    @Override
    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    @Override
    public List<UUID> getPlayers() {
        return this.players;
    }

    @Override
    public boolean start() {
        if(this.getPlayers().size() < 2) {
            return false;
        }
        List<Player> players = new ArrayList<>();
        this.getPlayers().stream().forEach(i -> {
            players.add(Bukkit.getServer().getPlayer(i));
        });
        UHCGameManager.getInstance().getRooms().remove(this.name());
        UHCGame game = new UHCGame(players, Setting.getInstance().generalUHCSetting());
        if(UHCGameManager.getInstance().newGame(game)) {
            game.start(SimpleUHC.getInstance());
        }
        return true;
    }

    /**
     * 检测给定密码是否和房间密码匹配
     *
     * @param room 房间
     * @param password 给定密码
     *
     * @return 密码是否匹配
     */
    public static boolean checkPassword(Room room, String password) {
        if(room.getPassword().equals(password)) {
            return true;
        }
        return false;
    }
}
