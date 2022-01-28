package xingchen.simpleuhc.event;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffectType;
import xingchen.simpleuhc.SimpleUHC;
import xingchen.simpleuhc.config.Setting;
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
            Bukkit.getServer().getScheduler().runTaskLater(SimpleUHC.getInstance(), () -> {
                player.setGameMode(GameMode.SPECTATOR);
                game.teleportPlayerToWorld(player);
                if(UHCGameManager.getInstance().canFinishing(index)) {
                    UHCGameManager.getInstance().finishGame(index);
                }
            }, 8);
        }
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        //判断是否在游戏中,是则令玩家死亡以出局
        int index = UHCGameManager.getInstance().getGameFromPlayer(event.getPlayer());
        if(index >= 0) {
            event.getPlayer().damage(1000000);
        }
        //判断是否在房间中,是则离开房间
        String name = UHCGameManager.getInstance().getRoomNameFromPlayer(event.getPlayer());
        if(!name.isEmpty()) {
            UHCGameManager.getInstance().leaveRoom(name, event.getPlayer());
        }
    }

    @EventHandler
    public void pluginDisabled(PluginDisableEvent event) {
        UHCGameManager.getInstance().stopAll(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerTeleport(PlayerTeleportEvent event) {
        if(event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL || event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            int index = UHCGameManager.getInstance().getGameFromPlayer(event.getPlayer());
            if(index >= 0) {
                if(UHCGameManager.getInstance().getGame(index).getWorldName().equals(event.getFrom().getWorld().getName())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
