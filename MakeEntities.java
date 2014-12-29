public class MakeEntities {
    
    public static UniqueId makePlayer(Tiles tiles, int x, int y) {
	UniqueId id = new UniqueId();
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

	InputComponent ic = new InputComponent();
	InputSubsystem.get().newComponent(id, ic);

	return id;
    }
}
