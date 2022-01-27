package xingchen.simpleuhc.game.room;

import java.util.List;
import java.util.UUID;

public interface Room {
    public String name();

    public int getMaxPlayerNumber();

    public void setMaxPlayerNumber(int number);

    public String getPassword();

    public UUID getOwner();

    public void setOwner(UUID owner);

    public List<UUID> getPlayers();

    public boolean start();
}
