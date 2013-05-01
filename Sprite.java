import java.awt.*;

public class Sprite {
    private Image image;

    /* Creates a new sprite of the image. Automatically converts the image
       to a compatible color model. */

    public Sprite(Image image) {
        this.image = image;
    }

    public int getWidth() {
        return image.getWidth(null);
    }

    public int getHeight() {
        return image.getHeight(null);
    }

    public void draw(Graphics g, int x, int y) {
        g.drawImage(image, x, y, null);
    }
}