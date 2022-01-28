package xingchen.simpleuhc.game;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.*;
import xingchen.simpleuhc.language.UHCLanguage;

public class UHCScoreboard {
    protected Scoreboard scoreboard;
    /**侧边显示的计分版*/
    protected Objective sidebarObjective;
    /**剩余游戏时间*/
    protected Score time;

    public UHCScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    /**
     * 设置剩余游戏时间
     */
    public void setTime(int time) {
        this.time.setScore(time);
    }

    /**
     * 设置存活的玩家数
     *
     * @param currentNumber 存活的玩家数量
     * @param maxNumber 开局时的玩家数量
     */
    public void setPlayerNumber(int currentNumber, int maxNumber) {
        Score score = this.sidebarObjective.getScore(UHCLanguage.getInstance().translate("game.scoreboard.currentPlayerNumber"));
        score.setScore(currentNumber);
    }

    /**
     * 初始化侧边栏
     */
    public void init() {
        if(this.sidebarObjective == null) {
            this.sidebarObjective = this.scoreboard.registerNewObjective("sidebar", "dummy", UHCLanguage.getInstance().translate("game.scoreboard.sidebarTitle"));
            this.sidebarObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        this.time = this.sidebarObjective.getScore(UHCLanguage.getInstance().translate("game.scoreboard.time"));
    }

    /**
     * 清空所有分数
     * 如仍需使用,请在之后重新初始化{@link #init()}
     */
    public void clearScores() {
        this.scoreboard.getEntries().forEach(scoreboard::resetScores);
    }

    /**
     * 清空计分版内的对象
     */
    public void clearObjective() {
        this.scoreboard.getObjectives().stream().forEach(i -> i.unregister());
        this.sidebarObjective = null;
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    /**
     * 创建一个新的UHC计分版
     * 创建的计分版与原版计分版独立
     */
    public static UHCScoreboard createScoreboard() {
        Scoreboard scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
        UHCScoreboard uhcScoreboard = new UHCScoreboard(scoreboard);
        uhcScoreboard.init();
        return uhcScoreboard;
    }
}
