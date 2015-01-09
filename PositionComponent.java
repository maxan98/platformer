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
    public boolean onEntity;

    public void update(Tiles tiles) {
	redirtyTiles(tiles);

	deltaX += xRemainder;
	x += (int) deltaX;
	xRemainder = deltaX - (int) deltaX;
	deltaX = 0;

	deltaY += yRemainder;
	y += (int) deltaY;
	yRemainder = deltaY - (int) deltaY;
	deltaY = 0;

	calculateOnGround(tiles);
	calculateOnOneWayPlatform(tiles);
    }

    private void redirtyTiles(Tiles tiles) {
        int iMin = tiles.pixToTile(x);
        int iMax = tiles.pixToTile(x + xBound - 1);
        int jMin = tiles.pixToTile(y);
        int jMax = tiles.pixToTile(y + yBound - 1);

	if (iMin >= tiles.getWidth() ||
	    iMax < 0 ||
	    jMin >= tiles.getHeight() ||
	    jMax < 0) {
	    return;
	}

	if (iMin < 0) iMin = 0;
	if (iMax >= tiles.getWidth()) iMax = tiles.getWidth() - 1;
	if (jMin < 0) jMin = 0;
	if (jMax >= tiles.getHeight()) jMax = tiles.getHeight() - 1;

        for (int i = iMin; i <= iMax; i++) {
            for (int j = jMin; j <= jMax; j++) {
                tiles.setDirty(i, j);
            }
        }
    }

    private void calculateOnGround(Tiles tiles) {
        int iMin = tiles.pixToTile(x);
        int iMax = tiles.pixToTile(x + xBound - 1);
        int pixMiddle = x + ((xBound - 1) / 2);
        int iMiddle = tiles.pixToTile(pixMiddle);
        int jBelowFeet = tiles.pixToTile(y + yBound);

	// If you're outside the map you're not on the ground.
	if (iMin < 0 ||
	    iMax >= tiles.getWidth() ||
	    jBelowFeet >= tiles.getHeight()) {
	    onGround = false;
	    return;
	}

        if (tiles.isSlope(iMiddle, jBelowFeet)) {
            onGround =
		CollisionComponent.distanceToSlope(jBelowFeet, tiles, x, y,
						   xBound, yBound) <= 0;
	    return;
        }

        int i;
        for (i = iMin; i <= iMax; i++) {
            if (tiles.isSolid(i, jBelowFeet)) break;

	    // If we're in the middle of a one way tile, we're not on the
	    // ground.
            if (tiles.isOneWay(i, jBelowFeet) &&
		(y + yBound) % tiles.getPixPerTile() == 0) break;
	        
        }
        
        onGround = i <= iMax;    
    }

    private void calculateOnOneWayPlatform(Tiles tiles) {
        int iMin = tiles.pixToTile(x);
        int iMax = tiles.pixToTile(x + xBound - 1);
        int pixMiddle = x + ((xBound - 1) / 2);
        int iMiddle = tiles.pixToTile(pixMiddle);
        int jBelowFeet = tiles.pixToTile(y + yBound);

	// If you're outside the map you're not on a platform.
	if (iMin < 0 ||
	    iMax >= tiles.getWidth() ||
	    jBelowFeet >= tiles.getHeight()) {
	    onOneWayPlatform = false;
	    return;
	}

        int i;
        boolean hasOneWay = false;
        for (i = iMin; i <= iMax; i++) {
            if (tiles.isOneWay(i, jBelowFeet)) hasOneWay = true;
            if (!tiles.isOneWay(i, jBelowFeet) && !tiles.isEmpty(i, jBelowFeet)) break;
        }
        
        onOneWayPlatform = i > iMax && hasOneWay;
    }
}
