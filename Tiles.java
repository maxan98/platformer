import java.awt.*;

public class Tiles {

    private Tile board[][];
    private int xTiles, yTiles, pixPerTile;
    public Tileset tileset;
    
    public Tiles(int xTiles, int yTiles, int pixPerTile, String tilesetLocation) {
        this.xTiles = xTiles;
        this.yTiles = yTiles;
        this.pixPerTile = pixPerTile;
        board = new Tile[xTiles][yTiles];

        tileset = new Tileset(tilesetLocation, pixPerTile);
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

    public void setTile(int x, int y, int tileNum) {
        if (x >= xTiles || y >= yTiles || x < 0 || y < 0) {
            return;
        }

        int maxTileNum = tileset.getNumTiles();

        board[x][y] = tileset.getNewTile(tileNum);
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