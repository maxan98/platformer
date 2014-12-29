public class PositionComponent {
    // Each entity has a bounding box extending from (x,y) in the upper left
    // to (x + xBound, y + yBound) in the lower right
    public int x;
    public int y;
    public int xBound;
    public int yBound;
    public float xRemainder;
    public float yRemainder;
    public float deltaX;
    public float deltaY;
    public boolean onGround;
    public boolean onOneWayPlatform;
}
