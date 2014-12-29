import java.util.Map;
import java.util.LinkedList;

public class AISubsystem {
    // implement singleton pattern
    private static final AISubsystem singleton = new AISubsystem();
    private AISubsystem() {}
    public static AISubsystem get() {
	return singleton;
    }

    // Member variables
    private ComponentStore<AIComponent> cs = new ComponentStore<AIComponent>();

    public AIComponent getComponent(UniqueId id) {
	return cs.get(id);
    }

    public void newComponent(UniqueId id, AIComponent aic) {
	assert ControlSubsystem.get().getComponent(id) != null;
	assert CollisionSubsystem.get().getComponent(id) != null;	
	
	cs.put(id, aic);
    }

    public void update() {
	for (Map.Entry<UniqueId, AIComponent> entry : cs.entrySet()) {
	    UniqueId id = entry.getKey();
	    AIComponent aic = entry.getValue();

	    ControlComponent ctrlc = ControlSubsystem.get().getComponent(id);
	    CollisionComponent cc = CollisionSubsystem.get().getComponent(id);

	    if (cc.hitWall) {
		aic.walkingDirection *= -1;
	    }

	    ctrlc.commands = new LinkedList<Command>();
	    if (aic.walkingDirection < 0) {
		ctrlc.commands.addLast(Command.WALK_LEFT);
	    } else if (aic.walkingDirection > 0) {
		ctrlc.commands.addLast(Command.WALK_RIGHT);
	    } else {
		ctrlc.commands.addLast(Command.STOP);
	    }
	}
    }
}

