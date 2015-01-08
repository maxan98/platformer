public class InputComponent {
    // Todo: put keymap and such here.
    
    public ControlComponent ctrlc;

    public void update(InputListener input) {
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
