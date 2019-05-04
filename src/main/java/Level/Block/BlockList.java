package Level.Block;

import Level.Coin;

import java.util.HashMap;

public class BlockList {
    static private final HashMap<String, Class<?>> list = new HashMap<String, Class<?>>() {
        {
            put("Coin", Coin.class);
            put("Ground", Ground.class);
            put("Ground2", Ground2.class);
            put("Brick", BrickSpawnTile.class);
            put("MarioSpawnTile", MarioSpawnTile.class);
            put("GoombaSpawnTile", GoombaSpawnTile.class);
            put("Breakablecrick", BreakableBrickSpawnTile.class);
        }
    };
    static public Class<?> getClassForName(String name) {
        Class<?> clazz = BlockList.list.get(name);
        if (clazz != null) {
            return clazz;
        } else {
            System.err.println("[BLOCKLIST] Block with name: \"".concat(name).concat("\" does not exist!"));
            return null;
        }
    }
}
