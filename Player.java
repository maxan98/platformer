public class Player { 
    private float maxSpeed = 400;
    private float jumpImpulse = 375;
    private float jumpExtra = 1000;
    private float maxHangTime = (float) 0.3;
    private float hangTime;
    
    private UniqueId id;

    private int lastGroundY;
    private boolean inJump;

    private Tiles tiles;

    public Player(Tiles tiles, int x, int y) {
	this.tiles = tiles;
	
	this.id = new UniqueId();
        this.lastGroundY = 0;	
        this.hangTime = 0;

	PositionComponent pc = new PositionComponent();
	pc.x = x;
	pc.y = y;
	PositionSubsystem.get().newComponent(id, pc);
	
	VelocityComponent vc = new VelocityComponent();
	PositionSubsystem.get().newComponent(id, vc);

	FacingComponent fc = new FacingComponent();
	fc.facing = 1;
	FacingSubsystem.get().newComponent(id, fc);

	SpriteComponent sc = new SpriteComponent();
	sc.sprite = SpriteStore.get().getSprite("./assets/sprites/player.png");
	SpriteSubsystem.get().newComponent(id, sc);

	CollisionComponent cc = new CollisionComponent();
	cc.xBound = sc.sprite.getWidth();
	cc.yBound = sc.sprite.getHeight();
	CollisionSubsystem.get().newComponent(id, cc);

	PhysicsComponent pyc = new PhysicsComponent();
	pyc.xAcceleration = 850;
	pyc.yAcceleration = 1200;
	pyc.gravityAffected = true;
	PhysicsSubsystem.get().newComponent(id, pyc);
    }

    public Player(Tiles tiles) {
        this(tiles, tiles.startingPlayerX, tiles.startingPlayerY);
    }

    private void dirtyPlayer() {
	PositionComponent pc = PositionSubsystem.get().getComponent(id);
	CollisionComponent cc = CollisionSubsystem.get().getComponent(id);	
        int iMin = tiles.pixToTile(pc.x);
        int iMax = tiles.pixToTile(pc.x + cc.xBound - 1);
        int jMin = tiles.pixToTile(pc.y);
        int jMax = tiles.pixToTile(pc.y + cc.yBound - 1);
        
        for (int i = iMin; i <= iMax; i++) {
            for (int j = jMin; j <= jMax; j++) {
                tiles.setDirty(i, j);
            }
        }
    }

    public void handleInput(InputListener input, long delta) {
	PositionComponent pc = PositionSubsystem.get().getComponent(id);
	CollisionComponent cc = CollisionSubsystem.get().getComponent(id);	
	VelocityComponent vc = VelocitySubsystem.get().getComponent(id);
	PhysicsComponent phyc = PhysicsSubsystem.get().getComponent(id);

        if (cc.onGround) {
            lastGroundY = pc.y + cc.yBound;
        }
        
        if (input.isKeyDown(InputListener.LEFT) && !input.isKeyDown(InputListener.RIGHT)) {
            phyc.targetDx = -1 * maxSpeed;
        } else if (!input.isKeyDown(InputListener.LEFT) && input.isKeyDown(InputListener.RIGHT)) {
            phyc.targetDx = maxSpeed;
        } else {
            phyc.targetDx = 0;
        }

        if (input.isKeyDown(InputListener.DOWN)) {
            if (currentlyOnGround && onOneWayPlatform()) {
                dirtyPlayer();
                pc.y += 1;
            }
        }

        if (input.isKeyDown(InputListener.JUMP)) {
            if (cc.onGround && vc.dy >= 0) {
                vc.dy = -jumpImpulse;
                hangTime = 0;
            } else if (hangTime < maxHangTime) {
                vc.dy -= delta * jumpExtra / 1000;
                hangTime += (float) delta / 1000;
            }
        }

        if (input.isKeyDown(InputListener.RESET)) {
            dirtyPlayer();
            vc.dx = 0;
            vc.dy = 0;
            pc.x = tiles.startingPlayerX;
            pc.y = tiles.startingPlayerY;
        }
    }
    
    // returns the Y coordinate (of the center of the sprite)
    // at the start of the last jump
    public int lastGroundY() {
        return lastGroundY;
    }

    public int getCenterX() {
	PositionComponent pc = PositionSubsystem.get().getComponent(id);
	CollisionComponent cc = CollisionSubsystem.get().getComponent(id);	
        return pc.x + (cc.xBound / 2);
    }

    public int getCenterY() {
	PositionComponent pc = PositionSubsystem.get().getComponent(id);
	CollisionComponent cc = CollisionSubsystem.get().getComponent(id);	
        return pc.y + (cc.yBound / 2);
    }

    public int getFacing() {
	FacingComponent fc = FacingSubsystem.get().getComponent(id);
	return fc.facing;
    }

}
