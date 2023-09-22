package rs.ac.bg.etf.cuckoo;

public class Entry<T> {
    private T key;

    public Entry(T key) {
        this.key = key;
    }

    public T getKey() {
        return key;
    }

    public void setKey(T key) {
        this.key = key;
    }
}
