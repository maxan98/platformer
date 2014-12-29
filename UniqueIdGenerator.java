public class UniqueIdGenerator {
    // Implement singleton pattern
    private static final UniqueIdGenerator singleton = new UniqueIdGenerator();
    private UniqueIdGenerator() {}
    public static UniqueIdGenerator get() {
	return singleton;
    }

    // Member variables
    private int nextId = 0;

    public synchronized int getNewId() {
	int idToReturn = nextId;
	nextId++;
	return idToReturn;
    }
}
