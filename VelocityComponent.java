public class VelocityComponent {
    public float dx;
    public float dy;

    public PositionComponent pc;

    public void update(long delta) {
	pc.deltaX = ((float) (delta * dx)) / 1000;
	pc.deltaY = ((float) (delta * dy)) / 1000;	    
    }
}
