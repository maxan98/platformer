import java.awt.*;
import java.awt.image.BufferedImage;

public class Camera {
    private int x, y; // upper left
    private int xRes, yRes;

    public Camera(int xRes, int yRes) {
        this.xRes = xRes;
        this.yRes = yRes;
    }

    public void update(Player player, Tiles tiles) {
        int playerX = player.getCenterX();
        int playerY = player.getCenterY();
        int maxX = tiles.getWidth() * tiles.getPixPerTile();
        int maxY = tiles.getHeight() * tiles.getPixPerTile();
        
        x = playerX - (xRes/2);
        y = playerY - (yRes/2);

        if (x + xRes > maxX) {
            x = maxX - xRes - 1;
        } else if (x < 0) {
            x = 0;
        }

        if (y + yRes > maxY) {
            y = maxY - yRes - 1;
        } else if (y < 0) {
            y = 0;
        }
    }

    public void render(Graphics g, BufferedImage image) {
        BufferedImage view = image.getSubimage(x, y, xRes, yRes);
        g.drawImage(view, 0, 0, null);
    }

    public int cameraToGlobalX(int xPix) {
        return x + xPix;
    }

    public int cameraToGlobalY(int yPix) {
        return y + yPix;
    }
}