import java.util.HashMap;

public class ComponentStore<T> {
    private HashMap<UniqueId, T> map = new HashMap<UniqueId, T>();

    public void put(UniqueId id, T component) {
	assert map.get(id) == null;

	map.put(id, component);
    }

    public T get(UniqueId id) {
	T c = map.get(id);
	assert c != null;
	return c;
    }
}
