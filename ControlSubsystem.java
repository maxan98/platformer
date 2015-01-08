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
	    ctrlc.update(delta);
	}
    }
}

