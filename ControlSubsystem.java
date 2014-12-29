import java.util.Map;

public class ControlSubsystem {
    // implement singleton pattern
    private static final ControlSubsystem singleton = new ControlSubsystem();
    private ControlSubsystem() {}
    public static ControlSubsystem get() {
	return singleton;
    }

    // Member variables
    private ComponentStore<ControlComponent> cs = new ComponentStore<ControlComponent>();

    public ControlComponent getComponent(UniqueId id) {
	return cs.get(id);
    }

    public void newComponent(UniqueId id, ControlComponent ctrlc) {
	assert PhysicsSubsystem.get().getComponent(id) != null;	
	assert PositionSubsystem.get().getComponent(id) != null;
	assert VelocitySubsystem.get().getComponent(id) != null;
	
	cs.put(id, ctrlc);
    }

    public void update(long delta) {
	for (Map.Entry<UniqueId, ControlComponent> entry : cs.entrySet()) {
	    UniqueId id = entry.getKey();
	    ControlComponent ctrlc = entry.getValue();

	    PositionComponent pc = PositionSubsystem.get().getComponent(id);
	    VelocityComponent vc = VelocitySubsystem.get().getComponent(id);
	    PhysicsComponent phyc = PhysicsSubsystem.get().getComponent(id);
	    
	    if (pc.onGround) {
		ctrlc.lastGroundY = pc.y + pc.yBound;
	    }

	    for (Command command : ctrlc.commands) {
		switch (command) {
		case DROP_DOWN:
		    if (pc.onGround && pc.onOneWayPlatform) {
			pc.y += 1;
		    }
		    break;
		case JUMP:
		    if (pc.onGround && vc.dy >= 0) {
			vc.dy = -ctrlc.jumpImpulse;
			ctrlc.hangTime = 0;
		    } else if (ctrlc.hangTime < ctrlc.maxHangTime) {
			vc.dy -= delta * ctrlc.jumpExtra / 1000;
			ctrlc.hangTime += (float) delta / 1000;
		    }
		    break;
		case STOP:
		    phyc.targetDx = 0;
		    break;
		case WALK_LEFT:
		    phyc.targetDx = -1 * ctrlc.maxSpeed;
		    break;
		case WALK_RIGHT:
		    phyc.targetDx = ctrlc.maxSpeed;
		    break;
		}
	    }
	    ctrlc.commands = null;
	}
    }
}

