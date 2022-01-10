package xingchen.simpleuhc;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xingchen.simpleuhc.config.Setting;
import xingchen.simpleuhc.game.UHCGame;
import xingchen.simpleuhc.game.UHCGameManager;

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
                    if(UHCGameManager.getInstance().newGame(game)) {
                        sender.sendMessage("游戏即将开始……");
                        game.start(SimpleUHC.getInstance());
                    } else {
                        sender.sendMessage("正在进行的游戏数已达上限，请耐心等待");
                    }
                }
            }
        }

        return true;
    }

    public static void sendHelp(CommandSender sender) {

    }
}
