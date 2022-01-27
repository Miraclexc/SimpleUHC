package xingchen.simpleuhc.game.room;

import java.util.List;
import java.util.UUID;

public interface Room {
    /**
     * 房间名字(一般作为Map的主键)
     */
    public String name();

    /**
     * 获取房间最大允许的玩家数
     */
    public int getMaxPlayerNumber();

    /**
     * 设置房间最大允许的玩家数
     */
    public void setMaxPlayerNumber(int number);

    /**
     * 获取房间密码
     */
    public String getPassword();

    /**
     * 获取房间的拥有者
     *
     * @return 拥有者的UUID,可以为空(用于特定用途)
     */
    public UUID getOwner();

    /**
     * 设置房间拥有者
     *
     * @param owner 拥有者,可以为空
     */
    public void setOwner(UUID owner);

    /**
     * 获取房间中所有的玩家
     * 是否删除离线玩家视用途而定
     */
    public List<UUID> getPlayers();

    /**
     * 开始游戏
     *
     * @return 是否成功开始游戏
     */
    public boolean start();
}
