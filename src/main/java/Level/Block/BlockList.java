package Level.Block;

import java.util.HashMap;

public class BlockList {
    static private final HashMap<String, Class<?>> list = new HashMap<String, Class<?>>() {
        {
            put("Ground", Ground.class);
            put("Ground2", Ground2.class);
        }
    };
    static public Class<?> getClassForName(String name) {
        return BlockList.list.get(name);
    }
}
