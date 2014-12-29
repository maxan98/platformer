public class UniqueId {
    private final int id = UniqueIdGenerator.get().getNewId();

    public int hashCode() {
	return this.id;
    }
}
