public class PhysicsComponent {
    public float targetDx;
    public float targetDy;
    public float xAcceleration;
    public float yAcceleration;
    public float terminalVelocity = 500;
    public boolean gravityAffected;

    public PositionComponent pc;
    public VelocityComponent vc;
}
