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

    public PositionComponent pc;
    public VelocityComponent vc;

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
		vc.dx = 0;
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
		vc.dx = 0;
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

	    int distanceToObstacle = tiles.tileToPix(j) + tiles.getPixPerTile() - pc.y;
	    if (distanceToObstacle > pc.deltaY && distanceToObstacle <= 0) {
		pc.deltaY = (float) distanceToObstacle;
		pc.yRemainder = 0;
		vc.dy = 0;
	    }
	}
    }

    public void checkCollisionWithEntity(CollisionComponent that) {
	if (!this.collideWithEntities) return;
	if (!that.collideWithEntities) return;

	// First we compute the bounding boxes that the two entities are projected to
	// occupy on at the end of the current frame.
	float xMin1 = this.pc.x + this.pc.deltaX + this.pc.xRemainder;
	float xMax1 = xMin1 + this.pc.xBound;
	float yMin1 = this.pc.y + this.pc.deltaY + this.pc.yRemainder;
	float yMax1 = yMin1 + this.pc.yBound;
	float xMin2 = that.pc.x + that.pc.deltaX + that.pc.xRemainder;
	float xMax2 = xMin2 + that.pc.xBound;
	float yMin2 = that.pc.y + that.pc.deltaY + that.pc.yRemainder;
	float yMax2 = yMin2 + that.pc.yBound;

	// Next we figure out whether the bounding boxes overlap.
	if (xMax1 > xMin2 &&
	    xMax2 > xMin1 &&
	    yMax1 > yMin2 &&
	    yMax2 > yMin1) {
	    
	    // Collision direction - positive xDirection means that the right
	    // side of entity 1 touched the left side of entity 2, and
	    // so forth.
	    float xDirection = this.pc.deltaX - that.pc.deltaX;
	    float yDirection = this.pc.deltaY - that.pc.deltaY;
	    
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
		if (this.solidToEntities && that.solidToEntities) {
		    this.pc.deltaX -= this.pc.deltaX * xMultiple;
		    that.pc.deltaX -= that.pc.deltaX * xMultiple;
		    this.vc.dx = 0;
		    that.vc.dx = 0;
		}
	    } else {
		float yMultiple = yDisplacement / yDirection;
		System.out.printf("delta1: %f, delta2: %f\n",
				  this.pc.deltaY, that.pc.deltaY);
		System.out.printf("Displacement: %f\n", yDisplacement);
		System.out.printf("Direction: %f\n", yDirection);
		System.out.printf("Multiple: %f\n", yMultiple);
		if (this.solidToEntities && that.solidToEntities) {
		    this.pc.deltaY -= this.pc.deltaY * yMultiple;
		    that.pc.deltaY -= that.pc.deltaY * yMultiple;
		    System.out.printf("delta1: %f, delta2: %f\n",
				      this.pc.deltaY, that.pc.deltaY);
		    
		    this.vc.dy = 0;
		    that.vc.dy = 0;
		}
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
