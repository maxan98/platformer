public class PhysicsSubsystem() {
    // implement singleton pattern
    private static final PhysicsSubsystem singleton = new PhysicsSubsystem();
    private PhysicsSubsystem() {}
    public static PhysicsSubsystem get() {
	return singleton;
    }

    // Member variables
    private ComponentStore<PhysicsComponent> cs = new ComponentStore<PhysicsComponent>();

    public PhysicsComponent getComponent(UniqueId id) {
	return cs.get(id);
    }

    public void newComponent(UniqueId id, PhysicsComponent pc) {
	assert VelocitySubsystem.get().getComponent(id);
	assert PositionSubsystem.get().getComponent(id);
	assert CollisionSubsystem.get().getComponent(id);
	
	cs.put(id, pc);
    }

    public void update(long delta) {
	for (HashMap.Entry<UniqueId, PhysicsComponent> entry : cs.entrySet()) {
	    UniqueId id = entry.getKey();
	    PhysicsComponent pc = entry.getValue();
	    VelocityComponent vc = VelocitySubsystem.get().getComponent(id);
	    CollisionComponent cc = CollisionSubsytem.get().getComponent(id);

	    if (pc.gravityAffected && !cc.onGround) {
		pc.targetDy = pc.terminalVelocity;
	    }

	    if (vc.dx < pc.targetDx) {
		if (cc.onGround) {
		    vc.dx += pc.xAcceleration * delta / 1000;
		} else {
		    vc.dx += 0.5 * pc.xAcceleration * delta / 1000;
		}

		if (vc.dx > pc.targetDx) vc.dx = pc.targetDx;

	    } else if (vc.dx > pc.targetDx) {
		if (cc.onGround) {
		    vc.dx -= pc.xAcceleration * delta / 1000;
		} else {
		    vc.dx -= 0.5 * pc.xAcceleration * delta / 1000;
		}

		if (vc.dx < pc.targetDx) vc.dx = pc.targetDx;
	    }

	    if (vc.dy < pc.targetDy) {
		vc.dy += pc.yAcceleration * delta / 1000;
		if (vc.dy > pc.targetDy) vc.dy = pc.targetDy;
	    } else if (vc.dy > pc.targetDy) {
		vc.dy -= pc.yAcceleration * delta / 1000;
		if (vc.dy < pc.targetDy) vc.dy = pc.targetDy;
	    }
	}
    }
}
