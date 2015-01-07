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
}
