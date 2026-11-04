package xcord.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PersistentMap<K, V> implements Serializable {
    protected transient File file = null;
    protected final Map<K, V> map = new ConcurrentHashMap<K, V>();

    public PersistentMap(File file) {
        this.file = file;
    }

    public PersistentMap() {
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Map<K, V> getMap() {
        return this.map;
    }

    public V put(K key, V value) {
        return this.map.put(key, value);
    }

    public V get(K key) {
        return this.map.get(key);
    }

    public void save() throws IOException {
        if (this.file != null) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(this.file))) {
                objectOutputStream.writeObject(this);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
