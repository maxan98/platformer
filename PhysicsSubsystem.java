import java.util.Map;

public class PhysicsSubsystem {
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
	assert VelocitySubsystem.get().getComponent(id) != null;
	assert PositionSubsystem.get().getComponent(id) != null;
	
	cs.put(id, pc);
    }

    public void update(long delta) {
	for (Map.Entry<UniqueId, PhysicsComponent> entry : cs.entrySet()) {
	    UniqueId id = entry.getKey();
	    PhysicsComponent phyc = entry.getValue();
	    VelocityComponent vc = VelocitySubsystem.get().getComponent(id);
	    PositionComponent pc = PositionSubsystem.get().getComponent(id);

	    if (phyc.gravityAffected && !pc.onGround) {
		phyc.targetDy = phyc.terminalVelocity;
	    }

	    if (vc.dx < phyc.targetDx) {
		if (pc.onGround) {
		    vc.dx += phyc.xAcceleration * delta / 1000;
		} else {
		    vc.dx += 0.5 * phyc.xAcceleration * delta / 1000;
		}

		if (vc.dx > phyc.targetDx) vc.dx = phyc.targetDx;

	    } else if (vc.dx > phyc.targetDx) {
		if (pc.onGround) {
		    vc.dx -= phyc.xAcceleration * delta / 1000;
		} else {
		    vc.dx -= 0.5 * phyc.xAcceleration * delta / 1000;
		}

		if (vc.dx < phyc.targetDx) vc.dx = phyc.targetDx;
	    }

	    if (vc.dy < phyc.targetDy) {
		vc.dy += phyc.yAcceleration * delta / 1000;
		if (vc.dy > phyc.targetDy) vc.dy = phyc.targetDy;
	    } else if (vc.dy > phyc.targetDy) {
		vc.dy -= phyc.yAcceleration * delta / 1000;
		if (vc.dy < phyc.targetDy) vc.dy = phyc.targetDy;
	    }
	}
    }
}
