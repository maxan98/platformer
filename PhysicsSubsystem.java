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
	    phyc.update(delta);
	}
    }
}
