package Level.Block;

import java.util.HashMap;

public class BlockList {
    static private final HashMap<String, Class<?>> list = new HashMap<String, Class<?>>() {
        {
            put("Ground", Ground.class);
            put("Ground2", Ground2.class);
            put("MarioSpawnTile", MarioSpawnTile.class);
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
