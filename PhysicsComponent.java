public class PhysicsComponent {
    public float targetDx;
    public float targetDy;
    public float xAcceleration;
    public float yAcceleration;
    public float terminalVelocity = 500;
    public boolean gravityAffected;

    public PositionComponent pc;
    public VelocityComponent vc;

    public void update(long delta) {
	if (gravityAffected && !pc.onGround && !pc.onEntity) {
	    targetDy = terminalVelocity;
	}

	if (vc.dx < targetDx) {
	    if (pc.onGround) {
		vc.dx += xAcceleration * delta / 1000;
	    } else {
		vc.dx += 0.5 * xAcceleration * delta / 1000;
	    }

	    if (vc.dx > targetDx) vc.dx = targetDx;

	} else if (vc.dx > targetDx) {
	    if (pc.onGround) {
		vc.dx -= xAcceleration * delta / 1000;
	    } else {
		vc.dx -= 0.5 * xAcceleration * delta / 1000;
	    }

	    if (vc.dx < targetDx) vc.dx = targetDx;
	}

	if (vc.dy < targetDy) {
	    vc.dy += yAcceleration * delta / 1000;
	    if (vc.dy > targetDy) vc.dy = targetDy;
	} else if (vc.dy > targetDy) {
	    vc.dy -= yAcceleration * delta / 1000;
	    if (vc.dy < targetDy) vc.dy = targetDy;
	}
    }
}
