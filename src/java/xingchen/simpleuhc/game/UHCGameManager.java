package xingchen.simpleuhc.game;

import xingchen.simpleuhc.config.Setting;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public class UHCGameManager {
    public static final String WORLDNAMEFORMATS = "spuhc-%d";

    private static final UHCGameManager INSTANCE = new UHCGameManager();

    private List<UHCGame> games;
    private List<String> worldNames;

    public UHCGameManager() {
        this.games = new ArrayList<>();
        this.worldNames = new LinkedList<>();
        IntStream.range(1, Setting.getInstance().getMaxGameNumber()).forEach(i -> {
            worldNames.add(String.format(WORLDNAMEFORMATS, i));
        });
    }

    public boolean newGame(UHCGame game) {
        if(worldNames.isEmpty()) {
            return false;
        }
        this.games.add(game);
        String worldName = worldNames.remove(0);
        game.createWolrd(worldName);
        return true;
    }

    public static UHCGameManager getInstance() {
        return INSTANCE;
    }
}
