import java.util.Map;

public class PositionSubsystem {
    // implement singleton pattern
    private static final PositionSubsystem singleton = new PositionSubsystem();
    private PositionSubsystem() {}
    public static PositionSubsystem get() {
	return singleton;
    }

    // Member variables
    private ComponentStore<PositionComponent> cs = new ComponentStore<PositionComponent>();
  
    public PositionComponent getComponent(UniqueId id) {
	return cs.get(id);
    }

    public void newComponent(UniqueId id, PositionComponent pc) {
	cs.put(id, pc);
    }

    public void update() {
	for (Map.Entry<UniqueId, PositionComponent> entry : cs.entrySet()) {
	    UniqueId id = entry.getKey();
	    PositionComponent pc = entry.getValue();
	    
	    pc.deltaX += pc.xRemainder;
	    pc.x += (int) pc.deltaX;
	    pc.xRemainder = pc.deltaX - (int) pc.deltaX;
	    pc.deltaX = 0;

	    pc.deltaY += pc.yRemainder;
	    pc.y += (int) pc.deltaY;
	    pc.yRemainder = pc.deltaY - (int) pc.deltaY;
	    pc.deltaY = 0;
	}
    }
}
