package rs.ac.bg.etf.cuckoo;

import org.apache.commons.codec.digest.MurmurHash3;

import java.util.Random;

public class BlockedCuckooHashTable<T> implements CuckooHashTable<T> {

    private int size;
    private int capacity;
    private int threshold;
    private int blockSize;
    private final double loadFactor;
    private static final int MAXIMUM_CAPACITY = 1073741824;
    protected BlockedEntry<T>[] firstTable;
    protected BlockedEntry<T>[] secondTable;
    private final Random random;
    private int seed1;
    private int seed2;

    public BlockedCuckooHashTable() {
        this(1024, (double) 1 / 3, 2);
    }

    public BlockedCuckooHashTable(int capacity, double loadFactor, int blockSize) {
        this.size = 0;
        this.capacity = capacity;
        this.threshold = (int) Math.ceil(3 * Math.log(capacity) / Math.log(2));
        this.loadFactor = loadFactor;
        this.blockSize = blockSize;
        this.firstTable = new BlockedEntry[capacity / 2 / blockSize];
        this.secondTable = new BlockedEntry[capacity / 2 / blockSize];
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

    @Override
    public boolean contains(T key) {
        int index1 = hashFunction1(key);
        if (firstTable[index1] != null && firstTable[index1].contains(key)) {
            return true;
        }
        int index2 = hashFunction2(key);
        return secondTable[index2] != null && secondTable[index2].contains(key);
    }

    @Override
    public boolean insert(T key) {
        if (key == null || contains(key)) {
            return false;
        }
        T keyToInsert = key;
        for (int i = 0; i < threshold; i++) {
            int index1 = hashFunction1(keyToInsert);
            BlockedEntry<T> entryToInsert = firstTable[index1];
            if (entryToInsert == null) {
                entryToInsert = new BlockedEntry<>(keyToInsert, blockSize);
                firstTable[index1] = entryToInsert;
                size++;
                return true;
            } else {
                keyToInsert = entryToInsert.insert(keyToInsert);
                if (keyToInsert == null) {
                    size++;
                    return true;
                }
            }

            int index2 = hashFunction2(keyToInsert);
            entryToInsert = secondTable[index2];
            if (entryToInsert == null) {
                entryToInsert = new BlockedEntry<>(keyToInsert, blockSize);
                secondTable[index2] = entryToInsert;
                size++;
                return true;
            } else {
                keyToInsert = entryToInsert.insert(keyToInsert);
                if (keyToInsert == null) {
                    size++;
                    return true;
                }
            }
        }
        rehash();
        insert(keyToInsert);
        return true;
    }

    @Override
    public boolean remove(T key) {
        int index1 = hashFunction1(key);
        if (firstTable[index1] != null && firstTable[index1].remove(key)) {
            size--;
            return true;
        }
        int index2 = hashFunction2(key);
        if (secondTable[index2] != null && secondTable[index2].remove(key)) {
            size--;
            return true;
        }
        return false;
    }

    protected void rehash() {
        BlockedEntry<T>[] tempFirstTable = firstTable;
        BlockedEntry<T>[] tempSecondTable = secondTable;

        if ((double) size / capacity > loadFactor && capacity < MAXIMUM_CAPACITY) {
            capacity *= 2;
            threshold = (int) Math.ceil(3 * Math.log(capacity) / Math.log(2));

            firstTable = new BlockedEntry[firstTable.length * 2];
            secondTable = new BlockedEntry[secondTable.length * 2];
        } else {
            firstTable = new BlockedEntry[firstTable.length];
            secondTable = new BlockedEntry[secondTable.length];
        }

        size = 0;

        this.seed1 = random.nextInt();
        this.seed2 = random.nextInt();

        for (int i = 0; i < tempFirstTable.length; i++) {
            if (tempFirstTable[i] != null) {
                for (int j = 0; j < tempFirstTable[i].keys.length; j++) {
                    insert(tempFirstTable[i].keys[j]);
                }
            }
        }
        for (int i = 0; i < tempSecondTable.length; i++) {
            if (tempSecondTable[i] != null) {
                for (int j = 0; j < tempSecondTable[i].keys.length; j++) {
                    insert(tempSecondTable[i].keys[j]);
                }
            }
        }
    }

    public void printHashTables() {
        for (int i = 0; i < firstTable.length; i++) {
            System.out.print("|  " + i + "  ");
        }
        System.out.println("|");
        for (int i = 0; i < firstTable.length; i++) {
            System.out.print("________");
        }
        System.out.println();
        for (int i = 0; i < firstTable.length; i++) {
            System.out.print("|  ");
            if (firstTable[i] != null) System.out.print(firstTable[i].keys[0] + " " + firstTable[i].keys[1] + "  ");
            else System.out.print("   ");
        }
        System.out.println("|");
        for (int i = 0; i < firstTable.length; i++) {
            System.out.print("________");
        }
        System.out.println();
        for (int i = 0; i < secondTable.length; i++) {
            System.out.print("|  ");
            if (secondTable[i] != null) System.out.print(secondTable[i].keys[0] + " " + secondTable[i].keys[1] + "  ");
            else System.out.print("   ");
        }
        System.out.println("|\n\n");
    }

    public static void main(String[] args) {
        BlockedCuckooHashTable<Integer> cuckoo = new BlockedCuckooHashTable<>(16, (double) 1 / 3, 2);
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
