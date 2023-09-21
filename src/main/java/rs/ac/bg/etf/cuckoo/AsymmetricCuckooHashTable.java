package rs.ac.bg.etf.cuckoo;

public class AsymmetricCuckooHashTable<T> extends StandardCuckooHashTable<T> {

    public AsymmetricCuckooHashTable() {
        this(1536, (double) 1 / 3);
    }

    public AsymmetricCuckooHashTable(int capacity, double loadFactor) {
        super(capacity, loadFactor);
        this.firstTable = new SingleEntry[(int) Math.floor((double) capacity * 2 / 3)];
        this.secondTable = new SingleEntry[(int) Math.ceil((double) capacity / 3)];
    }

}
