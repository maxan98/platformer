public class Player extends Entity {
    private float maxSpeed = 400;
    private float jumpImpulse = 375;
    private float jumpExtra = 1000;
    private float maxHangTime = (float) 0.3;
    private float hangTime;

    private int lastGroundY;
    private boolean inJump;

    public Player(Tiles tiles, int x, int y) {
        super(tiles, "./assets/sprites/player.png", x, y);
        this.lastGroundY = 0;
        this.gravityAffected = true;
        this.xAcceleration = 850;
        this.yAcceleration = 1200;
        this.hangTime = 0;
    }

    public Player(Tiles tiles) {
        this(tiles, tiles.startingPlayerX, tiles.startingPlayerY);
    }

    private void dirtyPlayer() {
        int iMin = tiles.pixToTile(x);
        int iMax = tiles.pixToTile(x + xBound - 1);
        int jMin = tiles.pixToTile(y);
        int jMax = tiles.pixToTile(y + yBound - 1);
        
        for (int i = iMin; i <= iMax; i++) {
            for (int j = jMin; j <= jMax; j++) {
                tiles.setDirty(i, j);
            }
        }
    }

    public void handleInput(InputListener input, long delta) {
        boolean currentlyOnGround = onGround();

        if (currentlyOnGround) {
            lastGroundY = y + yBound;
        }
        
        if (input.isKeyDown(InputListener.LEFT) && !input.isKeyDown(InputListener.RIGHT)) {
            targetDx = -1 * maxSpeed;
        } else if (!input.isKeyDown(InputListener.LEFT) && input.isKeyDown(InputListener.RIGHT)) {
            targetDx = maxSpeed;
        } else {
            targetDx = 0;
        }

        if (input.isKeyDown(InputListener.DOWN)) {
            if (currentlyOnGround && onOneWayPlatform()) {
                dirtyPlayer();
                y += 1;
            }
        }

        if (input.isKeyDown(InputListener.JUMP)) {
            if (currentlyOnGround && dy >= 0) {
                dy = -jumpImpulse;
                hangTime = 0;
            } else if (hangTime < maxHangTime) {
                dy -= delta * jumpExtra / 1000;
                hangTime += (float) delta / 1000;
            }
        }

        if (input.isKeyDown(InputListener.RESET)) {
            dirtyPlayer();
            dx = 0;
            dy = 0;
            x = tiles.startingPlayerX;
            y = tiles.startingPlayerY;
        }
    }
    
    // returns the Y coordinate (of the center of the sprite)
    // at the start of the last jump
    public int lastGroundY() {
        return lastGroundY;
    }
}