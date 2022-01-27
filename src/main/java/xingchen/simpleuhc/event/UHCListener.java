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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
                //TODO 线程
                player.setGameMode(GameMode.SPECTATOR);
                game.teleportPlayerToWorld(player);
                if(UHCGameManager.getInstance().canFinishing(index)) {
                    UHCGameManager.getInstance().finishGame(index);
                }
            }, 8);
        }
    }

    /*@EventHandler(priority = EventPriority.LOWEST)
    public void playerFalling(EntityDamageEvent event) {
        event.getEntity().sendMessage(event.getCause().toString() + ";" + event.getDamage());
        if(event.getEntity() instanceof Player && EntityDamageEvent.DamageCause.FALL.equals(event.getCause())) {
            Player player = (Player) event.getEntity();
            if(player.hasMetadata(Setting.METADATA_NOFALL)) {
                event.setCancelled(true);
                event.setDamage(0);
                player.removeMetadata(Setting.METADATA_NOFALL, SimpleUHC.getInstance());
                //移除玩家身上所有药水效果
                //player.getActivePotionEffects().stream().forEach(i -> player.removePotionEffect(i.getType()));
            }
        }
    }*/

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {

    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        int index = UHCGameManager.getInstance().getGameFromPlayer(event.getPlayer());
        if(index >= 0) {
            event.getPlayer().damage(1000000);
        }
        String name = UHCGameManager.getInstance().getRoomNameFromPlayer(event.getPlayer());
        if(!name.isEmpty()) {
            UHCGameManager.getInstance().leaveRoom(name, event.getPlayer());
        }
    }

    @EventHandler
    public void pluginDisabled(PluginDisableEvent event) {
        UHCGameManager.getInstance().stopAll(true);
    }
}
