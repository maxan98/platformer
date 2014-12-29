import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ComponentStore<T> {
    private HashMap<UniqueId, T> map = new HashMap<UniqueId, T>();

    public void put(UniqueId id, T component) {
	assert map.get(id) == null;

	map.put(id, component);
    }

    public Set<Map.Entry<UniqueId, T>> entrySet() {
	return map.entrySet();
    }

    public T get(UniqueId id) {
	T c = map.get(id);
	assert c != null;
	return c;
    }
}
