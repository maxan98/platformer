public class Player extends Entity {
    private float maxSpeed = 250;
    private float jumpImpulse = 350;

    public Player(Tiles tiles, int x, int y) {
        super(tiles, "./assets/sprites/player.png", x, y);
        this.gravityAffected = true;
        this.acceleration = 750;
    }


    public void handleInput(InputListener input) {
        boolean currentlyOnGround = onGround();
        
        if (input.leftPressed() && !input.rightPressed()) {
            targetDx = -1 * maxSpeed;
        } else if (!input.leftPressed() && input.rightPressed()) {
            targetDx = maxSpeed;
        } else {
            targetDx = 0;
        }

        if (input.jumpPressed() && currentlyOnGround) {
            dy = -1 * jumpImpulse;
        }
    }
}