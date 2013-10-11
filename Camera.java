import java.awt.*;
import java.awt.image.BufferedImage;

public class Camera {
    public int x, y; // upper left
    private float xRemainder, yRemainder;
    private int xTarget, yTarget;
    private int xRes, yRes;
    private int dx, dy;
    private int maxXSpeed = 300;
    private int maxYSpeedUp = 150;
    private int maxYSpeedDown = 150;
    private int targetDx, targetDy;
    private int acceleration = 750;
    private int screenClearance = 100; // Minimum number of pixels between
                                       // character and edge of screen
    static final int LOCKED = 0;
    static final int FOLLOW = 1;
    static final int FACING = 2;
    static final int SMART = 3;
    static final int DRUNK = 4;
    
    private int mode;

    public Camera(int xRes, int yRes, int x, int y, int mode) {
        this.xRes = xRes;
        this.yRes = yRes;

        this.x = x;
        this.y = y;
        this.xTarget = x;
        this.yTarget = y;

        if (mode != LOCKED &&mode != FOLLOW && mode != FACING && mode != SMART && mode != DRUNK) {
            this.mode = LOCKED;
        } else {
            this.mode = mode;
        }
    }

    public void update(Player player, Tiles tiles, long delta) {
        int playerX = player.getCenterX();
        int playerY = player.getCenterY();
        int maxX = tiles.getWidth() * tiles.getPixPerTile();
        int maxY = tiles.getHeight() * tiles.getPixPerTile();

        // First we figure out the target location, based on the
        // style of camera we're using. 
        switch (mode) {
        case LOCKED:
            break;
        case FOLLOW:
            xTarget = playerX - (int) (xRes*.5);
            yTarget = playerY - (int) (yRes*.5);
            break;
        case FACING:
            if (player.getFacing() > 0) {
                xTarget = playerX - (int) (xRes*.4);
            } else {
                xTarget = playerX - (int) (xRes*.6);
            }
            yTarget = playerY - (int) (yRes*.5);
            break;
        case DRUNK: // fall-through
        case SMART:
            if (player.getFacing() > 0) {
                xTarget = playerX - (int) (xRes*.4);
            } else {
                xTarget = playerX - (int) (xRes*.6);
            }
            if (playerY > player.lastGroundY() || player.onGround()) {
                yTarget = playerY - (int) (yRes*.5);
            }
            break;
        default:
            break;
        }

        // Next we adjust the screen to follow the character if it's
        // about to go off the screen
        if (xTarget + xRes - playerX < screenClearance) {
            xTarget = screenClearance + playerX - xRes;
        } else if (playerX - xTarget < screenClearance) {
            xTarget = playerX - screenClearance;
        }

        if (yTarget + yRes - playerY < screenClearance) {
            yTarget = screenClearance + playerY - yRes;
        } else if (playerY - yTarget < screenClearance) {
            yTarget = playerY - screenClearance;
        }

        // Next, we make sure the camera doesn't scroll off the map.
        if (xTarget + xRes > maxX) {
            xTarget = maxX - xRes - 1;
        } else if (xTarget < 0) {
            xTarget = 0;
        }

        if (yTarget + yRes > maxY) {
            yTarget = maxY - yRes - 1;
        } else if (yTarget < 0) {
            yTarget = 0;
        }


        if (mode == DRUNK) {
            if (xTarget > x) {
                targetDx = maxXSpeed;
            } else if (xTarget < x) {
                targetDx = -1 * maxXSpeed;
            } else {
                targetDx = 0;
            }
            
            if (yTarget > y) {
                targetDy = maxYSpeedDown;
            } else if (yTarget < y) {
                targetDy = -1 * maxYSpeedUp;
            } else {
                targetDy = 0;
            }
            
            if (dx < targetDx) {
                dx += acceleration * delta / 1000;
                if (dx > targetDx) dx = targetDx;
            } else if (dx > targetDx) {
                dx -= acceleration * delta / 1000;
                if (dx < targetDx) dx = targetDx;
            }
            
            if (dy < targetDy) {
                dy += acceleration * delta / 1000;
                if (dy > targetDy) dy = targetDy;
            } else if (dy > targetDy) {
                dy -= acceleration * delta / 1000;
                if (dy < targetDy) dy = targetDy;
            }
        } else { // non-DRUNK
            if (xTarget > x) {
                dx = maxXSpeed;
            } else if (xTarget < x) {
                dx = -1 * maxXSpeed;
            } else {
                dx = 0;
            }
            
            if (yTarget > y) {
                dy = maxYSpeedDown;
            } else if (yTarget < y) {
                dy = -1 * maxYSpeedUp;
            } else {
                dy = 0;
            }
        }
            
        // Next we move the actual location towards the target
        // location, not exceeding the max speed.
        float deltaX = ((float) (dx * delta)) / 1000;
        if (xTarget > x) {
            if (deltaX > xTarget - x) deltaX = xTarget - x;
        } else if (xTarget < x) {
            if (deltaX < xTarget - x) deltaX = xTarget - x;
        }

        deltaX += xRemainder;
        x += (int) deltaX;
        xRemainder = deltaX - (int) deltaX;

        float deltaY = ((float) (dy * delta)) / 1000;
        if (yTarget > y) {
            if (deltaY > yTarget - y) deltaY = yTarget - y;
        } else if (yTarget < y) {
            if (deltaY < yTarget - y) deltaY = yTarget - y;
        }

        deltaY += yRemainder;
        y += (int) deltaY;
        yRemainder = deltaY - (int) deltaY;

        // Even though we did these checks at the beginning for targetX/Y,
        // we need to do them again for the real x and y.

        // We adjust the screen to follow the character if it's
        // about to go off the screen
        if (x + xRes - playerX < screenClearance) {
            x = screenClearance + playerX - xRes;
            xTarget = x;
        } else if (playerX - x < screenClearance) {
            x = playerX - screenClearance;
            xTarget = x;
        }

        if (y + yRes - playerY < screenClearance) {
            y = screenClearance + playerY - yRes;
            yTarget = y;
        } else if (playerY - y < screenClearance) {
            y = playerY - screenClearance;
            yTarget = y;
        }

        // Finally, we make sure the camera doesn't scroll off the map.
        if (x + xRes > maxX) {
            x = maxX - xRes - 1;
            xTarget = x;
        } else if (x < 0) {
            x = 0;
            xTarget = x;
        }

        if (y + yRes > maxY) {
            y = maxY - yRes - 1;
            yTarget = y;
        } else if (y < 0) {
            y = 0;
            yTarget = y;
        }
    }

    public void render(Graphics g, BufferedImage image) {
        BufferedImage view;
        int imageSizeX = image.getWidth();
        int imageSizeY = image.getHeight();
        
        if (xRes < imageSizeX && yRes < imageSizeY) {
            view = image.getSubimage(x, y, xRes, yRes);
        } else if (xRes < imageSizeX) {
            view = image.getSubimage(x, 0, xRes, imageSizeY);
        } else if (yRes < imageSizeY) {
            view = image.getSubimage(0, y, imageSizeX, yRes);
        } else {
            view = image;
        }
        g.drawImage(view, 0, 0, null);
    }

    public int cameraToGlobalX(int xPix) {
        return x + xPix;
    }

    public int cameraToGlobalY(int yPix) {
        return y + yPix;
    }
}