public class SpriteSubsystem() {
    // implement singleton pattern
    private static final SpriteSubsystem singleton = new SpriteSubsystem();
    private SpriteSubsystem() {}
    public static SpriteSubsystem get() {
	return singleton;
    }

    // Member variables
    private ComponentStore<SpriteComponent> cs = new ComponentStore<SpriteComponent>();

    public SpriteComponent getComponent(UniqueId id) {
	return cs.get(id);
    }

    public void newComponent(UniqueId id, SpriteComponent sc) {
	cs.put(id, sc);
    }

    public void update(Graphics g) {
	for (HashMap.Entry<UniqueId, SpriteComponent> entry : cs.entrySet()) {
	    UniqueId id = entry.getKey();
	    SpriteComponent sc = entry.getValue();
	    
	    PositionComponent pc = PositionSubsystem.get().getComponent(id);
	    sc.sprite.draw(g, pc.x, pc.y);
	}
    }
}
