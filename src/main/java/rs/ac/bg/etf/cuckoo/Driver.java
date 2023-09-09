package rs.ac.bg.etf.cuckoo;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;

public class Driver {

    private int N;
    private int[] numbersForInsertion;
    private int[] numbersForOperations;
    private int[] lookupTimes;
    private int[] insertionTimes;
    private int[] deletionTimes;

    public Driver(int N) {
        this.N = N;
        initializeData();
        initializeTimesArrays();
    }

    public Driver(String filePath) {
        initializeDataFromFile(filePath);
        N = numbersForInsertion.length;
        initializeTimesArrays();
    }

    private void initializeTimesArrays() {
        lookupTimes = new int[3 * N * 2];
        insertionTimes = new int[4 * N];
        deletionTimes = new int[3 * N];
    }

    private void initializeData() {
        int[][] testData = Generator.generateTestData(N);
        numbersForInsertion = testData[0];
        numbersForOperations = testData[1];
    }

    private void initializeDataFromFile(String fileName) {
        int[][] testData = Generator.readTestDataFromFile(fileName);
        numbersForInsertion = testData[0];
        numbersForOperations = testData[1];
    }

    private void analyzeOperationPerformance(String name) {
        int sum = 0;
        int minLookupTime = Integer.MAX_VALUE;
        int maxLookupTime = 0;
        for (int i = 0; i < lookupTimes.length; i++) {
            sum += lookupTimes[i];
            if (lookupTimes[i] < minLookupTime) minLookupTime = lookupTimes[i];
            if (lookupTimes[i] > maxLookupTime) maxLookupTime = lookupTimes[i];
        }
        double avgLookupTime = (double) sum / lookupTimes.length;

        sum = 0;
        int minInsertionTime = Integer.MAX_VALUE;
        int maxInsertionTime = 0;
        for (int i = 0; i < insertionTimes.length; i++) {
            sum += insertionTimes[i];
            if (insertionTimes[i] < minInsertionTime) minInsertionTime = insertionTimes[i];
            if (insertionTimes[i] > maxInsertionTime) maxInsertionTime = insertionTimes[i];
        }
        double avgInsertionTime = (double) sum / insertionTimes.length;

        sum = 0;
        int minDeletionTime = Integer.MAX_VALUE;
        int maxDeletionTime = 0;
        for (int i = 0; i < deletionTimes.length; i++) {
            sum += deletionTimes[i];
            if (deletionTimes[i] < minDeletionTime) minDeletionTime = deletionTimes[i];
            if (deletionTimes[i] > maxDeletionTime) maxDeletionTime = deletionTimes[i];
        }
        double avgDeletionTime = (double) sum / deletionTimes.length;

        System.out.println(name + ": avg lookup = " + avgLookupTime);
        System.out.println(name + ": avg insertion = " + avgInsertionTime);
        System.out.println(name + ": avg deletion = " + avgDeletionTime);
        initializeTimesArrays();
    }

    private void testCuckoo(CuckooHashTable hashTable, String name, boolean testOperations) {
        for (int i = 0; i < numbersForInsertion.length; i++) {
            hashTable.insert(numbersForInsertion[i]);
        }

        if (testOperations) {
            testCuckooWithOperations(hashTable, name);
            return;
        }

        Instant start = Instant.now();
        for (int i = 0; i < numbersForOperations.length; i += 4) {
            hashTable.contains(numbersForOperations[i]);
            hashTable.contains(numbersForOperations[i + 1]);
            hashTable.remove(numbersForOperations[i + 2]);
            hashTable.insert(numbersForOperations[i + 3]);
        }
        Instant end = Instant.now();
        Duration elapsedTime = Duration.between(start, end);
        System.out.println(name + ": " + elapsedTime.toMillis());
    }

