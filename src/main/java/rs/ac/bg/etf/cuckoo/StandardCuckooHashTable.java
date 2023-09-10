package rs.ac.bg.etf.cuckoo;

import java.util.Random;
import org.apache.commons.codec.digest.MurmurHash3;

public class StandardCuckooHashTable<T> implements CuckooHashTable<T> {

    private int size;
    private int capacity;
    private int threshold;
    private final double loadFactor;
    private static final int MAXIMUM_CAPACITY = 1073741824;
    protected Entry<T>[] firstTable;
    protected Entry<T>[] secondTable;
    private final Random random;
    private int seed1;
    private int seed2;

    public StandardCuckooHashTable() {
        this(1024, (double) 1 / 3);
    }

    public StandardCuckooHashTable(int capacity, double loadFactor) {
        this.size = 0;
        this.capacity = capacity;
        this.threshold = (int) Math.ceil(3 * Math.log(capacity) / Math.log(2));
        this.loadFactor = loadFactor;
        this.firstTable = new Entry[capacity / 2];
        this.secondTable = new Entry[capacity / 2];
        this.random = new Random();
        this.seed1 = random.nextInt();
        this.seed2 = random.nextInt();
    }

    public int getCapacity() {
        return capacity;
    }

    protected int hashFunction1(T key) {
        return MurmurHash3.hash32(key.hashCode(), seed1) & (firstTable.length - 1);
    }

    protected int hashFunction2(T key) {
        return MurmurHash3.hash32(key.hashCode(), seed2) & (secondTable.length - 1);
    }

    public boolean insert(T key) {
        if (contains(key)) {
            return false;
        }
        T keyToInsert = key;
        for (int i = 0; i < threshold; i++) {
            Entry<T> entryToInsert = new Entry<>(keyToInsert);
            int index1 = hashFunction1(keyToInsert);
            keyToInsert = firstTable[index1] != null ? firstTable[index1].key : null;
            firstTable[index1] = entryToInsert;
            if (keyToInsert == null) {
                size++;
                return true;
            }

            entryToInsert = new Entry<>(keyToInsert);
            int index2 = hashFunction2(keyToInsert);
            keyToInsert = secondTable[index2] != null ? secondTable[index2].key : null;
            secondTable[index2] = entryToInsert;
            if (keyToInsert == null) {
                size++;
                return true;
            }
        }
        rehash();
        insert(keyToInsert);
        return true;
    }

    public boolean contains(T key) {
        int index1 = hashFunction1(key);
        if (firstTable[index1] != null && firstTable[index1].key.equals(key)) return true;
        int index2 = hashFunction2(key);
        return secondTable[index2] != null && secondTable[index2].key.equals(key);
    }

    public boolean remove(T key) {
        int index1 = hashFunction1(key);
        if (firstTable[index1] != null && firstTable[index1].key.equals(key)) {
            firstTable[index1] = null;
            size--;
            return true;
        }
        int index2 = hashFunction2(key);
        if (secondTable[index2] != null && secondTable[index2].key.equals(key)) {
            secondTable[index2] = null;
            size--;
            return true;
        }
        return false;
    }

    protected void rehash() {
        Entry<T>[] tempFirstTable = firstTable;
        Entry<T>[] tempSecondTable = secondTable;

        if ((double) size / capacity > loadFactor && capacity < MAXIMUM_CAPACITY) {
            capacity *= 2;
            threshold = (int) Math.ceil(3 * Math.log(capacity) / Math.log(2));

            firstTable = new Entry[firstTable.length * 2];
            secondTable = new Entry[secondTable.length * 2];
        } else {
            firstTable = new Entry[firstTable.length];
            secondTable = new Entry[secondTable.length];
        }

        size = 0;

        this.seed1 = random.nextInt();
        this.seed2 = random.nextInt();

        for (int i = 0; i < tempFirstTable.length; i++) {
            if (tempFirstTable[i] != null)
                insert(tempFirstTable[i].key);
        }
        for (int i = 0; i < tempSecondTable.length; i++) {
            if (tempSecondTable[i] != null)
                insert(tempSecondTable[i].key);
        }
    }

    public void printHashTables() {
        for (int i = 0; i < firstTable.length; i++) {
            System.out.print("|  " + i + "  ");
        }
        System.out.println("|");
        for (int i = 0; i < firstTable.length; i++) {
            System.out.print("______");
        }
        System.out.println();
        for (int i = 0; i < firstTable.length; i++) {
            System.out.print("|  ");
            if (firstTable[i] != null) System.out.print(firstTable[i].key + "  ");
            else System.out.print("   ");
        }
        System.out.println("|");
        for (int i = 0; i < firstTable.length; i++) {
            System.out.print("______");
        }
        System.out.println();
        for (int i = 0; i < secondTable.length; i++) {
            System.out.print("|  ");
            if (secondTable[i] != null) System.out.print(secondTable[i].key + "  ");
            else System.out.print("   ");
        }
        System.out.println("|\n\n");
    }

    public static void main(String[] args) {
        StandardCuckooHashTable<Integer> cuckoo = new StandardCuckooHashTable<Integer>(16, (double) 1 / 3);
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
        cuckoo.insert(9);
        cuckoo.printHashTables();
        cuckoo.insert(29);
        cuckoo.printHashTables();
        cuckoo.insert(27);
        cuckoo.printHashTables();
    }

}
