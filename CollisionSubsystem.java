import java.util.HashMap;
import java.util.Map;

public class CollisionSubsystem {
    // implement singleton pattern
    private static final CollisionSubsystem singleton = new CollisionSubsystem();
    private CollisionSubsystem() {}
    public static CollisionSubsystem get() {
	return singleton;
    }

    // Member variables
    private HashMap<UniqueId, CollisionComponent> componentStore =
	new HashMap<UniqueId, CollisionComponent>();

    public CollisionComponent getComponent(UniqueId id) {
	return componentStore.get(id);
    }

    public void newComponent(UniqueId id, CollisionComponent cc) {
	cc.pc = PositionSubsystem.get().getComponent(id);
	cc.vc = VelocitySubsystem.get().getComponent(id);
	
	assert cc.pc != null;
	assert cc.vc != null;

	componentStore.put(id, cc);
    }

    public void update(Tiles tiles) {
	for (Map.Entry<UniqueId, CollisionComponent> entry1 : componentStore.entrySet()) {
	    UniqueId id1 = entry1.getKey();
	    CollisionComponent cc1 = entry1.getValue();
	    if (!cc1.collideWithEntities) continue;

	    for (Map.Entry<UniqueId, CollisionComponent> entry2 : componentStore.entrySet()) {
		// TODO: Do looping properly, starting from entry after entry1.
		UniqueId id2 = entry2.getKey();
		if (id1.greaterThan(id2)) continue;
		if (id1.equals(id2)) continue;
		
		CollisionComponent cc2 = entry2.getValue();
		if (!cc2.collideWithEntities) continue;
		
		entityEntityCollision(cc1, cc2);
	    }
	}

	for (CollisionComponent cc : componentStore.values()) {
	    if (!cc.collideWithTiles) continue;
	    entityTileCollision(tiles, cc);
	}
    }

    private void entityEntityCollision(CollisionComponent cc1,
				       CollisionComponent cc2) {

	// First we compute the bounding boxes that the two entities are projected to
	// occupy on at the end of the current frame.
	float xMin1 = cc1.pc.x + cc1.pc.deltaX + cc1.pc.xRemainder;
	float xMax1 = xMin1 + cc1.pc.xBound;
	float yMin1 = cc1.pc.y + cc1.pc.deltaY + cc1.pc.yRemainder;
	float yMax1 = yMin1 + cc1.pc.yBound;
	float xMin2 = cc2.pc.x + cc2.pc.deltaX + cc2.pc.xRemainder;
	float xMax2 = xMin2 + cc2.pc.xBound;
	float yMin2 = cc2.pc.y + cc2.pc.deltaY + cc2.pc.yRemainder;
	float yMax2 = yMin2 + cc2.pc.yBound;

	// Next we figure out whether the bounding boxes overlap.
	if (xMax1 > xMin2 &&
	    xMax2 > xMin1 &&
	    yMax1 > yMin2 &&
	    yMax2 > yMin1) {
	    
	    // Collision direction - positive xDirection means that the right
	    // side of entity 1 touched the left side of entity 2, and
	    // so forth.
	    float xDirection = cc1.pc.deltaX - cc2.pc.deltaX;
	    float yDirection = cc1.pc.deltaY - cc2.pc.deltaY;
	    
	    // Displacement - how much would they have to be transposed along a
	    // given axis to make them not collide?
	    float xDisplacement;
	    float yDisplacement;
	    if (xDirection > 0) {
		xDisplacement = xMax1 - xMin2;
	    } else {
		xDisplacement = xMin1 - xMax2;
	    }
	    
	    if (yDirection > 0) {
		yDisplacement = yMax1 - yMin2;
	    } else {
		yDisplacement = yMin1 - yMax2;
	    }

	    // Move both entities just far enough to separate them, along the x
	    // or y axis depending on which requires a smaller displacement.
	    if (Math.abs(xDisplacement) < Math.abs(yDisplacement)) {
		float xMultiple = xDisplacement / xDirection;
		if (cc1.solidToEntities && cc2.solidToEntities) {
		    cc1.pc.deltaX -= cc1.pc.deltaX * xMultiple;
		    cc2.pc.deltaX -= cc2.pc.deltaX * xMultiple;
		    cc1.vc.dx = 0;
		    cc2.vc.dx = 0;
		}
	    } else {
		float yMultiple = yDisplacement / yDirection;
		System.out.printf("delta1: %f, delta2: %f\n",
				   cc1.pc.deltaY, cc2.pc.deltaY);
		System.out.printf("Displacement: %f\n", yDisplacement);
		System.out.printf("Direction: %f\n", yDirection);
		System.out.printf("Multiple: %f\n", yMultiple);
		if (cc1.solidToEntities && cc2.solidToEntities) {
		    cc1.pc.deltaY -= cc1.pc.deltaY * yMultiple;
		    cc2.pc.deltaY -= cc2.pc.deltaY * yMultiple;
		    System.out.printf("delta1: %f, delta2: %f\n",
				      cc1.pc.deltaY, cc2.pc.deltaY);
		    
		    cc1.vc.dy = 0;
		    cc2.vc.dy = 0;
		}
	    }
	}
    }

