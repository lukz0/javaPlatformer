package Level;

public class Tilemap {
    public final int chunkAmount;
    public final String[] tileNames;

    /**
     * @param chunkAmount The amount of chunks
     * @param tileNames   An array of tiles indexed by x + y*9*chunkAmount, the length of it must be 9*9*chunkAmount
     */
    public Tilemap(int chunkAmount, String[] tileNames) {
        this.chunkAmount = chunkAmount;
        this.tileNames = tileNames;
    }
}
