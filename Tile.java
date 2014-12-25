public class Tile {
    // Must this tile be redrawn next frame?
    public boolean dirty;

    public Sprite sprite;

    // The y-coordinate of the solid edge of this tile on the left and right
    // sides, with 0 being the top of the tile.
    public int leftY;
    public int rightY;

    // Index of this tile in the tileset. Written to and read from the level
    // save file.
    public int num;

    // Integer representing the type of this tile with respect to collision
    // detection.
    public int type;
    
    // Possible values of 'type'.
    public static final int NON_SOLID = 0;
    public static final int SOLID = 1;
    public static final int SLOPE = 2;
    public static final int ONE_WAY = 3;

    public Tile() {};
}
