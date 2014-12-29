public class CollisionSubsystem() {
    // implement singleton pattern
    private static final CollisionSubsystem singleton = new CollisionSubsystem();
    private CollisionSubsystem() {}
    public static CollisionSubsystem get() {
	return singleton;
    }

    // Member variables
    private ComponentStore<CollisionComponent> cs = new ComponentStore<CollisionComponent>();

    public CollisionComponent getComponent(UniqueId id) {
	assert PositionSubsystem.get().getComponent(id);
	assert VelocityComponent.get().getComponent(id);

	return cs.get(id);
    }

    public void newComponent(UniqueId id, CollisionComponent cc) {
	cs.put(id, cc);
    }

    public void update(Tiles tiles) {
	for (HashMap.Entry<UniqueId, CollisionComponent> entry : cs.entrySet()) {
	    UniqueId id = entry.getKey();
	    CollisionComponent cc = entry.getValue();
	    PositionComponent pc = PositionSubsystem..get().getComponent(id);
	    VelocityComponent vc = VelocitySubsystem.get().getComponent(id);

	    int iMin = tiles.pixToTile(pc.x);
	    int iMax = tiles.pixToTile(pc.x + cc.xBound - 1);
	    int jMin = tiles.pixToTile(pc.y);
	    int jMax = tiles.pixToTile(pc.y + cc.yBound - 1);

	    for (int i = iMin; i <= iMax; i++) {
		for (int j = jMin; j <= jMax; j++) {
		    tiles.setDirty(i, j);
		}
	    }

	    cc.hitWall = false;

	    // For each line the bounding box intersects, scan for an obstacle
	    if (pc.deltaX > 0) {
		int i = iMax + 1;
		while (i < tiles.getWidth()) {
		    int j;
		    for (j = jMin ; j <= jMax; j++) {
			if (tiles.isSlope(i, j)) {
			    if (!tiles.isSlope(i-1, j) &&
				tiles.getLeftY(i, j) + tiles.tileToPix(j) < y + cc.yBound - 1) {
				if (tiles.slopesLeft(i, j)) break;
				if (tiles.slopesRight(i, j) && !tiles.isSlope(i-1, j+1)) break; 
			    }
			}
			if (tiles.isSolid(i, j)) {
			    if (!(tiles.isSlope(i-1, j) && 0 == tiles.getRightY(i-1, j))) break;
			}
		    }
		    if (j <= jMax) break;
		    i++;
		}

		int distanceToObstacle = tiles.tileToPix(i) - (pc.x + cc.xBound);
		if (distanceToObstacle < pc.deltaX) {
		    pc.deltaX = (float) distanceToObstacle;
		    vc.dx = 0;
		    cc.hitWall = true;
		}
	    } else if (pc.deltaX < 0) {
		int i = iMin - 1;
		while (i >= 0) {
		    int j;
		    for (j = jMin ; j <= jMax; j++) {
			if (tiles.isSlope(i, j)) {
			    if (!tiles.isSlope(i+1,j) &&
				tiles.getRightY(i, j) + tiles.tileToPix(j) < pc.y + cc.yBound - 1) {
				if (tiles.slopesRight(i, j)) break;
				if (tiles.slopesLeft(i, j) && !tiles.isSlope(i+1,j+1)) break;
			    }
			}
			if (tiles.isSolid(i, j)) {
			    if (!(tiles.isSlope(i+1, j) && 0 == tiles.getLeftY(i+1, j))) break;
			}
		    }
		    if (j <= jMax) break;
		    i--;
		}

		int distanceToObstacle = tiles.tileToPix(i) + tiles.getPixPerTile() - pc.x;
		if (distanceToObstacle > pc.deltaX) {
		    pc.deltaX = (float) distanceToObstacle;
		    vc.dx = 0;
		    cc.hitWall = true;
		}
	    }

	    int oldPixMiddle = pc.x + ((cc.xBound - 1) / 2);
	    int oldiMiddle = tiles.pixToTile(oldPixMiddle);
        
	    pc.deltaX += pc.xRemainder;
	    pc.x += (int) pc.deltaX;
	    pc.xRemainder = pc.deltaX - (int) pc.deltaX;
	    pc.deltaX = 0.0;

	    iMin = tiles.pixToTile((int) pc.x);
	    iMax = tiles.pixToTile((int) pc.x + cc.xBound - 1);
	    int pixMiddle = pc.x + ((cc.xBound - 1) / 2);
	    int iMiddle = tiles.pixToTile(pixMiddle);


	    if (pc.deltaY > 0) {
		int j = jMax;
		while (j < tiles.getHeight()) {
		    int i;
		    for (i = iMin; i <= iMax; i++) {
			if (tiles.isSolid(i, j)) break;
			if (tiles.isOneWay(i, j)) break;
			if (tiles.isSlope(i, j) && i == iMiddle) break;
			if (tiles.isSlope(i, j) && tiles.isEmpty(iMiddle, j)) {
			    if (i < iMiddle && tiles.slopesRight(i, j)) break;
			    if (i > iMiddle && tiles.slopesLeft(i, j)) break;
			}
		    }
		    if (i <= iMax) break;
		    j++;
		}
            
		// TODO: Properly compute distance when the player is part-way
		// off of a slope - Right now, if the player's center is not
		// above a slope block but the slope block is the one causing
		// the collision, the slope block is treated as a full
		// block. Fixing this will likely require changing the algorithm
		// so it separately computes the distance to each square and
		// then takes the maximum.
		int distanceToObstacle;
		if (tiles.isSlope(iMiddle, j)) {
		    float progress = ((float) (pixMiddle - tiles.tileToPix(iMiddle))) /
			tiles.getPixPerTile();
        
		    distanceToObstacle =
			tiles.tileToPix(j) +
			(int) (tiles.getLeftY(iMiddle, j) * (1.0 - progress) +
			       tiles.getRightY(iMiddle, j) * progress)
			- (pc.y + cc.yBound - 1);
		} else {
		    distanceToObstacle = tiles.tileToPix(j) - (pc.y + cc.yBound);
		}

		boolean collision = false;
		if (tiles.isSlope(iMiddle, jMax) ||
		    tiles.isSlope(iMiddle, jMax-1) ||
		    tiles.isSlope(oldiMiddle, jMax)) {
		    if (distanceToObstacle < pc.deltaY) {
			collision = true;
		    }
		    if (cc.onGround && distanceToObstacle < tiles.getPixPerTile()) {
			collision = true;
		    }
		} else if (distanceToObstacle < pc.deltaY && tiles.isSlope(oldiMiddle, jMax)) {
		    collision = true;
		} else if (distanceToObstacle < pc.deltaY && distanceToObstacle >= 0) {
		    collision = true;
		}

		if (collision) {
		    pc.deltaY = (float) distanceToObstacle;
		    pc.yRemainder = 0;
		    vc.dy = 0;
		}
	    } else if (pc.deltaY < 0) {
		int j = jMin - 1;
		while (j >= 0) {
		    int i;
		    for (i = iMin; i <= iMax; i++) {
			if (tiles.isSolid(i, j) || tiles.isSlope(i, j)) break;
		    }
		    if (i <= iMax) break;
		    j--;
		}

		int distanceToObstacle = tiles.tileToPix(j) + tiles.getPixPerTile() - y;
		if (distanceToObstacle > pc.deltaY && distanceToObstacle <= 0) {
		    pc.deltaY = (float) distanceToObstacle;
		    pc.yRemainder = 0;
		    vc.dy = 0;
		}
	    }
	    
	    calculateOnGround(pc, cc, tiles);
	    calculateOnOneWayPlatform(pc, cc, tiles);
	}
    }

    private static void calculateOnGround(PositionComponent pc,
					  CollisionComponent cc,
					  Tiles tiles) {
        int iMin = tiles.pixToTile(pc.x);
        int iMax = tiles.pixToTile(pc.x + cc.xBound - 1);
        int pixMiddle = pc.x + ((cc.xBound - 1) / 2);
        int iMiddle = tiles.pixToTile(pixMiddle);
        int jBelowFeet = tiles.pixToTile(pc.y + cc.yBound);

        if (tiles.isSlope(iMiddle, jBelowFeet)) {
            cc.onGround = distanceToSlope(jBelowFeet) <= 0;
	    return;
        }

        int i;
        for (i = iMin; i <= iMax; i++) {
            if (tiles.isSolid(i, jBelowFeet)) break;
            if (tiles.isOneWay(i, jBelowFeet)) break;
        }
        
        cc.onGround = i <= iMax;    
    }

    private static void calculateOnOneWayPlatform(PositionComponent pc,
						  CollisionComponent cc,
						  Tiles tiles) {
        int iMin = tiles.pixToTile(pc.x);
        int iMax = tiles.pixToTile(pc.x + cc.xBound - 1);
        int pixMiddle = pc.x + ((cc.xBound - 1) / 2);
        int iMiddle = tiles.pixToTile(pixMiddle);
        int jBelowFeet = tiles.pixToTile(pc.y + cc.yBound);

        int i;
        boolean hasOneWay = false;
        for (i = iMin; i <= iMax; i++) {
            if (tiles.isOneWay(i, jBelowFeet)) hasOneWay = true;
            if (!tiles.isOneWay(i, jBelowFeet) && !tiles.isEmpty(i, jBelowFeet)) break;
        }
        
        cc.onOneWayPlatform = i > iMax && hasOneWay;
    }


}

