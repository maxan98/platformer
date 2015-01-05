import java.awt.*;
import java.io.*;

public class Tiles {

    private Tile board[][];
    private int xTiles, yTiles;
    public Tileset tileset;
    public int startingPlayerX;
    public int startingPlayerY;

    private String tilesetName;
    
    public Tiles(int xTiles, int yTiles, String tilesetName) {
        this.xTiles = xTiles;
        this.yTiles = yTiles;
        this.startingPlayerX = 100;
        this.startingPlayerY = 100;
        this.board = new Tile[xTiles][yTiles];
        this.tileset = new Tileset("./assets/tilesets/" + tilesetName);
	this.tilesetName = tilesetName;
    }

    public Tiles(String filename) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filename));
            this.xTiles = Integer.parseInt(br.readLine());
            this.yTiles = Integer.parseInt(br.readLine());
	    this.tilesetName = br.readLine();
            String tilesetLocation = "./assets/tilesets/" + this.tilesetName;
            this.tileset = new Tileset(tilesetLocation);
            this.startingPlayerX = Integer.parseInt(br.readLine());
            this.startingPlayerY = Integer.parseInt(br.readLine());

            board = new Tile[xTiles][yTiles];
            int j = 0;

            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                if (j >= yTiles) break;
                String[] tokens = currentLine.split(" ");

                for (int i = 0; i < xTiles; i++) {
                    if (i >= tokens.length) break;
                    int tileNum = Integer.parseInt(tokens[i]);
                    board[i][j] = tileset.getNewTile(tileNum);
                }

                j++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } finally {
            try {
                if (br != null) { br.close(); }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public int getWidth() {
        return xTiles;
    }

    public int getHeight() {
        return yTiles;
    }

    public int getPixPerTile() {
        return tileset.getPixPerTile();
    }

    public int pixToTile(int pixel) {
        return pixel/getPixPerTile();
    }

    // returns the pixel of the left or top edge of the tile
    public int tileToPix(int tile) {
        return getPixPerTile()*tile;
    }

    public void setDirty(int x, int y) {
        assert x < xTiles;
	assert y < yTiles;
	assert x >= 0;
	assert y >= 0;

        board[x][y].dirty = true;
    }

    public boolean isEmpty(int x, int y) {
        assert x < xTiles;
	assert y < yTiles;
	assert x >= 0;
	assert y >= 0;

        return board[x][y].type == Tile.NON_SOLID;

    }

    public boolean isSolid(int x, int y) {
        assert x < xTiles;
	assert y < yTiles;
	assert x >= 0;
	assert y >= 0;

        return board[x][y].type == Tile.SOLID;
    }

    public boolean isSlope(int x, int y) {
        assert x < xTiles;
	assert y < yTiles;
	assert x >= 0;
	assert y >= 0;

        return board[x][y].type == Tile.SLOPE;
    }

    public boolean isOneWay(int x, int y) {
	assert x < xTiles;
	assert y < yTiles;
	assert x >= 0;
	assert y >= 0;

        return board[x][y].type == Tile.ONE_WAY;
    }

    public int getLeftY(int x, int y) {
        assert x < xTiles;
	assert y < yTiles;
	assert x >= 0;
	assert y >= 0;

        return board[x][y].leftY;
    }

    public int getRightY(int x, int y) {
        assert x < xTiles;
	assert y < yTiles;
	assert x >= 0;
	assert y >= 0;

        return board[x][y].rightY;
    }

    public boolean slopesRight(int x, int y) {
        assert x < xTiles;
	assert y < yTiles;
	assert x >= 0;
	assert y >= 0;
        
        return getRightY(x,y) < getLeftY(x,y);
    }
    
    public boolean slopesLeft(int x, int y) { return !slopesRight(x,y); }
    
    public void setTile(int x, int y, int tileNum) {
        assert x < xTiles;
	assert y < yTiles;
	assert x >= 0;
	assert y >= 0;

        int maxTileNum = tileset.getNumTiles();
	
        assert tileNum < maxTileNum;
	assert tileNum >= 0;

        board[x][y] = tileset.getNewTile(tileNum);
    }

    public void draw(Graphics g) {
        int ppt = getPixPerTile();
        for (int i = 0, x = 0; i < xTiles; i++, x+=ppt) {
            for (int j = 0, y = 0; j < yTiles; j++, y+=ppt) {
                if (board[i][j].dirty) {
		    tileset.getBackgroundTile().sprite.draw(g, x, y);
                    board[i][j].sprite.draw(g, x, y);
                    board[i][j].dirty = false;
                }
            }
        }
    }

    public void save(String filename) {
        try {
            File file = new File("./assets/levels/" + filename);
            file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            
            bw.write(Integer.toString(xTiles));
            bw.newLine();
            bw.write(Integer.toString(yTiles));
            bw.newLine();
            bw.write(this.tilesetName);
            bw.newLine();
            bw.write(Integer.toString(startingPlayerX));
            bw.newLine();
            bw.write(Integer.toString(startingPlayerY));
            bw.newLine();
            for (int j = 0; j < yTiles; j++) {
                for (int i = 0; i < xTiles; i++) {
                    bw.write(Integer.toString(board[i][j].num));
                    bw.write(" ");
                }
                bw.newLine();
            }
            

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
