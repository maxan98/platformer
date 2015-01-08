import java.util.HashMap;

public class PositionSubsystem {
    // implement singleton pattern
    private static final PositionSubsystem singleton = new PositionSubsystem();
    private PositionSubsystem() {}
    public static PositionSubsystem get() {
	return singleton;
    }

    // Member variables
    private HashMap<UniqueId, PositionComponent> componentStore =
	new HashMap<UniqueId, PositionComponent>();
  
    public PositionComponent getComponent(UniqueId id) {
	return componentStore.get(id);
    }

    public void newComponent(UniqueId id, PositionComponent pc) {
	componentStore.put(id, pc);
    }

    public void update(Tiles tiles) {
	for (PositionComponent pc : componentStore.values()) {
	    pc.update(tiles);
	}
    }
}
