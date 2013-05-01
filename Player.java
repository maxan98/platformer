public class Player extends Entity {
    private float maxSpeed = 250;
    private float jumpImpulse = 350;

    public Player(int x, int y) {
        super("./assets/sprites/player.png", x, y);
        this.gravityAffected = true;
        this.acceleration = 750;
    }


    public void handleInput(Tiles tiles, InputListener input) {
        boolean currentlyOnGround = onGround(tiles);
        
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