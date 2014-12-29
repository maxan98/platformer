import java.util.Map;
import java.util.LinkedList;

public class InputSubsystem {
    // implement singleton pattern
    private static final InputSubsystem singleton = new InputSubsystem();
    private InputSubsystem() {}
    public static InputSubsystem get() {
	return singleton;
    }

    // Member variables
    private ComponentStore<InputComponent> cs = new ComponentStore<InputComponent>();

    public InputComponent getComponent(UniqueId id) {
	return cs.get(id);
    }

    public void newComponent(UniqueId id, InputComponent ic) {
	assert ControlSubsystem.get().getComponent(id) != null;
	
	cs.put(id, ic);
    }

    public void update(InputListener input) {
	for (Map.Entry<UniqueId, InputComponent> entry : cs.entrySet()) {
	    UniqueId id = entry.getKey();
	    InputComponent ic = entry.getValue();

	    ControlComponent ctrlc = ControlSubsystem.get().getComponent(id);

	    ctrlc.commands = new LinkedList<Command>();
	    if (input.isKeyDown(InputListener.LEFT) && !input.isKeyDown(InputListener.RIGHT)) {
		ctrlc.commands.addLast(Command.WALK_LEFT);
	    } else if (!input.isKeyDown(InputListener.LEFT) && input.isKeyDown(InputListener.RIGHT)) {
		ctrlc.commands.addLast(Command.WALK_RIGHT);
	    } else {
		ctrlc.commands.addLast(Command.STOP);
	    }

	    if (input.isKeyDown(InputListener.DOWN)) {
		ctrlc.commands.addLast(Command.DROP_DOWN);
	    }

	    if (input.isKeyDown(InputListener.JUMP)) {
		ctrlc.commands.addLast(Command.JUMP);
	    }
	}
    }
}

