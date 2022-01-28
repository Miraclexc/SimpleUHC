package xingchen.simpleuhc.command;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xingchen.simpleuhc.SimpleUHC;
import xingchen.simpleuhc.config.Setting;
import xingchen.simpleuhc.game.UHCGame;
import xingchen.simpleuhc.game.UHCGameManager;
import xingchen.simpleuhc.game.room.Room;
import xingchen.simpleuhc.game.room.RoomImpl;
import xingchen.simpleuhc.language.UHCLanguage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UHCCommand implements TabExecutor {
    private CommandDictionary dictionary;

    public UHCCommand() {
        this.dictionary = new CommandDictionary(Arrays.stream(EnumSubCommand.values()).map(i -> i.getName()).toArray(String[]::new));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            sendHelp(sender);
            return true;
        }
        if(args[0].equalsIgnoreCase("gui")) {

        } else if(args[0].equalsIgnoreCase("create")) {
            if(args.length < 2) {
                sendMessage(sender, "command.message.needRoomName");
                return true;
            }
            String name = args[1];
            //判断房间名是否冲突
            if(UHCGameManager.getInstance().getRooms().containsKey(name)) {
                sendMessage(sender, "command.message.roomNameConflict");
                return true;
            }
            String password = "";
            if(args.length > 2) {
                password = args[2];
            }
            Room room = new RoomImpl(name, password);
            if(sender instanceof Player) {
                Player player = (Player) sender;
                //判断是否在已开始的游戏中
                if(UHCGameManager.getInstance().getGameFromPlayer(player) >= 0) {
                    sendMessage(sender, "command.message.inGame");
                    return true;
                }
                //判断是否在未开始的游戏中
                String roomName = UHCGameManager.getInstance().getRoomNameFromPlayer(player);
                if(!roomName.isEmpty()) {
                    sendMessage(sender, "command.message.inGame");
                    return true;
                }
                room.setOwner(player.getUniqueId());
                room.getPlayers().add(player.getUniqueId());
            }
            if(UHCGameManager.getInstance().newRoom(room)) {
                sendMessage(sender, "command.message.createRoomSucceed");
            } else {
                sendMessage(sender, "command.message.createRoomFail");
            }
        } else if(args[0].equalsIgnoreCase("join")) {
            if(!(sender instanceof Player)) {
                sendMessage(sender, "command.message.onlyPlayer");
                return true;
            }
            Player player = (Player) sender;
            //判断是否在已开始的游戏中
            if(UHCGameManager.getInstance().getGameFromPlayer(player) >= 0) {
                sendMessage(sender, "command.message.inGame");
                return true;
            }
            //判断是否在未开始的游戏中,如果是则自动退出
            String roomName = UHCGameManager.getInstance().getRoomNameFromPlayer(player);
            if(!roomName.isEmpty()) {
                if(player.getUniqueId().equals(UHCGameManager.getInstance().getRooms().get(roomName).getOwner())) {
                    sendMessage(sender, "command.message.ownerCannotLeave");
                    return true;
                } else {
                    UHCGameManager.getInstance().leaveRoom(roomName, player);
                }
                //player.performCommand("/spuhc leave " + roomName);
            }
            //随机查找未满人的房间
            if(args.length < 2) {
                //sendMessage(sender, "command.message.needRoomName");
                UHCGameManager.getInstance().getRooms().entrySet().stream().forEach(i -> {
                    if(i.getValue().getPlayers().size() < i.getValue().getMaxPlayerNumber() && i.getValue().getPassword().isEmpty()) {
                        if(!UHCGameManager.getInstance().joinRoom(i.getKey(), player)) {
                            sendMessage(sender, "command.message.roomFull");
                        }
                        return;
                    }
                });
                return true;
            }
            String name = args[1];
            //判断房间是否存在
            if(!UHCGameManager.getInstance().getRooms().containsKey(name)) {
                sendMessage(sender, "command.message.roomAbsence");
                ComponentBuilder builder = new ComponentBuilder(UHCLanguage.getInstance().translate("command.message.ask.createRoom"));
                builder.append(new ComponentBuilder("[" + UHCLanguage.getInstance().translate("command.button.yes") + "]")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/spuhc create " + name)).create());
                sender.spigot().sendMessage(builder.create());
                return true;
            }
            //对比房间密码
            String password = "";
            if(args.length > 2) {
                password = args[2];
            }
            if(!RoomImpl.checkPassword(UHCGameManager.getInstance().getRooms().get(name), password)) {
                sendMessage(sender, "command.message.wrongPassword");
                return true;
            }
            //加入房间
            if(UHCGameManager.getInstance().joinRoom(name, player)) {
                sender.sendMessage(String.format(UHCLanguage.getInstance().translate("command.message.joinRoom"), name));
            } else {
                sendMessage(sender, "command.message.roomFull");
            }
        } else if(args[0].equalsIgnoreCase("leave")) {
            if(!(sender instanceof Player)) {
                sendMessage(sender, "command.message.onlyPlayer");
                return true;
            }
            Player player = (Player) sender;
            String name = UHCGameManager.getInstance().getRoomNameFromPlayer(player);
            if(!name.isEmpty()) {
                Room room = UHCGameManager.getInstance().getRooms().get(name);
                //房间拥有者和非拥有者分开处理
                if(player.getUniqueId().equals(room.getOwner())) {
                    room.getPlayers().stream().forEach(i -> {
                        sendMessage(Bukkit.getServer().getPlayer(i), "command.message.roomDissolved");
                    });
                    UHCGameManager.getInstance().getRooms().remove(name);
                } else {
                    UHCGameManager.getInstance().leaveRoom(name, player);
                }
                sendMessage(sender, "command.message.leaveRoomSucceed");
            }
        } else if(args[0].equalsIgnoreCase("list")) {
            if(UHCGameManager.getInstance().getRooms().isEmpty()) {
                sendMessage(sender, "command.message.noAnyRoom");
                return true;
            }
            sendMessage(sender, "command.message.roomList");
            UHCGameManager.getInstance().getRooms().entrySet().stream().forEach(i -> {
                int currentNumber = i.getValue().getPlayers().size();
                int maxNumber = i.getValue().getMaxPlayerNumber();
                //颜色设置,满人显示红色,未满显示绿色
                String color = "§2";
                if(currentNumber >= maxNumber) {
                    color = "§4";
                }
                //文字点击事件(加入游戏)设置
                ComponentBuilder builder = new ComponentBuilder(String.format(UHCLanguage.getInstance().translate("command.message.roomListFormat"), color, i.getValue().name(), currentNumber, maxNumber));
                builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/spuhc join " + i.getKey()));
                sender.spigot().sendMessage(builder.create());
                //sender.sendMessage(String.format(UHCLanguage.getInstance().translate("command.message.roomListFormat"), color, i.getValue().name(), currentNumber, maxNumber));
            });
        } else if(args[0].equalsIgnoreCase("start")) {
            if(!(sender instanceof Player)) {
                sendMessage(sender, "command.message.onlyPlayer");
                return true;
            }
            Player player = (Player) sender;
            String name = UHCGameManager.getInstance().getRoomNameFromPlayer(player);
            //判断玩家是否在房间中
            if(name.isEmpty()) {
                sendMessage(sender, "command.message.noRoom");
                return true;
            }
            Room room = UHCGameManager.getInstance().getRooms().get(name);
            //判断玩家是否有开始游戏的权限
            if(!player.getUniqueId().equals(room.getOwner())) {
                sendMessage(sender, "command.message.notOwner");
                return true;
            }
            //判断房间人数是否满足要求
            if(room.getPlayers().size() < 2) {
                sendMessage(sender, "command.message.lackPlayers");
                return true;
            }
            //开始游戏
            List<Player> players = new ArrayList<>();
            room.getPlayers().stream().forEach(i -> {
                players.add(Bukkit.getServer().getPlayer(i));
            });
            UHCGameManager.getInstance().getRooms().remove(name);
            sender.sendMessage(String.format(UHCLanguage.getInstance().translate("command.message.currentPlayers"), players.stream().map(i -> i.getName()).collect(Collectors.joining(", "))));
            UHCGame game = new UHCGame(players, Setting.getInstance().generalUHCSetting());
            if(UHCGameManager.getInstance().newGame(game)) {
                game.start(SimpleUHC.getInstance());
            } else {
                sendMessage(sender, "command.message.full");
            }
        } else {
            sendHelp(sender);
        }
        /*if(args[0].equalsIgnoreCase("start")) {
            if(Setting.getInstance().getCentre() == null) {
                sendMessage(sender, "command.message.needCentre");
            } else if(Setting.getInstance().getArea() == null) {
                sendMessage(sender, "command.message.needRange");
            } else if(Setting.getInstance().getLastTime() <= 0) {
                sendMessage(sender, "command.message.needLastTime");
            } else {
                if(args.length == 1) {
                    if(Bukkit.getServer().getOnlinePlayers().size() < 2) {
                        sendMessage(sender, "command.message.lackPlayers");
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
                        sendMessage(sender, "command.message.full");
                    }
                }
            }
        }*/

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            return this.dictionary.getCommands();
        }
        if(args.length == 1) {
            return this.dictionary.search(args[0]);
        }
        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("join")) {
                return new ArrayList<>(UHCGameManager.getInstance().getRooms().keySet());
            }
        }
        return null;
    }

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(UHCLanguage.getInstance().translate(message));
    }

    public static void sendHelp(CommandSender sender) {
        String usageFormat = UHCLanguage.getInstance().translate("command.message.helpFormat");
        //sender.sendMessage(String.format(usageFormat, "/spuhc gui", "打开gui界面并显示所有房间"));
        sender.sendMessage(String.format(usageFormat, "/spuhc create <name> [password]", "创建一个新的房间"));
        sender.sendMessage(String.format(usageFormat, "/spuhc join [name] [password]", "加入对应房间,name为空则随机搜索房间加入(如果玩家在房间中会先自动退出房间再加入新房间)"));
        sender.sendMessage(String.format(usageFormat, "/spuhc leave", "离开当前房间"));
        sender.sendMessage(String.format(usageFormat, "/spuhc list", "查看所有可用房间"));
        sender.sendMessage(String.format(usageFormat, "/spuhc start", "开始游戏"));
    }
}
