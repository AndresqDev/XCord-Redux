package xcord;

public interface Blacklist {
    public boolean add(String var1);

    public boolean contains(String var1);

    public int purge();

    public boolean isEmpty();
}

