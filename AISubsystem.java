import java.util.HashMap;

public class AISubsystem {
    // implement singleton pattern
    private static final AISubsystem singleton = new AISubsystem();
    private AISubsystem() {}
    public static AISubsystem get() {
	return singleton;
    }

    // Member variables
    private HashMap<UniqueId, AIComponent> componentStore = new HashMap<UniqueId, AIComponent>();

    public AIComponent getComponent(UniqueId id) {
	return componentStore.get(id);
    }

    public void newComponent(UniqueId id, AIComponent aic) {
	aic.ctrlc = ControlSubsystem.get().getComponent(id);
	aic.cc = CollisionSubsystem.get().getComponent(id);

	assert aic.ctrlc != null;
	assert aic.cc != null;
	
	componentStore.put(id, aic);
    }

    public void update() {
	for (AIComponent aic : componentStore.values()) {
	    aic.update();
	}
    }
}

