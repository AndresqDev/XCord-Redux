package xcord.blacklist;

public interface Blacklist {
    public boolean add(String address);

    public boolean contains(String address);

    public int purge();

    public boolean isEmpty();
}
