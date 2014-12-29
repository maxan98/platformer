public class FacingSubsystem() {
    // implement singleton pattern
    private static final FacingSubsystem singleton = new FacingSubsystem();
    private FacingSubsystem() {}
    public static FacingSubsystem get() {
	return singleton;
    }

    // Member variables
    private ComponentStore<FacingComponent> cs = new ComponentStore<FacingComponent>();

    public FacingComponent getComponent(UniqueId id) {
	return cs.get(id);
    }

    public void newComponent(UniqueId id, FacingComponent fc) {
	assert VeloctitySubsystem.get().getComponent(id);
	
	cs.put(id, fc);
    }

    public void update(long delta) {
	for (HashMap.Entry<UniqueId, FacingComponent> entry : cs.entrySet()) {
	    UniqueId id = entry.getKey();
	    FacingComponent fc = entry.getValue();
	    
	    VelocityComponent vc = FacingComponent.get().getComponent(id);
	    if (vc.dx > 0) {
		fc.facing = 1;
	    } else if (vc.dx < 0) {
		fc.facing = -1;
	    }
	}
    }
}
