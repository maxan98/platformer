public class CollisionComponent {
    // If true, the entity will be stopped by solid tiles.
    // If false, the entity will pass through tiles as if they aren't there.
    public boolean collideWithTiles;
    
    // If true, the entity will be checked for collisions with other entities.
    // If false, collisions with other entities will be ignored.
    public boolean collideWithEntities;

    // If true for both of two entities, the entities will not be able to move
    // through each other. If false for one or both of the entities, they will pass
    // through each other.
    public boolean solidToEntities;
    
    // Did the entity hit a wall this turn? Only set if collideWithTiles is
    // true.
    public boolean hitWall;

    // If we immediately zero the velocity upon collision with something, then
    // that mucks with later collision detection. So we wait until the very end.
    private boolean zeroDxAtEndOfUpdate;
    private boolean zeroDyAtEndOfUpdate;

    public PositionComponent pc;
    public VelocityComponent vc;

    public void prework() {
	zeroDxAtEndOfUpdate = false;	
	zeroDyAtEndOfUpdate = false;
    }

    public void checkCollisionWithTiles(Tiles tiles) {
	if (!collideWithTiles) return;

	int iMin = tiles.pixToTile(pc.x);
	int iMax = tiles.pixToTile(pc.x + pc.xBound - 1);
	int jMin = tiles.pixToTile(pc.y);
	int jMax = tiles.pixToTile(pc.y + pc.yBound - 1);

	hitWall = false;

	// For each line the bounding box intersects, scan for an obstacle
	if (pc.deltaX > 0) {
	    int i = iMax + 1;
	    while (i < tiles.getWidth()) {
		int j;
		for (j = jMin ; j <= jMax; j++) {
		    if (tiles.isSlope(i, j)) {
			if (!tiles.isSlope(i-1, j) &&
			    tiles.getLeftY(i, j) + tiles.tileToPix(j) < pc.y + pc.yBound - 1) {
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

	    int distanceToObstacle = tiles.tileToPix(i) - (pc.x + pc.xBound);
	    if (distanceToObstacle < pc.deltaX) {
		pc.deltaX = (float) distanceToObstacle;
		zeroDxAtEndOfUpdate = true;
		hitWall = true;
	    }
	} else if (pc.deltaX < 0) {
	    int i = iMin - 1;
	    while (i >= 0) {
		int j;
		for (j = jMin ; j <= jMax; j++) {
		    if (tiles.isSlope(i, j)) {
			if (!tiles.isSlope(i+1,j) &&
			    tiles.getRightY(i, j) + tiles.tileToPix(j) < pc.y + pc.yBound - 1) {
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

	    int distanceToObstacle = tiles.tileToPix(i) + tiles.getPixPerTile() - pc.x;
	    if (distanceToObstacle > pc.deltaX) {
		pc.deltaX = (float) distanceToObstacle;
		zeroDxAtEndOfUpdate = true;
		hitWall = true;
	    }
	}

	int oldPixMiddle = pc.x + ((pc.xBound - 1) / 2);
	int oldiMiddle = tiles.pixToTile(oldPixMiddle);

	// We need to use the new value of X for the y-axis collision
	// calculations. However, we don't want to actually change the
	// position component , or else the position component won't be able
	// to properly mark dirty tiles during its update step.
	float newDeltaX = pc.deltaX + pc.xRemainder;
	int newX = pc.x + (int) newDeltaX;

	iMin = tiles.pixToTile((int) newX);
	iMax = tiles.pixToTile((int) newX + pc.xBound - 1);
	int pixMiddle = newX + ((pc.xBound - 1) / 2);
	int iMiddle = tiles.pixToTile(pixMiddle);


	if (pc.deltaY > 0) {
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
		distanceToObstacle = distanceToSlope(j, tiles, newX, pc.y,
						     pc.xBound, pc.yBound);
	    } else {
		distanceToObstacle = tiles.tileToPix(j) - (pc.y + pc.yBound) + minTopY;
	    }

	    boolean collision = false;
	    if (tiles.isSlope(iMiddle, jMax) ||
		tiles.isSlope(iMiddle, jMax-1) ||
		tiles.isSlope(oldiMiddle, jMax)) {
		if (distanceToObstacle < pc.deltaY) {
		    collision = true;
		}
		if (pc.onGround && distanceToObstacle < tiles.getPixPerTile()) {
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
		zeroDyAtEndOfUpdate = true;
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

	    int distanceToObstacle = tiles.tileToPix(j) + tiles.getPixPerTile() - pc.y;
	    if (distanceToObstacle > pc.deltaY && distanceToObstacle <= 0) {
		pc.deltaY = (float) distanceToObstacle;
		pc.yRemainder = 0;
		zeroDyAtEndOfUpdate = true;
	    }
	}
    }

    public void checkCollisionWithEntity(CollisionComponent that) {
	if (!this.collideWithEntities) return;
	if (!that.collideWithEntities) return;

	// First we compute the bounding boxes that the two entities are projected to
	// occupy on at the end of the current frame.
	float xFinalMin1 = this.pc.x + this.pc.deltaX + this.pc.xRemainder;
	float xFinalMax1 = xFinalMin1 + this.pc.xBound;
	float yFinalMin1 = this.pc.y + this.pc.deltaY + this.pc.yRemainder;
	float yFinalMax1 = yFinalMin1 + this.pc.yBound;
	float xFinalMin2 = that.pc.x + that.pc.deltaX + that.pc.xRemainder;
	float xFinalMax2 = xFinalMin2 + that.pc.xBound;
	float yFinalMin2 = that.pc.y + that.pc.deltaY + that.pc.yRemainder;
	float yFinalMax2 = yFinalMin2 + that.pc.yBound;

	// Next we figure out whether the bounding boxes overlap.
	if (xFinalMax1 > xFinalMin2 &&
	    xFinalMax2 > xFinalMin1 &&
	    yFinalMax1 > yFinalMin2 &&
	    yFinalMax2 > yFinalMin1) {

	    // Our strategy for collision resolution is to essentially "go back
	    // in time" by walking backwards along both entities' velocity
	    // vectors. For each dimension (x and y), we calculate:
	    // 
	    // * In which direction will the entities separate along this axis?
	    //   I.e. if 1.dx > 0 and 2.dx < 0, then when both are played
	    //   backwards 1 will end up to the left of 2. This gives us our
	    //   boundary condition - in this case, that the right edge of 1
	    //   should just be touching the left edge of 2.
	    // 
	    // * What multiple of the time step we need to play backwards to
	    //   separate the two along this dimension. I.e. if 1.dx = 2 and
	    //   2.dx = -4, and they were overlapping by 3 units, then we would
	    //   need to go back in time by a factor of 0.5.
	    //
	    // After doing this for x and y, we compare the factors, and choose
	    // the smallest one. If the smallest one is greater than one, this
	    // indicates that they were colliding the previous frame as well. 
	    // In this case, just separate them by the nearest route possible.
	}
    }

    public void postwork() {
	if (zeroDxAtEndOfUpdate) {
	    vc.dx = 0;
	}
	if (zeroDyAtEndOfUpdate) {
	    vc.dy = 0;
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
