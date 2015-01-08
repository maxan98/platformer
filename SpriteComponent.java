import java.awt.Graphics;

public class SpriteComponent {
    public Sprite sprite;

    public PositionComponent pc;

    public void update(Graphics g) {
	sprite.draw(g, pc.x, pc.y);
    }
}
