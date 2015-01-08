import java.awt.Graphics;
import java.util.HashMap;

public class SpriteSubsystem {
    // implement singleton pattern
    private static final SpriteSubsystem singleton = new SpriteSubsystem();
    private SpriteSubsystem() {}
    public static SpriteSubsystem get() {
	return singleton;
    }

    // Member variables
    private HashMap<UniqueId, SpriteComponent> componentStore =
	new HashMap<UniqueId, SpriteComponent>();

    public SpriteComponent getComponent(UniqueId id) {
	return componentStore.get(id);
    }

    public void newComponent(UniqueId id, SpriteComponent sc) {
	sc.pc = PositionSubsystem.get().getComponent(id);

	assert sc.pc != null;

	componentStore.put(id, sc);
    }

    public void update(Graphics g) {
	for (SpriteComponent sc : componentStore.values()) {
	    sc.update(g);
	}
    }
}
