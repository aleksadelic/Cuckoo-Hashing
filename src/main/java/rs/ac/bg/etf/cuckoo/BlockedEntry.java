package rs.ac.bg.etf.cuckoo;

import java.util.Random;

public class BlockedEntry<T> {
    T[] keys;

    public BlockedEntry(T key, int blockSize) {
        keys = (T[]) new Object[blockSize];
        keys[0] = key;
    }

    public T insert(T key) {
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] == null) {
                keys[i] = key;
                return null;
            }
        }
        int index = new Random().nextInt(keys.length);
        T keyToReplace = keys[index];
        keys[index] = key;
        return keyToReplace;
    }

    public boolean contains(T key) {
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] != null && keys[i].equals(key)) {
                return true;
            }
        }
        return false;
    }

    public boolean remove(T key) {
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] != null && keys[i].equals(key)) {
                keys[i] = null;
                return true;
            }
        }
        return false;
    }
}
