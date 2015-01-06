public class UniqueId {
    private final int id = UniqueIdGenerator.get().getNewId();

    public int hashCode() {
	return this.id;
    }

    public boolean greaterThan(UniqueId that) {
	return this.id > that.id;
    }

    public boolean equals(UniqueId that) {
	return this.id == that.id;
    }

    public String toString() {
	return Integer.toString(this.id);
    }
}
