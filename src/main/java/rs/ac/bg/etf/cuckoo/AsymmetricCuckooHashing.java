package rs.ac.bg.etf.cuckoo;

import org.apache.commons.codec.digest.MurmurHash3;

public class AsymmetricCuckooHashing<T> extends CuckooHashing<T> {

    public AsymmetricCuckooHashing() {
        this(1536, (double) 1 / 3);
    }

    public AsymmetricCuckooHashing(int capacity, double loadFactor) {
        super(capacity, loadFactor);
        this.firstTable = new Entry[(int) Math.floor((double) capacity * 2 / 3)];
        this.secondTable = new Entry[(int) Math.ceil((double) capacity / 3)];
    }

    public static void main(String[] args) {
        CuckooHashing<Integer> cuckoo = new AsymmetricCuckooHashing<>(16, 0.3);
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
