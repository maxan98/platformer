import java.util.LinkedList;

public class ControlComponent {
    public float maxSpeed;
    public float jumpImpulse;
    public float jumpExtra;
    public float maxHangTime;
    public float hangTime;
    public int lastGroundY;
    public boolean inJump;
    public LinkedList<Command> commands = new LinkedList<Command>();

    public PositionComponent pc;
    public VelocityComponent vc;
    public PhysicsComponent phyc;

    public void update(long delta) {
	if (pc.onGround) {
	    lastGroundY = pc.y + pc.yBound;
	}
	    
	for (Command command : commands) {
	    switch (command) {
	    case DROP_DOWN:
		if (pc.onGround && pc.onOneWayPlatform) {
		    pc.y += 1;
		}
		break;
	    case JUMP:
		if ((pc.onGround || pc.onEntity) && vc.dy >= 0) {
		    vc.dy = -jumpImpulse;
		    hangTime = 0;
		} else if (hangTime < maxHangTime) {
		    vc.dy -= delta * jumpExtra / 1000;
		    hangTime += (float) delta / 1000;
		}
		break;
	    case STOP:
		phyc.targetDx = 0;
		break;
	    case WALK_LEFT:
		phyc.targetDx = -1 * maxSpeed;
		break;
	    case WALK_RIGHT:
		phyc.targetDx = maxSpeed;
		break;
	    }
	}
	commands.clear();
    }
}
