import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import javax.imageio.ImageIO;

public class SpriteStore {
    // Implement singleton pattern
    private static final SpriteStore singleton = new SpriteStore();
    private SpriteStore() {}
    public static SpriteStore get() {
        return singleton;
    }

    // Member variables
    private HashMap sprites = new HashMap();
    
    public Sprite getSprite(String ref) {
        if (sprites.get(ref) != null) {
            return (Sprite) sprites.get(ref);
        }
        BufferedImage sourceImage = null;

        try {
            URL url = this.getClass().getClassLoader().getResource(ref);
            if (url == null) {
                fail("Can't find ref: "+ref);
            }

            sourceImage = ImageIO.read(url);
        } catch (IOException e) {
            fail("Failed to load: "+ref);
        }

        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        
        Image image = gc.createCompatibleImage(sourceImage.getWidth(),
                                               sourceImage.getHeight(),
                                               Transparency.BITMASK);
        image.getGraphics().drawImage(sourceImage, 0, 0, null);
        
        Sprite sprite = new Sprite(image);
        sprites.put(ref, sprite);

        return sprite;
    }

    private void fail(String message) {
        System.err.println(message);
        System.exit(0);
    }
}
