import java.util.HashMap;

public class PhysicsSubsystem {
    // implement singleton pattern
    private static final PhysicsSubsystem singleton = new PhysicsSubsystem();
    private PhysicsSubsystem() {}
    public static PhysicsSubsystem get() {
	return singleton;
    }

    // Member variables
    private HashMap<UniqueId, PhysicsComponent> componentStore =
	new HashMap<UniqueId, PhysicsComponent>();

    public PhysicsComponent getComponent(UniqueId id) {
	return componentStore.get(id);
    }

    public void newComponent(UniqueId id, PhysicsComponent phyc) {
	phyc.pc = PositionSubsystem.get().getComponent(id);
	phyc.vc = VelocitySubsystem.get().getComponent(id);
	
	assert phyc.pc != null;
	assert phyc.vc != null;

	componentStore.put(id, phyc);
    }

    public void update(long delta) {
	for (PhysicsComponent phyc : componentStore.values()) {
	    if (phyc.gravityAffected && !phyc.pc.onGround) {
		phyc.targetDy = phyc.terminalVelocity;
	    }

	    if (phyc.vc.dx < phyc.targetDx) {
		if (phyc.pc.onGround) {
		    phyc.vc.dx += phyc.xAcceleration * delta / 1000;
		} else {
		    phyc.vc.dx += 0.5 * phyc.xAcceleration * delta / 1000;
		}

		if (phyc.vc.dx > phyc.targetDx) phyc.vc.dx = phyc.targetDx;

	    } else if (phyc.vc.dx > phyc.targetDx) {
		if (phyc.pc.onGround) {
		    phyc.vc.dx -= phyc.xAcceleration * delta / 1000;
		} else {
		    phyc.vc.dx -= 0.5 * phyc.xAcceleration * delta / 1000;
		}

		if (phyc.vc.dx < phyc.targetDx) phyc.vc.dx = phyc.targetDx;
	    }

	    if (phyc.vc.dy < phyc.targetDy) {
		phyc.vc.dy += phyc.yAcceleration * delta / 1000;
		if (phyc.vc.dy > phyc.targetDy) phyc.vc.dy = phyc.targetDy;
	    } else if (phyc.vc.dy > phyc.targetDy) {
		phyc.vc.dy -= phyc.yAcceleration * delta / 1000;
		if (phyc.vc.dy < phyc.targetDy) phyc.vc.dy = phyc.targetDy;
	    }
	}
    }
}
