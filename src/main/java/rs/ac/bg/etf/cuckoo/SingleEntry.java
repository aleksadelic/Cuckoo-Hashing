package rs.ac.bg.etf.cuckoo;

public class SingleEntry<T> extends Entry<T> {
    private T key;

    public SingleEntry(T key) {
        this.key = key;
    }

    public T getKey() {
        return key;
    }

    public void setKey(T key) {
        this.key = key;
    }
}
