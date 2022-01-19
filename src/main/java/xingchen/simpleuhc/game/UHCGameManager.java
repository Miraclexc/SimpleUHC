package xingchen.simpleuhc.game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xingchen.simpleuhc.SimpleUHC;
import xingchen.simpleuhc.config.Setting;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public class UHCGameManager {
    public static final String WORLDNAMEFORMATS = "spuhc-%d";

    private static final UHCGameManager INSTANCE = new UHCGameManager();

    private List<UHCGame> games;
    private List<String> worldNames;

    public UHCGameManager() {
        this.games = new ArrayList<>();
        this.worldNames = new LinkedList<>();
        IntStream.range(1, Setting.getInstance().getMaxGameNumber()).forEach(i -> {
            worldNames.add(String.format(WORLDNAMEFORMATS, i));
        });
    }

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
        UHCGame game = this.games.get(index);
        game.settle(false);
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

    public static UHCGameManager getInstance() {
        return INSTANCE;
    }
}
