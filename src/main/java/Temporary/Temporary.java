package Temporary;

import java.util.ArrayList;

public class Temporary {
    public static String[] groundChunkStringArray() {
        ArrayList<String> tileList = new ArrayList<>(81);
        for (int x = 0; x < 9; x++) {
            tileList.add("Ground");
        }
        for (int x = 0; x < 9; x++) {
            for (int y = 1; y < 9; y++) {
                tileList.add(null);
            }
        }
        return tileList.toArray(new String[]{});
    }

    public static String[] doubleGroundStringArray() {
        ArrayList<String> tileList = new ArrayList<>(81*2);
        for (int x = 0; x < 9*2; x++) {
            tileList.add("Ground");
        }
        for (int x = 0; x < 9*2; x++) {
            for (int y = 1; y < 9; y++) {
                tileList.add(null);
            }
        }
        tileList.set(2+2*9, "MarioSpawnTile");
        return tileList.toArray(new String[]{});
    }
}
