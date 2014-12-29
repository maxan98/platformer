import java.util.LinkedList;

public class ControlComponent {
    public float maxSpeed = 400;
    public float jumpImpulse = 375;
    public float jumpExtra = 1000;
    public float maxHangTime = (float) 0.3;
    public float hangTime;
    public int lastGroundY;
    public boolean inJump;
    public LinkedList<Command> commands;
}
