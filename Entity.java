import java.awt.*;

public class Entity {
    protected int x, y;
    // Each entity has a bounding box extending from (x,y) in the upper left
    // to (x + xBound, y + yBound) in the lower right
    protected int xBound, yBound;  
    protected float xRemainder, yRemainder;
    protected float targetDx, targetDy;
    protected float dx, dy;
    protected float xAcceleration;
    protected float yAcceleration;

    protected Sprite sprite;
    protected Tiles tiles;

    protected boolean gravityAffected;
    protected int facing; // positive if facing right, negative otherwise

    private final float terminalVelocity = 1000; // Gravitational constant

    public Entity(Tiles tiles, String graphics_ref, int x, int y) {
        this.sprite = SpriteStore.get().getSprite(graphics_ref);
        this.x = x;
        this.y = y;
        this.xBound = sprite.getWidth();
        this.yBound = sprite.getHeight();
        this.tiles = tiles;
        this.facing = 1;
    }

    public int getCenterX() {
        return this.x + (this.xBound / 2);
    }

    public int getCenterY() {
        return this.y + (this.yBound / 2);
    }
    
    public int getFacing() {
        return this.facing;
    }

    public void move(long delta) {
        int iMin = tiles.pixToTile(x);
        int iMax = tiles.pixToTile(x + xBound - 1);
        int jMin = tiles.pixToTile(y);
        int jMax = tiles.pixToTile(y + yBound - 1);

        for (int i = iMin; i <= iMax; i++) {
            for (int j = jMin; j <= jMax; j++) {
                tiles.setDirty(i, j);
            }
        }

        float deltaX = ((float) (delta * dx)) / 1000;
        if (deltaX > 0) {
            facing = 1;
        } else if (deltaX < 0) {
            facing = -1;
        }

        // For each line the bounding box intersects, scan for an obstacle
        if (deltaX > 0) {
            int i = iMax + 1;
            while (i < tiles.getWidth()) {
                int j;
                for (j = jMin; j <= jMax; j++) {
                    if (tiles.isSolid(i, j)) break;
                }
                if (j <= jMax) break;
                i++;
            }

            int distanceToObstacle = tiles.tileToPix(i) - (x + xBound);
            if (distanceToObstacle < deltaX) {
                deltaX = (float) distanceToObstacle;
                dx = 0;
            }
        } else if (deltaX < 0) {
            int i = iMin - 1;
            while (i >= 0) {
                int j;
                for (j = jMin; j <= jMax; j++) {
                    if (tiles.isSolid(i, j)) break;
                }
                if (j <= jMax) break;
                i--;
            }

            int distanceToObstacle = tiles.tileToPix(i) + tiles.getPixPerTile() - x;
            if (distanceToObstacle > deltaX) {
                deltaX = (float) distanceToObstacle;
                dx = 0;
            }
        }
        
        deltaX += xRemainder;
        x += (int) deltaX;
        xRemainder = deltaX - (int) deltaX;

        iMin = tiles.pixToTile((int) x);
        iMax = tiles.pixToTile((int) x + xBound - 1);

        float deltaY = ((float) (delta * dy)) / 1000;

        if (deltaY > 0) {
            int j = jMax + 1;
            while (j < tiles.getHeight()) {
                int i;
                for (i = iMin; i <= iMax; i++) {
                    if (tiles.isSolid(i, j)) break;
                }
                if (i <= iMax) break;
                j++;
            }
            
            int distanceToObstacle = tiles.tileToPix(j) - (y + yBound);
            if (distanceToObstacle < deltaY) {
                deltaY = (float) distanceToObstacle;
                dy = 0;
            }

        } else if (deltaY < 0) {
            int j = jMin - 1;
            while (j >= 0) {
                int i;
                for (i = iMin; i <= iMax; i++) {
                    if (tiles.isSolid(i, j)) break;
                }
                if (i <= iMax) break;
                j--;
            }

            int distanceToObstacle = tiles.tileToPix(j) + tiles.getPixPerTile() - y;
            if (distanceToObstacle > deltaY) {
                deltaY = (float) distanceToObstacle;
                dy = 0;
            }
        }

        deltaY += yRemainder;
        y += (int) deltaY;
        yRemainder = deltaY - (int) deltaY;
    }

    public boolean onGround() {
        int iMin = tiles.pixToTile(x);
        int iMax = tiles.pixToTile(x + xBound - 1);
        int jBelowFeet = tiles.pixToTile(y + yBound);

        int i;
        for (i = iMin; i <= iMax; i++) {
            if (tiles.isSolid(i, jBelowFeet)) break;
        }
        
        return i <= iMax;    
    }


    public void updatePhysics(long delta) {
        boolean onGround = onGround();

        if (gravityAffected && !onGround) {
            targetDy = terminalVelocity;
        }

        if (dx < targetDx) {
            if (onGround) {
                dx += xAcceleration * delta / 1000;
            } else {
                dx += 0.5 * xAcceleration * delta / 1000;
            }

            if (dx > targetDx) dx = targetDx;

        } else if (dx > targetDx) {
            if (onGround) {
                dx -= xAcceleration * delta / 1000;
            } else {
                dx -= 0.5 * xAcceleration * delta / 1000;
            }

            if (dx < targetDx) dx = targetDx;
        }

        if (dy < targetDy) {
            dy += yAcceleration * delta / 1000;
            if (dy > targetDy) dy = targetDy;
        } else if (dy > targetDy) {
            dy -= yAcceleration * delta / 1000;
            if (dy < targetDy) dy = targetDy;
        }
    }

    public void draw(Graphics g) {
        sprite.draw(g, (int) x, (int) y);
    }
 }