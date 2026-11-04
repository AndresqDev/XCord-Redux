package xcord.blacklist;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryBlacklist implements Blacklist {
    private final Object sentinel = new Object();
    private final Map<String, Object> entries = new ConcurrentHashMap<String, Object>(1000);

    @Override
    public boolean add(String address) {
        return this.entries.put(address, this.sentinel) == null;
    }

    @Override
    public boolean contains(String address) {
        return this.entries.get(address) != null;
    }

    @Override
    public int purge() {
        int size = this.entries.size();
        this.entries.clear();
        return size;
    }

    @Override
    public boolean isEmpty() {
        return this.entries.isEmpty();
    }
}
