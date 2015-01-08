import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
    private Random rng = new Random();

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
	for (CollisionComponent cc : componentStore.values()) {
	    cc.prework();
	}

	for (CollisionComponent cc : componentStore.values()) {
	    cc.checkCollisionWithTiles(tiles);
	}

	for (Map.Entry<UniqueId, CollisionComponent> entry1 : componentStore.entrySet()) {
	    UniqueId id1 = entry1.getKey();
	    CollisionComponent cc1 = entry1.getValue();

	    // Purely an optimization: collision checking is a no-op if either
	    // component has collideWithEntities == false.
	    if (!cc1.collideWithEntities) continue;

	    for (Map.Entry<UniqueId, CollisionComponent> entry2 : componentStore.entrySet()) {
		// TODO: Do looping properly, starting from entry after entry1.
		UniqueId id2 = entry2.getKey();

		// Avoid double checking or colliding entities with themselves.
		if (id1.greaterThan(id2)) continue;
		if (id1.equals(id2)) continue;
		
		CollisionComponent cc2 = entry2.getValue();

		// Randomly vary the order of collision testing. In theory,
		// a.checkCollisionWithEntity(b) should have exactly the same
		// effect as b.checkCollisionWithEntity(a), so this will have no
		// effect. If there are bugs where the order of testing is
		// significant, this will hopefully expose them.
		if (rng.nextFloat() > 0.5) {
		    cc1.checkCollisionWithEntity(cc2);
		} else {
		    cc2.checkCollisionWithEntity(cc1);
		}
	    }
	}

	for (CollisionComponent cc : componentStore.values()) {
	    cc.postwork();
	}
    }
}

