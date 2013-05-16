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

    public Editor(Tiles tiles) {
        this.tiles = tiles;
        this.lastClick = System.currentTimeMillis();
        this.lastClickX = -1;
        this.lastClickY = -1;
        this.brushSize = 1;

        this.currentTile = 1;
        this.currentTileSprite = tiles.tileset.getNewTile(currentTile).sprite;
    }

    public void handleInput(InputListener input, Camera camera) {
        cursorX = camera.cameraToGlobalX(input.getMouseX());
        cursorY = camera.cameraToGlobalY(input.getMouseY());

        if (input.getKeyTyped(InputListener.UP)) { brushSize++; }
        if (input.getKeyTyped(InputListener.DOWN)) {
            if (brushSize > 1) { brushSize--; }
        }

        if (input.getKeyTyped(InputListener.LEFT)) {
            currentTile--;
            currentTileSprite = tiles.tileset.getNewTile(currentTile).sprite;
        }
        if (input.getKeyTyped(InputListener.RIGHT)) {
            currentTile++;
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