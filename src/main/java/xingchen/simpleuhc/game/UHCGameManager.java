package xingchen.simpleuhc.game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xingchen.simpleuhc.SimpleUHC;
import xingchen.simpleuhc.config.Setting;
import xingchen.simpleuhc.game.room.Room;

import java.util.*;
import java.util.stream.IntStream;

public class UHCGameManager {
    public static final String WORLDNAMEFORMATS = "spuhc-%d";

    private static final UHCGameManager INSTANCE = new UHCGameManager();

    private List<UHCGame> games;
    private List<String> worldNames;
    private Map<String, Room> rooms;

    public UHCGameManager() {
        this.games = new ArrayList<>();
        this.worldNames = new LinkedList<>();
        IntStream.range(1, Setting.getInstance().getMaxGameNumber()).forEach(i -> {
            worldNames.add(String.format(WORLDNAMEFORMATS, i));
        });
        this.rooms = new HashMap<>();
    }

    //UHCGame相关
    /**
     * 添加新一局游戏,并初始化世界
     */
    public synchronized boolean newGame(UHCGame game) {
        String worldName;
        synchronized(this) {
            //判断是否还有可分配的世界名
            if(worldNames.isEmpty()) {
                return false;
            }
            this.games.add(game);
            //分配世界名并创建世界
            worldName = worldNames.remove(0);
        }
        game.createWorld(worldName);
        return true;
    }

    /**
     * 结算游戏并在一定时间后完全结束游戏(删除世界并在列表中移除游戏)
     *
     * @param index 游戏索引
     *
     * @return 是否成功结束游戏
     */
    public synchronized boolean finishGame(int index) {
        if(index < 0) {
            return false;
        }
        UHCGame game = this.games.get(index);
        game.settle();
        UHCGameManager self = this;
        Bukkit.getServer().getScheduler().runTaskLater(SimpleUHC.getInstance(), () -> {
            //TODO 同样是线程问题
            String worldName = game.unloadWorld();
            synchronized(self) {
                if(worldName != null) {
                    worldNames.add(0, worldName);
                }
                games.remove(game);
            }
        }, Setting.getInstance().getDelayedTime());
        game.setGaming(false);

        return true;
    }

    /**
     * 判断一局游戏是否满足结束条件
     *
     * @return 游戏是否满足结束条件
     */
    public synchronized boolean canFinishing(int index) {
        UHCGame game = this.games.get(index);
        if(!game.isGaming()) {
            return false;
        }
        if(game.getPlayers().size() <= 1) {
            return true;
        }

        return false;
    }

    /**
     * 结束所有正在进行的游戏
     *
     * @param force 是否强制停止,如果强制停止,则不进行胜负判断,只执行删除世界操作
     *
     * @return 是否成功结束所有游戏(在强制停止的情况下总为true)
     */
    public boolean stopAll(boolean force) {
        if(force) {
            this.games.stream().forEach(game -> {
                game.unloadWorld();
            });
            this.games.clear();
            Setting.getInstance().getLogger().info("已停止所有正在进行的游戏");
        } else {
            boolean flag = true;
            while(!this.games.isEmpty()) {
                if(!this.finishGame(0)) {
                    flag = false;
                }
            }
            return flag;
        }
        return true;
    }

    /**
     * 获取玩家所在游戏的索引
     *
     * @param player 待搜索的玩家
     *
     * @return 如果在所有游戏中未找到玩家,返回-1,否则返回对应索引
     */
    public int getGameFromPlayer(Player player) {
        for(int i = 0; i < this.games.size(); i++) {
            if(this.games.get(i).getPlayers().stream().anyMatch(p -> p.getUniqueId().equals(player.getUniqueId()))) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 根据索引获取指定游戏
     */
    public UHCGame getGame(int index) {
        return this.games.get(index);
    }

    /**
     * 根据指定游戏获取对应索引
     *
     * @return 返回游戏对应的索引,未找到则返回-1
     */
    public int getIndexFromGame(UHCGame game) {
        for(int i = 0; i < this.games.size(); i++) {
            if(this.games.get(i) == game) {
                return i;
            }
        }
        return -1;
    }

    //Room相关

    /**
     * 将一个房间加入到房间列表,键为房间名
     *
     * @param room 要加入的房间
     *
     * @return 对应名字的房间是否已存在
     */
    public boolean newRoom(Room room) {
        if (rooms.containsKey(room.name())) {
            return false;
        }
        this.rooms.put(room.name(), room);
        return true;
    }

    /**
     * 将玩家加入到指定房间(如果要对比密码请先判断{@link xingchen.simpleuhc.game.room.RoomImpl#checkPassword})
     *
     * @param name 房间列表中存在的房间名
     * @param player 玩家
     *
     * @return 房间人数是否已满
     */
    public boolean joinRoom(String name, Player player) {
        Room room = this.rooms.get(name);
        if(room.getPlayers().size() < room.getMaxPlayerNumber()) {
            room.getPlayers().add(player.getUniqueId());
            return true;
        }
        return false;
    }

    /**
     * 玩家离开房间,如果玩家是房间的所有者则房间自动解散.
     * 房间所有者为null时允许0人房间
     *
     * @param name 房间列表中存在的房间名
     * @param player 玩家
     */
    public void leaveRoom(String name, Player player) {
        Room room = this.rooms.get(name);
        room.getPlayers().remove(player.getUniqueId());
    }

    /**
     * 在列表中搜索玩家所在的房间
     *
     * @param player 玩家
     *
     * @return 找到的房间名,未找到则返回空字符串("")
     */
    public String getRoomNameFromPlayer(Player player) {
        for(Map.Entry<String, Room> entry : this.rooms.entrySet()) {
            if(entry.getValue().getPlayers().contains(player.getUniqueId())) {
                return entry.getKey();
            }
        }
        return "";
    }

    /**
     * 获取房间列表
     * 应优先选用已实现的代理方法
     */
    public Map<String, Room> getRooms() {
        return this.rooms;
    }

    public static UHCGameManager getInstance() {
        return INSTANCE;
    }
}
