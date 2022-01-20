package xingchen.simpleuhc.event;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xingchen.simpleuhc.game.UHCGame;
import xingchen.simpleuhc.game.UHCGameManager;

import java.util.List;

public class UHCListener implements Listener {
    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        int index = UHCGameManager.getInstance().getGameFromPlayer(event.getEntity());
        if(index >= 0) {
            Player player = event.getEntity();
            UHCGame game = UHCGameManager.getInstance().getGame(index);
            player.spigot().respawn();
            List<Player> players = game.getPlayers();
            for(int i = 0; i < players.size(); i++) {
                if(players.get(i).getUniqueId().equals(player.getUniqueId())) {
                    players.remove(i);
                    break;
                }
            }
            player.setGameMode(GameMode.SPECTATOR);
            game.teleportPlayerToWorld(player);
            if(UHCGameManager.getInstance().canFinishing(index)) {
                UHCGameManager.getInstance().finishGame(index);
            }
        }
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {

    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        int index = UHCGameManager.getInstance().getGameFromPlayer(event.getPlayer());
        if(index >= 0) {
            event.getPlayer().damage(1000000);
        }
    }
}
