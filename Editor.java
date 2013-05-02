import java.awt.*;

public class Editor {
    private Tiles tiles;
    private long lastEdit;
    private int lastEditX, lastEditY;
    private final long minTimeBetweenEdits = 300;

    public Editor(Tiles tiles) {
        this.tiles = tiles;
        this.lastEdit = System.currentTimeMillis();
        this.lastEditX = -1;
        this.lastEditY = -1;
    }

    public void handleInput(InputListener input, Camera camera) {
        if (input.mouseDown()) {
            int x = tiles.pixToTile(camera.cameraToGlobalX(input.getMouseX()));
            int y = tiles.pixToTile(camera.cameraToGlobalY(input.getMouseY()));
            if (x != lastEditX || y != lastEditY
                || System.currentTimeMillis() - lastEdit > minTimeBetweenEdits) {
                if (tiles.isSolid(x, y)) {
                    tiles.setTile(x, y, "./assets/tiles/background.png", false);
                } else {
                    tiles.setTile(x, y, "./assets/tiles/block.png", true);
                }
                lastEditX = x;
                lastEditY = y;
                lastEdit = System.currentTimeMillis();
            }
        }
    }
        
}