import java.awt.*;

public class Editor {
    private Tiles tiles;
    private long lastClick;
    private long lastUp, lastDown, lastLeft, lastRight;
    private int lastClickX, lastClickY;
    private final long minTimeBetweenEdits = 100;
    
    private int cursorX, cursorY; // in global coordinates
    private int brushSize;

    private int currentTile;
    private Sprite currentTileSprite;

    private String levelName;

    public Editor(Tiles tiles, String levelName) {
        this.tiles = tiles;
        this.lastClick = System.currentTimeMillis();
        this.lastClickX = -1;
        this.lastClickY = -1;
        this.brushSize = 1;

        this.currentTile = 1;
        this.currentTileSprite = tiles.tileset.getNewTile(currentTile).sprite;
	
	this.levelName = levelName;
    }

    public void handleInput(InputListener input, Camera camera) {
        cursorX = camera.cameraToGlobalX(input.getMouseX());
        cursorY = camera.cameraToGlobalY(input.getMouseY());

        if (input.getKeyTyped(InputListener.SAVE)) { tiles.save(levelName); }

        if (input.getKeyTyped(InputListener.UP)) { brushSize++; }
        if (input.getKeyTyped(InputListener.DOWN)) {
            if (brushSize > 1) { brushSize--; }
        }

        if (input.getKeyTyped(InputListener.LEFT)) {
            currentTile--;
	    if (currentTile < 0) {
		currentTile = tiles.tileset.getNumTiles() - 1;
	    }
            currentTileSprite = tiles.tileset.getNewTile(currentTile).sprite;
        }
        if (input.getKeyTyped(InputListener.RIGHT)) {
            currentTile++;
	    if (currentTile >= tiles.tileset.getNumTiles()) {
		currentTile = 0;
	    }
            currentTileSprite = tiles.tileset.getNewTile(currentTile).sprite;
        }
        
        if (input.mouseDown() && System.currentTimeMillis() - lastClick > minTimeBetweenEdits) {
            int pixOffsetToTopLeft = (brushSize - 1) * tiles.getPixPerTile() / 2;
            int x = tiles.pixToTile(cursorX - pixOffsetToTopLeft);
            int y = tiles.pixToTile(cursorY - pixOffsetToTopLeft);
            for (int i = x; i < x + brushSize; i++) {
                for (int j = y; j < y + brushSize; j++) {
                    tiles.setTile(i, j, currentTile);
                }
            }
            lastClick = System.currentTimeMillis();
        } 
    }

    public void draw(Graphics g) {

        int pixOffsetToTopLeft = (brushSize - 1) * tiles.getPixPerTile() / 2;
        int x = tiles.pixToTile(cursorX - pixOffsetToTopLeft);
        int y = tiles.pixToTile(cursorY - pixOffsetToTopLeft);

        for (int i = x; i < x + brushSize; i++) {
            for (int j = y; j < y + brushSize; j++) {
                currentTileSprite.draw(g, tiles.tileToPix(i), tiles.tileToPix(j));
                tiles.setDirty(i, j);
            }
        }
    }
}
