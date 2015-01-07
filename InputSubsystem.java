import java.util.HashMap;
import java.util.LinkedList;

public class InputSubsystem {
    // implement singleton pattern
    private static final InputSubsystem singleton = new InputSubsystem();
    private InputSubsystem() {}
    public static InputSubsystem get() {
	return singleton;
    }

    // Member variables
    private HashMap<UniqueId, InputComponent> componentStore =
	new HashMap<UniqueId, InputComponent>();

    public InputComponent getComponent(UniqueId id) {
	return componentStore.get(id);
    }

    public void newComponent(UniqueId id, InputComponent ic) {
	ic.ctrlc = ControlSubsystem.get().getComponent(id);

	assert ic.ctrlc != null;
	
	componentStore.put(id, ic);
    }

    public void update(InputListener input) {
	for (InputComponent ic : componentStore.values()) {
	    if (input.isKeyDown(InputListener.LEFT) && !input.isKeyDown(InputListener.RIGHT)) {
		ic.ctrlc.commands.addLast(Command.WALK_LEFT);
	    } else if (!input.isKeyDown(InputListener.LEFT) && input.isKeyDown(InputListener.RIGHT)) {
		ic.ctrlc.commands.addLast(Command.WALK_RIGHT);
	    } else {
		ic.ctrlc.commands.addLast(Command.STOP);
	    }

	    if (input.isKeyDown(InputListener.DOWN)) {
		ic.ctrlc.commands.addLast(Command.DROP_DOWN);
	    }

	    if (input.isKeyDown(InputListener.JUMP)) {
		ic.ctrlc.commands.addLast(Command.JUMP);
	    }
	}
    }
}

