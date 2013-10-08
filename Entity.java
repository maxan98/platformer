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

    private final float terminalVelocity = 500; // Gravitational constant

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

    private int distanceToSlope(int j) {
        int pixMiddle = x + ((xBound - 1) / 2);
        int iMiddle = tiles.pixToTile(pixMiddle);
        
        float progress = ((float) (pixMiddle - tiles.tileToPix(iMiddle))) /
            tiles.getPixPerTile();
        
        return tiles.tileToPix(j) +
            (int) (tiles.getLeftY(iMiddle, j) * (1.0 - progress) +
                   tiles.getRightY(iMiddle, j) * progress)
            - (y + yBound - 1);
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

        boolean startsOnGround = onGround();

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
                for (j = jMin ; j <= jMax; j++) {
                    if (tiles.isSlope(i, j)) {
                        if (!tiles.isSlope(i-1, j) &&
                            tiles.getLeftY(i, j) + tiles.tileToPix(j) < y + yBound - 1) {
                            if (tiles.slopesLeft(i, j)) break;
                            if (tiles.slopesRight(i, j) && !tiles.isSlope(i-1, j+1)) break; 
                        }
                    }
                    if (tiles.isSolid(i, j)) {
                        if (!tiles.isSlope(i-1, j)) break;
                    }
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
                for (j = jMin ; j <= jMax; j++) {
                    if (tiles.isSlope(i, j)) {
                        if (!tiles.isSlope(i+1,j) &&
                            tiles.getRightY(i, j) + tiles.tileToPix(j) < y + yBound - 1) {
                            if (tiles.slopesRight(i, j)) break;
                            if (tiles.slopesLeft(i, j) && !tiles.isSlope(i+1,j+1)) break;
                        }
                    }
                    if (tiles.isSolid(i, j)) {
                        if (!tiles.isSlope(i+1, j)) break;
                        if (tiles.getLeftY(i+1, j) > 0) break;
                    }
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

        int oldPixMiddle = x + ((xBound - 1) / 2);
        int oldiMiddle = tiles.pixToTile(oldPixMiddle);
        
        deltaX += xRemainder;
        x += (int) deltaX;
        xRemainder = deltaX - (int) deltaX;

        iMin = tiles.pixToTile((int) x);
        iMax = tiles.pixToTile((int) x + xBound - 1);
        int pixMiddle = x + ((xBound - 1) / 2);
        int iMiddle = tiles.pixToTile(pixMiddle);

        float deltaY = ((float) (delta * dy)) / 1000;
        if (deltaY > 0) {
            int j = jMax;
            while (j < tiles.getHeight()) {
                int i;
                for (i = iMin; i <= iMax; i++) {
                    if (tiles.isSolid(i, j)) break;
                    if (tiles.isOneWay(i, j)) break;
                    if (tiles.isSlope(i, j) && i == iMiddle) break;
                    if (tiles.isSlope(i, j) && tiles.isEmpty(iMiddle, j)) {
                        if (i < iMiddle && tiles.slopesRight(i, j)) break;
                        if (i > iMiddle && tiles.slopesLeft(i, j)) break;
                    }
                }
                if (i <= iMax) break;
                j++;
            }

            int distanceToObstacle;
            if (tiles.isSlope(iMiddle, j)) {
                distanceToObstacle = distanceToSlope(j);
            } else {
                distanceToObstacle = tiles.tileToPix(j) - (y + yBound);
            }
            boolean collision = false;
            if (tiles.isSlope(oldiMiddle, jMax)) {
                if (distanceToObstacle < deltaY ||
                    (startsOnGround && distanceToObstacle < tiles.getPixPerTile())) {
                    collision = true;
                }
            } else {
                if (distanceToObstacle < deltaY && distanceToObstacle >= 0) {
                    collision = true;
                }
            }

            if (collision) {
                deltaY = (float) distanceToObstacle;
                yRemainder = 0;
                dy = 0;
            }
        } else if (deltaY < 0) {
            int j = jMin - 1;
            while (j >= 0) {
                int i;
                for (i = iMin; i <= iMax; i++) {
                    if (tiles.isSolid(i, j) || tiles.isSlope(i, j)) break;
                }
                if (i <= iMax) break;
                j--;
            }

            int distanceToObstacle = tiles.tileToPix(j) + tiles.getPixPerTile() - y;
            if (distanceToObstacle > deltaY && distanceToObstacle <= 0) {
                deltaY = (float) distanceToObstacle;
                yRemainder = 0;
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
        int pixMiddle = x + ((xBound - 1) / 2);
        int iMiddle = tiles.pixToTile(pixMiddle);
        int jBelowFeet = tiles.pixToTile(y + yBound);

        if (tiles.isSlope(iMiddle, jBelowFeet)) {
            return distanceToSlope(jBelowFeet) <= 0;
        }

        int i;
        for (i = iMin; i <= iMax; i++) {
            if (tiles.isSolid(i, jBelowFeet)) break;
            if (tiles.isOneWay(i, jBelowFeet)) break;
        }
        
        return i <= iMax;    
    }

    public boolean onOneWayPlatform() {
        int iMin = tiles.pixToTile(x);
        int iMax = tiles.pixToTile(x + xBound - 1);
        int pixMiddle = x + ((xBound - 1) / 2);
        int iMiddle = tiles.pixToTile(pixMiddle);
        int jBelowFeet = tiles.pixToTile(y + yBound);

        int i;
        boolean hasOneWay = false;
        for (i = iMin; i <= iMax; i++) {
            if (tiles.isOneWay(i, jBelowFeet)) hasOneWay = true;
            if (!tiles.isOneWay(i, jBelowFeet) && !tiles.isEmpty(i, jBelowFeet)) break;
        }
        
        return i > iMax && hasOneWay;    
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