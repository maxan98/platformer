public class FacingComponent {
    public int facing; // positive if facing right, negative otherwise

    public VelocityComponent vc;

    public void update() {
	if (vc.dx > 0) {
	    facing = 1;
	} else if (vc.dx < 0) {
	    facing = -1;
	}
    }
}
