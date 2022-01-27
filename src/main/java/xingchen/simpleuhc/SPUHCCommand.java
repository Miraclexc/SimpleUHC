package xingchen.simpleuhc;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xingchen.simpleuhc.config.Setting;
import xingchen.simpleuhc.game.UHCGame;
import xingchen.simpleuhc.game.UHCGameManager;
import xingchen.simpleuhc.language.UHCLanguage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SPUHCCommand implements CommandExecutor {
    public static final String USAGEFORMAT = "%s - %s";

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
                sender.sendMessage(UHCLanguage.getInstance().translate("command.message.needCentre"));
            } else if(Setting.getInstance().getArea() == null) {
                sender.sendMessage(UHCLanguage.getInstance().translate("command.message.needRange"));
            } else if(Setting.getInstance().getLastTime() <=0) {
                sender.sendMessage(UHCLanguage.getInstance().translate("command.message.needLastTime"));
            } else {
                if(args.length == 1) {
                    if(Bukkit.getServer().getOnlinePlayers().size() < 2) {
                        sender.sendMessage(UHCLanguage.getInstance().translate("command.message.lackPlayers"));
                        return true;
                    }
                    List<Player> players = new ArrayList<>();
                    Bukkit.getServer().getOnlinePlayers().stream().forEach(i -> {
                        players.add(i);
                    });
                    sender.sendMessage(String.format(UHCLanguage.getInstance().translate("command.message.currentPlayers"), players.stream().map(i -> i.getName()).collect(Collectors.joining(", "))));
                    UHCGame game = new UHCGame(players, Setting.getInstance().generalSetting());
                    if(UHCGameManager.getInstance().newGame(game)) {
                        game.start(SimpleUHC.getInstance());
                    } else {
                        sender.sendMessage(UHCLanguage.getInstance().translate("command.message.full"));
                    }
                }
            }
        }

        return true;
    }

    public static void sendHelp(CommandSender sender) {
        sender.sendMessage(String.format(USAGEFORMAT, "/uhc start", "为所有玩家开启一个新游戏"));
    }
}
