public class VelocitySubsystem() {
    // implement singleton pattern
    private static final VelocitySubsystem singleton = new VelocitySubsystem();
    private VelocitySubsystem() {}
    public static VelocitySubsystem get() {
	return singleton;
    }

    // Member variables
    private ComponentStore<VelocityComponent> cs = new ComponentStore<VelocityComponent>();

    public VelocityComponent getComponent(UniqueId id) {
	return cs.get(id);
    }

    public void newComponent(UniqueId id, VelocityComponent vc) {
	assert PositionSubsystem.get().getComponent(id);
	
	cs.put(id, vc);
    }

    public void update(long delta) {
	for (HashMap.Entry<UniqueId, VelocityComponent> entry : cs.entrySet()) {
	    UniqueId id = entry.getKey();
	    VelocityComponent vc = entry.getValue();
	    
	    PositionComponent pc = PositionSubsystem.get().getComponent(id);
	    pc.deltaX = ((float) (delta * vc.dx)) / 1000;
	    pc.deltaY = ((float) (delta * vc.dy)) / 1000;	    
	}
    }
}
