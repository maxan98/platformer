public class AIComponent {
    // > 0: right
    // < 0: left
    //   0: stand still
    public int walkingDirection;

    public ControlComponent ctrlc;
    public CollisionComponent cc;

    public void update() {
	if (cc.hitWall || cc.hitEntity) {
	    walkingDirection *= -1;
	}

	if (walkingDirection < 0) {
	    ctrlc.commands.addLast(Command.WALK_LEFT);
	} else if (walkingDirection > 0) {
	    ctrlc.commands.addLast(Command.WALK_RIGHT);
	} else {
	    ctrlc.commands.addLast(Command.STOP);
	}

    }
}
