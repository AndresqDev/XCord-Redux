package xcord;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import xcord.Blacklist;

public class MemoryBlacklist
implements Blacklist {
    private final Object value = new Object();
    private final Map<String, Object> entries = new ConcurrentHashMap<String, Object>(1000);

    @Override
    public boolean add(String string) {
        return this.entries.put(string, this.value) == null;
    }

    @Override
    public boolean contains(String string) {
        return this.entries.get(string) != null;
    }

    @Override
    public int purge() {
        int n = this.entries.size();
        this.entries.clear();
        return n;
    }

    @Override
    public boolean isEmpty() {
        return this.entries.isEmpty();
    }
}

