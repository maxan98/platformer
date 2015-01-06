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
}
