import java.util.HashMap;

public class VelocitySubsystem {
    // implement singleton pattern
    private static final VelocitySubsystem singleton = new VelocitySubsystem();
    private VelocitySubsystem() {}
    public static VelocitySubsystem get() {
	return singleton;
    }

    // Member variables
    private HashMap<UniqueId, VelocityComponent> componentStore =
	new HashMap<UniqueId, VelocityComponent>();

    public VelocityComponent getComponent(UniqueId id) {
	return componentStore.get(id);
    }

    public void newComponent(UniqueId id, VelocityComponent vc) {
	vc.pc = PositionSubsystem.get().getComponent(id);
	
	assert vc.pc != null;
	
	componentStore.put(id, vc);
    }

    public void update(long delta) {
	for (VelocityComponent vc : componentStore.values()) {
	    vc.pc.deltaX = ((float) (delta * vc.dx)) / 1000;
	    vc.pc.deltaY = ((float) (delta * vc.dy)) / 1000;	    
	}
    }
}
