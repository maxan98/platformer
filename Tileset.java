import java.io.*;

public class Tileset {
    private int numTiles;
    private Tile tiles[];

    private int pixPerTile;

    public Tileset(String tilesetLocation) {
	BufferedReader br = null;
	try {
	    br = new BufferedReader(new FileReader(tilesetLocation));

	    this.numTiles = Integer.parseInt(br.readLine());
	    this.pixPerTile = Integer.parseInt(br.readLine());
	    this.tiles = new Tile[this.numTiles];

	    int i = 0;
	    String currentLine;
            while ((currentLine = br.readLine()) != null) {
		if (i >= this.numTiles) {
		    break;
		}
		String[] tokens = currentLine.split(" ");
		String tileName = tokens[0];
		int tileType = Integer.parseInt(tokens[1]);

		this.tiles[i] = new Tile();
		this.tiles[i].dirty = true;
		String spriteName = "./assets/tiles/" + tileName + ".png";
		this.tiles[i].sprite = SpriteStore.get().getSprite(spriteName);
		this.tiles[i].num = i;
		this.tiles[i].type = tileType;

		if (tileType == Tile.SLOPE) {
		    this.tiles[i].leftY = Integer.parseInt(tokens[2]);
		    this.tiles[i].rightY = Integer.parseInt(tokens[3]);
		}
		
		i++;
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

    public int getNumTiles() {
	return this.numTiles;
    }

    public int getPixPerTile() {
	return this.pixPerTile;
    }

    public Tile getNewTile(int tileNum) {
	return new Tile(this.tiles[tileNum]);
    }
}
