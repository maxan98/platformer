import java.util.HashMap;

public class PositionSubsystem {
    // implement singleton pattern
    private static final PositionSubsystem singleton = new PositionSubsystem();
    private PositionSubsystem() {}
    public static PositionSubsystem get() {
	return singleton;
    }

    // Member variables
    private HashMap<UniqueId, PositionComponent> componentStore =
	new HashMap<UniqueId, PositionComponent>();
  
    public PositionComponent getComponent(UniqueId id) {
	return componentStore.get(id);
    }

    public void newComponent(UniqueId id, PositionComponent pc) {
	componentStore.put(id, pc);
    }

    public void update(Tiles tiles) {
	for (PositionComponent pc : componentStore.values()) {
	    
	    redirtyTiles(tiles, pc);

	    pc.deltaX += pc.xRemainder;
	    pc.x += (int) pc.deltaX;
	    pc.xRemainder = pc.deltaX - (int) pc.deltaX;
	    pc.deltaX = 0;

	    pc.deltaY += pc.yRemainder;
	    pc.y += (int) pc.deltaY;
	    pc.yRemainder = pc.deltaY - (int) pc.deltaY;
	    pc.deltaY = 0;

	    calculateOnGround(pc, tiles);
	    calculateOnOneWayPlatform(pc, tiles);
	}
    }

    private static void redirtyTiles(Tiles tiles, PositionComponent pc) {
        int iMin = tiles.pixToTile(pc.x);
        int iMax = tiles.pixToTile(pc.x + pc.xBound - 1);
        int jMin = tiles.pixToTile(pc.y);
        int jMax = tiles.pixToTile(pc.y + pc.yBound - 1);

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

    private static void calculateOnGround(PositionComponent pc,
					  Tiles tiles) {
        int iMin = tiles.pixToTile(pc.x);
        int iMax = tiles.pixToTile(pc.x + pc.xBound - 1);
        int pixMiddle = pc.x + ((pc.xBound - 1) / 2);
        int iMiddle = tiles.pixToTile(pixMiddle);
        int jBelowFeet = tiles.pixToTile(pc.y + pc.yBound);

	// If you're outside the map you're not on the ground.
	if (iMin < 0 ||
	    iMax >= tiles.getWidth() ||
	    jBelowFeet >= tiles.getHeight()) {
	    pc.onGround = false;
	    return;
	}

        if (tiles.isSlope(iMiddle, jBelowFeet)) {
            pc.onGround =
		CollisionSubsystem.distanceToSlope(jBelowFeet, tiles, pc.x, pc.y,
						   pc.xBound, pc.yBound) <= 0;
	    return;
        }

        int i;
        for (i = iMin; i <= iMax; i++) {
            if (tiles.isSolid(i, jBelowFeet)) break;

	    // If we're in the middle of a one way tile, we're not on the
	    // ground.
            if (tiles.isOneWay(i, jBelowFeet) &&
		(pc.y + pc.yBound) % tiles.getPixPerTile() == 0) break;
	        
        }
        
        pc.onGround = i <= iMax;    
    }

    private static void calculateOnOneWayPlatform(PositionComponent pc,
						  Tiles tiles) {
        int iMin = tiles.pixToTile(pc.x);
        int iMax = tiles.pixToTile(pc.x + pc.xBound - 1);
        int pixMiddle = pc.x + ((pc.xBound - 1) / 2);
        int iMiddle = tiles.pixToTile(pixMiddle);
        int jBelowFeet = tiles.pixToTile(pc.y + pc.yBound);

	// If you're outside the map you're not on a platform.
	if (iMin < 0 ||
	    iMax >= tiles.getWidth() ||
	    jBelowFeet >= tiles.getHeight()) {
	    pc.onOneWayPlatform = false;
	    return;
	}

        int i;
        boolean hasOneWay = false;
        for (i = iMin; i <= iMax; i++) {
            if (tiles.isOneWay(i, jBelowFeet)) hasOneWay = true;
            if (!tiles.isOneWay(i, jBelowFeet) && !tiles.isEmpty(i, jBelowFeet)) break;
        }
        
        pc.onOneWayPlatform = i > iMax && hasOneWay;
    }
}
