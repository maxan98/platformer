import java.awt.*;

public class Tiles {

    private class Tile {
        public Sprite sprite;
        public boolean solid;
        public boolean dirty; // true if this tile must be redrawn
        
        public Tile(String graphics_ref, boolean solid) {
            this.sprite = SpriteStore.get().getSprite(graphics_ref);
            this.solid = solid;
            this.dirty = true;
        }
    }
    
    private Tile board[][];
    private int xTiles, yTiles, pixPerTile;
    
    public Tiles(int xTiles, int yTiles, int pixPerTile) {
        this.xTiles = xTiles;
        this.yTiles = yTiles;
        this.pixPerTile = pixPerTile;
        board = new Tile[xTiles][yTiles];
    }

    public int getWidth() {
        return xTiles;
    }

    public int getHeight() {
        return yTiles;
    }

    public int getPixPerTile() {
        return pixPerTile;
    }

    public int pixToTile(int pixel) {
        return pixel/pixPerTile;
    }

    // returns the pixel of the left or top edge of the tile
    public int tileToPix(int tile) {
        return pixPerTile*tile;
    }

    public void setDirty(int x, int y) {
        if (x >= xTiles || y >= yTiles || x < 0 || y < 0) {
            return;
        }

        board[x][y].dirty = true;
    }

    public boolean isSolid(int x, int y) {
        if (x >= xTiles || y >= yTiles || x < 0 || y < 0) {
            return false;
        }

        return board[x][y].solid;
    }

    public void setTile(int x, int y, String graphic, boolean solid) {
        if (x >= xTiles || y >= yTiles || x < 0 || y < 0) {
            return;
        }

        board[x][y] = new Tile(graphic, solid);
    }

    public void draw(Graphics g) {
        for (int i = 0, x = 0; i < xTiles; i++, x+=pixPerTile) {
            for (int j = 0, y = 0; j < yTiles; j++, y+=pixPerTile) {
                if (board[i][j].dirty) {
                    board[i][j].sprite.draw(g, x, y);
                    board[i][j].dirty = false;
                }
            }
        }
    }
}