    public void testCuckooWithOperations(CuckooHashTable hashTable, String name) {
        int lookupTimesIndex = 0;
        int insertionTimesIndex = 0;
        int deletionTimesIndex = 0;

        Instant start = Instant.now();

        for (int i = 0; i < numbersForOperations.length; i += 4) {
            Instant start1 = Instant.now();
            hashTable.contains(numbersForOperations[i]);
            Instant end1 = Instant.now();
            Duration elapsed = Duration.between(start1, end1);
            lookupTimes[lookupTimesIndex++] = elapsed.getNano();

            start1 = Instant.now();
            hashTable.contains(numbersForOperations[i + 1]);
            end1 = Instant.now();
            elapsed = Duration.between(start1, end1);
            lookupTimes[lookupTimesIndex++] = elapsed.getNano();

            start1 = Instant.now();
            hashTable.remove(numbersForOperations[i + 2]);
            end1 = Instant.now();
            elapsed = Duration.between(start1, end1);
            deletionTimes[deletionTimesIndex++] = elapsed.getNano();

            start1 = Instant.now();
            hashTable.insert(numbersForOperations[i + 3]);
            end1 = Instant.now();
            elapsed = Duration.between(start1, end1);
            insertionTimes[insertionTimesIndex++] = elapsed.getNano();
        }

        Instant end = Instant.now();
        Duration elapsedTime = Duration.between(start, end);
        analyzeOperationPerformance(name);
        System.out.println(name + ": " + elapsedTime.toMillis());
    }

    public void testJavaHashSet(HashSet hashTable, boolean testOperations) {
        for (int i = 0; i < numbersForInsertion.length; i++) {
            hashTable.add(numbersForInsertion[i]);
        }

        if (testOperations) {
            testJavaHashSetWithOperations(hashTable);
            return;
        }

        Instant start = Instant.now();
        for (int i = 0; i < numbersForOperations.length; i += 4) {
            hashTable.contains(numbersForOperations[i]);
            hashTable.contains(numbersForOperations[i + 1]);
            hashTable.remove(numbersForOperations[i + 2]);
            hashTable.add(numbersForOperations[i + 3]);
        }
        Instant end = Instant.now();
        Duration elapsedTime = Duration.between(start, end);
        System.out.println("Java HashSet: " + elapsedTime.toMillis());
    }

    public void testJavaHashSetWithOperations(HashSet hashTable) {
        int lookupTimesIndex = 0;
        int insertionTimesIndex = 0;
        int deletionTimesIndex = 0;

        Instant start = Instant.now();

        for (int i = 0; i < numbersForOperations.length; i += 4) {
            Instant start1 = Instant.now();
            hashTable.contains(numbersForOperations[i]);
            Instant end1 = Instant.now();
            Duration elapsed = Duration.between(start1, end1);
            lookupTimes[lookupTimesIndex++] = elapsed.getNano();

            start1 = Instant.now();
            hashTable.contains(numbersForOperations[i + 1]);
            end1 = Instant.now();
            elapsed = Duration.between(start1, end1);
            lookupTimes[lookupTimesIndex++] = elapsed.getNano();

            start1 = Instant.now();
            hashTable.remove(numbersForOperations[i + 2]);
            end1 = Instant.now();
            elapsed = Duration.between(start1, end1);
            deletionTimes[deletionTimesIndex++] = elapsed.getNano();

            start1 = Instant.now();
            hashTable.add(numbersForOperations[i + 3]);
            end1 = Instant.now();
            elapsed = Duration.between(start1, end1);
            insertionTimes[insertionTimesIndex++] = elapsed.getNano();
        }

        Instant end = Instant.now();
        Duration elapsedTime = Duration.between(start, end);
        analyzeOperationPerformance("Java HashSet");
        System.out.println("Java HashSet: " + elapsedTime.toMillis());
    }

    public static void main(String[] args) {
        boolean testOperations = false;
        if (args.length != 0 && args[0].equals("-testOperations")) {
            testOperations = true;
        }
        Driver driver = new Driver("testData2.txt");
        // Driver driver = new Driver(1000);

        StandardCuckooHashTable<Integer> cuckoo = new StandardCuckooHashTable<>();
        AsymmetricCuckooHashTable<Integer> asymmetricCuckoo = new AsymmetricCuckooHashTable<>();
        HashSet<Integer> set = new HashSet<>();

        driver.testCuckoo(cuckoo, "Cuckoo", testOperations);
        driver.testCuckoo(asymmetricCuckoo, "Asymmetric Cuckoo", testOperations);
        driver.testJavaHashSet(set, testOperations);
    }

}
