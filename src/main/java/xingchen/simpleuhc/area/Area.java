package xingchen.simpleuhc.area;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public interface Area {
    /**
     * 判断玩家是否在范围中
     *
     * @param player 玩家
     * @param origin 原点
     */
    public boolean isInArea(Player player, Location origin);

    /**
     * 设置世界边界
     * 由于原版mc边界只能为正方形,因此限制较大
     *
     * @param worldBorder 要设置的世界边界
     */
    public void setBorder(WorldBorder worldBorder);

    /**
     * 边界缩放
     * 配合边界随时间缩小的游戏模式使用
     *
     * @param worldBorder 要设置的世界边界
     * @param coefficient 用于控制最大边界和最小边界的缩放系数,介于0~1,根据区域类的不同而有不同算法
     */
    public void scaleBorder(WorldBorder worldBorder, double coefficient);

    /**
     * 将实体随机传送到区域内的一点(保证实体在对应坐标最顶层的方块上)
     *
     * @param entities 实体列表
     * @param world 随机传送到的世界
     */
    public void spread(List<? extends Entity> entities, World world);
}
