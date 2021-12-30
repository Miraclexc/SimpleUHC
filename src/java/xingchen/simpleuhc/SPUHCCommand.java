package xingchen.simpleuhc;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xingchen.simpleuhc.config.Setting;
import xingchen.simpleuhc.game.UHCGame;

import java.util.ArrayList;
import java.util.List;

public class SPUHCCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.isOp()) {
            return false;
        }
        if(args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }
        if(args[0].equalsIgnoreCase("start")) {
            if(Setting.getInstance().getCentre() == null) {
                sender.sendMessage("请设置游戏地图的中心");
            } else if(Setting.getInstance().getArea() == null) {
                sender.sendMessage("请设置游戏地图的范围大小");
            } else if(Setting.getInstance().getLastTime() <=0) {
                sender.sendMessage("游戏时间需要为非0整数(单位:秒)");
            } else {
                if(args.length == 1) {
                    List<Player> plays = new ArrayList<>();
                    Bukkit.getServer().getOnlinePlayers().stream().forEach(player -> {
                        plays.add(player);
                    });
                    UHCGame game = new UHCGame(plays, Setting.getInstance().generalSetting());

                }
            }
        }

        return true;
    }

    public static void sendHelp(CommandSender sender) {

    }
}
