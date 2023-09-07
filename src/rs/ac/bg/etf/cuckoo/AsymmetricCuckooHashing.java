package rs.ac.bg.etf.cuckoo;

public class AsymmetricCuckooHashing<T> extends CuckooHashing<T> {

    public AsymmetricCuckooHashing(int capacity, float loadFactor) {
        this.size = 0;
        this.capacity = capacity;
        this.threshold = (int) Math.log(capacity);
        this.loadFactor = loadFactor;
        this.firstTable = new Entry[(int) Math.floor(capacity * 2 / 3)];
        this.secondTable = new Entry[(int) Math.ceil(capacity / 3)];
    }

    public static void main(String[] args) {
        CuckooHashing<Integer> cuckoo = new AsymmetricCuckooHashing<>(20, 0.3F);
        cuckoo.insert(2);
        System.out.println(cuckoo.lookup(2));
        System.out.println(cuckoo.lookup(3));
        cuckoo.insert(3);
        System.out.println(cuckoo.lookup(3));
        cuckoo.remove(2);
        System.out.println(cuckoo.lookup(2));
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