    private void entityTileCollision(Tiles tiles,
				     CollisionComponent cc) {
	int iMin = tiles.pixToTile(cc.pc.x);
	int iMax = tiles.pixToTile(cc.pc.x + cc.pc.xBound - 1);
	int jMin = tiles.pixToTile(cc.pc.y);
	int jMax = tiles.pixToTile(cc.pc.y + cc.pc.yBound - 1);

	cc.hitWall = false;

	// For each line the bounding box intersects, scan for an obstacle
	if (cc.pc.deltaX > 0) {
	    int i = iMax + 1;
	    while (i < tiles.getWidth()) {
		int j;
		for (j = jMin ; j <= jMax; j++) {
		    if (tiles.isSlope(i, j)) {
			if (!tiles.isSlope(i-1, j) &&
			    tiles.getLeftY(i, j) + tiles.tileToPix(j) < cc.pc.y + cc.pc.yBound - 1) {
			    if (tiles.slopesLeft(i, j)) break;
			    if (tiles.slopesRight(i, j) && !tiles.isSlope(i-1, j+1)) break; 
			}
		    }
		    if (tiles.isSolid(i, j)) {
			if (!(tiles.isSlope(i-1, j) && 0 == tiles.getRightY(i-1, j))) break;
		    }

		    if (tiles.isOneWay(i, j)) {
			if (!tiles.isOneWay(i-1, j)) break;
		    }
		}
		if (j <= jMax) break;
		i++;
	    }

	    int distanceToObstacle = tiles.tileToPix(i) - (cc.pc.x + cc.pc.xBound);
	    if (distanceToObstacle < cc.pc.deltaX) {
		cc.pc.deltaX = (float) distanceToObstacle;
		cc.vc.dx = 0;
		cc.hitWall = true;
	    }
	} else if (cc.pc.deltaX < 0) {
	    int i = iMin - 1;
	    while (i >= 0) {
		int j;
		for (j = jMin ; j <= jMax; j++) {
		    if (tiles.isSlope(i, j)) {
			if (!tiles.isSlope(i+1,j) &&
			    tiles.getRightY(i, j) + tiles.tileToPix(j) < cc.pc.y + cc.pc.yBound - 1) {
			    if (tiles.slopesRight(i, j)) break;
			    if (tiles.slopesLeft(i, j) && !tiles.isSlope(i+1,j+1)) break;
			}
		    }
		    if (tiles.isSolid(i, j)) {
			if (!(tiles.isSlope(i+1, j) && 0 == tiles.getLeftY(i+1, j))) break;
		    }
		    if (tiles.isOneWay(i, j)) {
			if (!tiles.isOneWay(i+1, j)) break;
		    }
		}
		if (j <= jMax) break;
		i--;
	    }

	    int distanceToObstacle = tiles.tileToPix(i) + tiles.getPixPerTile() - cc.pc.x;
	    if (distanceToObstacle > cc.pc.deltaX) {
		cc.pc.deltaX = (float) distanceToObstacle;
		cc.vc.dx = 0;
		cc.hitWall = true;
	    }
	}

	int oldPixMiddle = cc.pc.x + ((cc.pc.xBound - 1) / 2);
	int oldiMiddle = tiles.pixToTile(oldPixMiddle);

	// We need to use the new value of X for the y-axis collision
	// calculations. However, we don't want to actually change the
	// position component , or else the position component won't be able
	// to properly mark dirty tiles during its update step.
	float newDeltaX = cc.pc.deltaX + cc.pc.xRemainder;
	int newX = cc.pc.x + (int) newDeltaX;

	iMin = tiles.pixToTile((int) newX);
	iMax = tiles.pixToTile((int) newX + cc.pc.xBound - 1);
	int pixMiddle = newX + ((cc.pc.xBound - 1) / 2);
	int iMiddle = tiles.pixToTile(pixMiddle);


	if (cc.pc.deltaY > 0) {
	    int j = jMax;
	    int minTopY = tiles.getPixPerTile();
	    boolean foundCollidingTile = false;
	    while (j < tiles.getHeight()) {
		int i;
		for (i = iMin; i <= iMax; i++) {
		    if (tiles.isSolid(i, j)) {
			foundCollidingTile = true;
			minTopY = 0;
		    }
		    if (tiles.isOneWay(i, j)) {
			foundCollidingTile = true;
			minTopY = 0;
		    }
		    if (tiles.isSlope(i, j) && i == iMiddle) {
			foundCollidingTile = true;
		    }
		    if (tiles.isSlope(i, j) && tiles.isEmpty(iMiddle, j)) {
			if (i < iMiddle && tiles.slopesRight(i, j)) {
			    foundCollidingTile = true;
			    int newTopY = tiles.getRightY(i, j);
			    minTopY = newTopY < minTopY ? newTopY : minTopY;
			}
			if (i > iMiddle && tiles.slopesLeft(i, j)) {
			    foundCollidingTile = true;
			    int newTopY = tiles.getLeftY(i, j);
			    minTopY = newTopY < minTopY ? newTopY : minTopY;
			}
		    }
		}
		if (foundCollidingTile) break;
		j++;
	    }
            
	    int distanceToObstacle;
	    if (tiles.isSlope(iMiddle, j)) {
		distanceToObstacle = distanceToSlope(j, tiles, newX, cc.pc.y,
						     cc.pc.xBound, cc.pc.yBound);
	    } else {
		distanceToObstacle = tiles.tileToPix(j) - (cc.pc.y + cc.pc.yBound) + minTopY;
	    }

	    boolean collision = false;
	    if (tiles.isSlope(iMiddle, jMax) ||
		tiles.isSlope(iMiddle, jMax-1) ||
		tiles.isSlope(oldiMiddle, jMax)) {
		if (distanceToObstacle < cc.pc.deltaY) {
		    collision = true;
		}
		if (cc.pc.onGround && distanceToObstacle < tiles.getPixPerTile()) {
		    collision = true;
		}
	    } else if (distanceToObstacle < cc.pc.deltaY && tiles.isSlope(oldiMiddle, jMax)) {
		collision = true;
	    } else if (distanceToObstacle < cc.pc.deltaY && distanceToObstacle >= 0) {
		collision = true;
	    }

	    if (collision) {
		cc.pc.deltaY = (float) distanceToObstacle;
		cc.pc.yRemainder = 0;
		cc.vc.dy = 0;
	    }
	} else if (cc.pc.deltaY < 0) {
	    int j = jMin - 1;
	    while (j >= 0) {
		int i;
		for (i = iMin; i <= iMax; i++) {
		    if (tiles.isSolid(i, j) || tiles.isSlope(i, j)) break;
		}
		if (i <= iMax) break;
		j--;
	    }

	    int distanceToObstacle = tiles.tileToPix(j) + tiles.getPixPerTile() - cc.pc.y;
	    if (distanceToObstacle > cc.pc.deltaY && distanceToObstacle <= 0) {
		cc.pc.deltaY = (float) distanceToObstacle;
		cc.pc.yRemainder = 0;
		cc.vc.dy = 0;
	    }
	}
    }

    public static int distanceToSlope(int j, Tiles tiles,
				      int x, int y,
				      int xBound, int yBound) {
        int pixMiddle = x + ((xBound - 1) / 2);
        int iMiddle = tiles.pixToTile(pixMiddle);
        
        float progress = ((float) (pixMiddle - tiles.tileToPix(iMiddle))) /
            tiles.getPixPerTile();
        
        return tiles.tileToPix(j) +
            (int) (tiles.getLeftY(iMiddle, j) * (1.0 - progress) +
                   tiles.getRightY(iMiddle, j) * progress)
            - (y + yBound - 1);
    }
}

