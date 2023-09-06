package rs.ac.bg.etf.cuckoo;

public class CuckooHashing<T> {

    int size;
    int capacity;
    int threshold;
    float loadFactor;
    Entry<T>[] firstTable;
    Entry<T>[] secondTable;

    class Entry<E> {
        E key;

        public Entry(E key) {
            this.key = key;
        }
    }

    public CuckooHashing() {
        this(1024, 1/3);
    }

    public CuckooHashing(int capacity, float loadFactor) {
        this.size = 0;
        this.capacity = capacity;
        this.threshold = (int) Math.log(capacity);
        this.loadFactor = loadFactor;
        this.firstTable = new Entry[capacity / 2];
        this.secondTable = new Entry[capacity / 2];
    }

    private int hashFunction1(T key) {
        return key.hashCode() % firstTable.length;
    }

    private int hashFunction2(T key) {
        return (key.hashCode() / secondTable.length) % secondTable.length;
    }

    public void insert(T key) {
        if (lookup(key)) {
            return;
        }
        T keyToInsert = key;
        for (int i = 0; i < threshold; i++) {
            Entry<T> entryToInsert = new Entry<>(keyToInsert);
            int index1 = hashFunction1(keyToInsert);
            keyToInsert = firstTable[index1] != null ? firstTable[index1].key : null;
            firstTable[index1] = entryToInsert;
            if (keyToInsert == null) {
                size++;
                if (size / capacity > loadFactor) rehash();
                return;
            }

            entryToInsert = new Entry<>(keyToInsert);
            int index2 = hashFunction2(keyToInsert);
            keyToInsert = secondTable[index2] != null ? secondTable[index2].key : null;
            secondTable[index2] = entryToInsert;
            if (keyToInsert == null) {
                size++;
                if (size / capacity > loadFactor) rehash();
                return;
            }
        }
        rehash();
        insert(keyToInsert);
    }

    public boolean lookup(T key) {
        int index1 = hashFunction1(key);
        if (firstTable[index1] != null && firstTable[index1].key.equals(key)) return true;
        int index2 = hashFunction2(key);
        return secondTable[index2] != null && secondTable[index2].key.equals(key);
    }

    public boolean remove(T key) {
        int index1 = hashFunction1(key);
        int index2 = hashFunction2(key);
        if (firstTable[index1].key.equals(key)) {
            firstTable[index1] = null;
            size--;
            return true;
        }
        if (secondTable[index2].key.equals(key)) {
            secondTable[index2] = null;
            size--;
            return true;
        }
        return false;
    }

    private void rehash() {
        if (size / capacity > loadFactor) {
            capacity *= 2;
            threshold = (int) Math.log(capacity);
        }
        Entry<T>[] tempFirstTable = firstTable;
        Entry<T>[] tempSecondTable = secondTable;

        firstTable = new Entry[firstTable.length * 2];
        secondTable = new Entry[secondTable.length * 2];

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
        CuckooHashing<Integer> cuckoo = new CuckooHashing<Integer>(20, 0.3F);
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
    }

}
