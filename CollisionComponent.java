public class CollisionComponent {
    // Each entity has a bounding box extending from (x,y) in the upper left
    // to (x + xBound, y + yBound) in the lower right
    public int xBound;
    public int yBound;
    public boolean hitWall;
    public boolean onGround;
    public boolean onOneWayPlatform;
}
