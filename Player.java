import java.util.LinkedList;

public class Player { 
    
    private UniqueId id;
    private Tiles tiles;

    public Player(Tiles tiles, int x, int y) {
	this.tiles = tiles;
	
	this.id = new UniqueId();

	Sprite playerSprite = SpriteStore.get().getSprite("./assets/sprites/player.png");

	PositionComponent pc = new PositionComponent();
	pc.x = x;
	pc.y = y;
	pc.xBound = playerSprite.getWidth();
	pc.yBound = playerSprite.getHeight();
	PositionSubsystem.get().newComponent(id, pc);
	
	VelocityComponent vc = new VelocityComponent();
	VelocitySubsystem.get().newComponent(id, vc);

	FacingComponent fc = new FacingComponent();
	fc.facing = 1;
	FacingSubsystem.get().newComponent(id, fc);

	SpriteComponent sc = new SpriteComponent();
	sc.sprite = playerSprite;
	SpriteSubsystem.get().newComponent(id, sc);

	CollisionComponent cc = new CollisionComponent();
	CollisionSubsystem.get().newComponent(id, cc);

	PhysicsComponent phyc = new PhysicsComponent();
	phyc.xAcceleration = 850;
	phyc.yAcceleration = 1200;
	phyc.gravityAffected = true;
	PhysicsSubsystem.get().newComponent(id, phyc);

	ControlComponent ctrlc = new ControlComponent();
	ControlSubsystem.get().newComponent(id, ctrlc);
    }

    public Player(Tiles tiles) {
        this(tiles, tiles.startingPlayerX, tiles.startingPlayerY);
    }

    public void handleInput(InputListener input, long delta) {
	ControlComponent ctrlc = ControlSubsystem.get().getComponent(id);

	ctrlc.commands = new LinkedList<Command>();
        if (input.isKeyDown(InputListener.LEFT) && !input.isKeyDown(InputListener.RIGHT)) {
	    ctrlc.commands.addLast(Command.WALK_LEFT);
        } else if (!input.isKeyDown(InputListener.LEFT) && input.isKeyDown(InputListener.RIGHT)) {
	    ctrlc.commands.addLast(Command.WALK_RIGHT);
        } else {
	    ctrlc.commands.addLast(Command.STOP);
        }

        if (input.isKeyDown(InputListener.DOWN)) {
	    ctrlc.commands.addLast(Command.DROP_DOWN);
        }

        if (input.isKeyDown(InputListener.JUMP)) {
	    ctrlc.commands.addLast(Command.JUMP);
        }
    }
    
    // returns the Y coordinate (of the center of the sprite)
    // at the start of the last jump
    public int lastGroundY() {
	ControlComponent ctrlc = ControlSubsystem.get().getComponent(id);
        return ctrlc.lastGroundY;
    }

    public boolean onGround() {
	PositionComponent pc = PositionSubsystem.get().getComponent(id);
	return pc.onGround;
    }

    public int getCenterX() {
	PositionComponent pc = PositionSubsystem.get().getComponent(id);
        return pc.x + (pc.xBound / 2);
    }

    public int getCenterY() {
	PositionComponent pc = PositionSubsystem.get().getComponent(id);
        return pc.y + (pc.yBound / 2);
    }

    public int getFacing() {
	FacingComponent fc = FacingSubsystem.get().getComponent(id);
	return fc.facing;
    }
}
