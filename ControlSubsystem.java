import java.util.HashMap;

public class ControlSubsystem {
    // implement singleton pattern
    private static final ControlSubsystem singleton = new ControlSubsystem();
    private ControlSubsystem() {}
    public static ControlSubsystem get() {
	return singleton;
    }

    // Member variables
    private HashMap<UniqueId, ControlComponent> componentStore =
	new HashMap<UniqueId, ControlComponent>();

    public ControlComponent getComponent(UniqueId id) {
	return componentStore.get(id);
    }

    public void newComponent(UniqueId id, ControlComponent ctrlc) {
	ctrlc.pc = PositionSubsystem.get().getComponent(id);
	ctrlc.vc = VelocitySubsystem.get().getComponent(id);
	ctrlc.phyc = PhysicsSubsystem.get().getComponent(id);

	assert ctrlc.pc != null;
	assert ctrlc.vc != null;
	assert ctrlc.phyc != null;

	componentStore.put(id, ctrlc);
    }

    public void update(long delta) {
	for (ControlComponent ctrlc : componentStore.values()) {
	    
	    if (ctrlc.pc.onGround) {
		ctrlc.lastGroundY = ctrlc.pc.y + ctrlc.pc.yBound;
	    }
	    
	    for (Command command : ctrlc.commands) {
		switch (command) {
		case DROP_DOWN:
		    if (ctrlc.pc.onGround && ctrlc.pc.onOneWayPlatform) {
			ctrlc.pc.y += 1;
		    }
		    break;
		case JUMP:
		    if (ctrlc.pc.onGround && ctrlc.vc.dy >= 0) {
			ctrlc.vc.dy = -ctrlc.jumpImpulse;
			ctrlc.hangTime = 0;
		    } else if (ctrlc.hangTime < ctrlc.maxHangTime) {
			ctrlc.vc.dy -= delta * ctrlc.jumpExtra / 1000;
			ctrlc.hangTime += (float) delta / 1000;
		    }
		    break;
		case STOP:
		    ctrlc.phyc.targetDx = 0;
		    break;
		case WALK_LEFT:
		    ctrlc.phyc.targetDx = -1 * ctrlc.maxSpeed;
		    break;
		case WALK_RIGHT:
		    ctrlc.phyc.targetDx = ctrlc.maxSpeed;
		    break;
		}
	    }
	    ctrlc.commands.clear();
	}
    }
}

