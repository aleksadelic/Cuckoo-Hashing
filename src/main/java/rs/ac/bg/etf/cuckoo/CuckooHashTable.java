package rs.ac.bg.etf.cuckoo;

public interface CuckooHashTable<T> {
    boolean contains(T key);
    boolean insert(T key);
    boolean remove(T key);
    int getSize();
    int getCapacity();
}
