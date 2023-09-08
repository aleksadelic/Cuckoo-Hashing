package rs.ac.bg.etf.cuckoo;

public class AsymmetricCuckooHashTable<T> extends StandardCuckooHashTable<T> {

    public AsymmetricCuckooHashTable() {
        this(1536, (double) 1 / 3);
    }

    public AsymmetricCuckooHashTable(int capacity, double loadFactor) {
        super(capacity, loadFactor);
        this.firstTable = new Entry[(int) Math.floor((double) capacity * 2 / 3)];
        this.secondTable = new Entry[(int) Math.ceil((double) capacity / 3)];
    }

    public static void main(String[] args) {
        StandardCuckooHashTable<Integer> cuckoo = new AsymmetricCuckooHashTable<>(16, 0.3);
        cuckoo.insert(2);
        System.out.println(cuckoo.contains(2));
        System.out.println(cuckoo.contains(3));
        cuckoo.insert(3);
        System.out.println(cuckoo.contains(3));
        cuckoo.remove(2);
        System.out.println(cuckoo.contains(2));
        cuckoo.insert(4);
        cuckoo.insert(13);
        cuckoo.printHashTables();
        cuckoo.insert(23);
        cuckoo.printHashTables();
        cuckoo.insert(3);
        cuckoo.printHashTables();
        cuckoo.insert(1);
        cuckoo.printHashTables();
        cuckoo.insert(11);
        cuckoo.printHashTables();
        cuckoo.insert(21);
        cuckoo.printHashTables();
        cuckoo.insert(14);
        cuckoo.printHashTables();
    }

}
