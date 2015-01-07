import java.util.HashMap;

public class FacingSubsystem {
    // implement singleton pattern
    private static final FacingSubsystem singleton = new FacingSubsystem();
    private FacingSubsystem() {}
    public static FacingSubsystem get() {
	return singleton;
    }

    // Member variables
    private HashMap<UniqueId, FacingComponent> componentStore =
	new HashMap<UniqueId, FacingComponent>();

    public FacingComponent getComponent(UniqueId id) {
	return componentStore.get(id);
    }

    public void newComponent(UniqueId id, FacingComponent fc) {
	fc.vc = VelocitySubsystem.get().getComponent(id);

	assert fc.vc != null;
	
	componentStore.put(id, fc);
    }

    public void update() {
	for (FacingComponent fc : componentStore.values()) {
	    if (fc.vc.dx > 0) {
		fc.facing = 1;
	    } else if (fc.vc.dx < 0) {
		fc.facing = -1;
	    }
	}
    }
}
