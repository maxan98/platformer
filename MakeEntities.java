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
	cc.collideWithTiles = true;
	cc.collideWithEntities = true;
	cc.solidToEntities = true;
	CollisionSubsystem.get().newComponent(id, cc);

	PhysicsComponent phyc = new PhysicsComponent();
	phyc.xAcceleration = 850;
	phyc.yAcceleration = 1200;
	phyc.gravityAffected = true;
	PhysicsSubsystem.get().newComponent(id, phyc);

	ControlComponent ctrlc = new ControlComponent();
	ctrlc.maxSpeed = 400;
	ctrlc.jumpImpulse = 375;
	ctrlc.jumpExtra = 1000;
	ctrlc.maxHangTime = (float) 0.3;
	ControlSubsystem.get().newComponent(id, ctrlc);

	InputComponent ic = new InputComponent();
	InputSubsystem.get().newComponent(id, ic);

	return id;
    }

    public static UniqueId makeEnemy(Tiles tiles, int x, int y) {
	UniqueId id = new UniqueId();
	Sprite enemySprite = SpriteStore.get().getSprite("./assets/sprites/enemy.png");

	PositionComponent pc = new PositionComponent();
	pc.x = x;
	pc.y = y;
	pc.xBound = enemySprite.getWidth();
	pc.yBound = enemySprite.getHeight();
	PositionSubsystem.get().newComponent(id, pc);
	
	VelocityComponent vc = new VelocityComponent();
	VelocitySubsystem.get().newComponent(id, vc);

	FacingComponent fc = new FacingComponent();
	fc.facing = 1;
	FacingSubsystem.get().newComponent(id, fc);

	SpriteComponent sc = new SpriteComponent();
	sc.sprite = enemySprite;
	SpriteSubsystem.get().newComponent(id, sc);

	CollisionComponent cc = new CollisionComponent();
	cc.collideWithTiles = true;
	cc.collideWithEntities = true;
	cc.solidToEntities = true;
	CollisionSubsystem.get().newComponent(id, cc);

	PhysicsComponent phyc = new PhysicsComponent();
	phyc.xAcceleration = 850;
	phyc.yAcceleration = 1200;
	phyc.gravityAffected = true;
	PhysicsSubsystem.get().newComponent(id, phyc);

	ControlComponent ctrlc = new ControlComponent();
	ctrlc.maxSpeed = 150;
	ctrlc.jumpImpulse = 375;
	ctrlc.jumpExtra = 1000;
	ctrlc.maxHangTime = (float) 0.3;
	ControlSubsystem.get().newComponent(id, ctrlc);

	AIComponent aic = new AIComponent();
	aic.walkingDirection = 1;
	AISubsystem.get().newComponent(id, aic);

	return id;
    }
}
