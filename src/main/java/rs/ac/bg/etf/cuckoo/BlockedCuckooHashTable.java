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
    private BlockedEntry<T>[] firstTable;
    private BlockedEntry<T>[] secondTable;
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

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    private int hashFunction1(T key) {
        return MurmurHash3.hash32(key.hashCode(), seed1) & (firstTable.length - 1);
    }

    private int hashFunction2(T key) {
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
                T[] keys = tempFirstTable[i].getKeys();
                for (int j = 0; j < keys.length; j++) {
                    insert(keys[j]);
                }
            }
        }
        for (int i = 0; i < tempSecondTable.length; i++) {
            if (tempSecondTable[i] != null) {
                T[] keys = tempSecondTable[i].getKeys();
                for (int j = 0; j < keys.length; j++) {
                    insert(keys[j]);
                }
            }
        }
    }

}
