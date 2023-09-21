package rs.ac.bg.etf.cuckoo;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;

public class Driver {

    private final int N;
    private int[] numbersForInsertion;
    private int[] numbersForOperations;
    private int[] lookupTimes;
    private int[] insertionTimes;
    private int[] deletionTimes;

    public Driver(int N, boolean isRandom) {
        this.N = N;
        if (isRandom) {
            initializeRandomData();
        } else {
            initializeData();
        }
        initializeTimesArrays();
    }

    public Driver(String filePath) {
        initializeDataFromFile(filePath);
        N = numbersForInsertion.length;
        initializeTimesArrays();
    }

    static class Result {
        long runtime;
        int bucketsAllocated;
        long memoryUsed;

        Result(long runtime, int bucketsAllocated, long memoryUsed) {
            Result.this.runtime = runtime;
            Result.this.bucketsAllocated = bucketsAllocated;
            Result.this.memoryUsed = memoryUsed;
        }

        Result() {
            this(0, 0, 0);
        }

        void add(Result result) {
            Result.this.runtime += result.runtime;
            Result.this.bucketsAllocated += result.bucketsAllocated;
            Result.this.memoryUsed += result.memoryUsed;
        }
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

    private void initializeRandomData() {
        int[][] testData = Generator.generateRandomTestData(N);
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

    private Result testCuckoo(StandardCuckooHashTable hashTable, String name, boolean testOperations) {
        for (int i = 0; i < numbersForInsertion.length; i++) {
            hashTable.insert(numbersForInsertion[i]);
        }

        if (testOperations) {
            return testCuckooWithOperations(hashTable, name);
        }

        Instant start = Instant.now();
        for (int i = 0; i < numbersForOperations.length; i += 4) {
            hashTable.contains(numbersForOperations[i]);
            hashTable.contains(numbersForOperations[i + 1]);
            ////////
            //hashTable.contains(numbersForOperations[i + 2]);
            //hashTable.contains(numbersForOperations[i + 3]);
            hashTable.remove(numbersForOperations[i + 2]);
            hashTable.insert(numbersForOperations[i + 3]);
        }
        Instant end = Instant.now();
        Duration elapsedTime = Duration.between(start, end);

        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();

        System.out.println(name + ": " + elapsedTime.toMillis() + " ms, buckets allocated: " + hashTable.getCapacity() +
                ", heap usage: " + heapMemoryUsage);
        return new Result(elapsedTime.toMillis(), hashTable.getCapacity(), heapMemoryUsage.getUsed());
    }

    public Result testCuckooWithOperations(StandardCuckooHashTable hashTable, String name) {
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

        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();

        System.out.println(name + ": " + elapsedTime.toMillis() + " ms, buckets allocated: " + hashTable.getCapacity());
        return new Result(elapsedTime.toMillis(), hashTable.getCapacity(), heapMemoryUsage.getUsed());
    }

    public Result testJavaHashSet(HashSet hashTable, boolean testOperations) {
        for (int i = 0; i < numbersForInsertion.length; i++) {
            hashTable.add(numbersForInsertion[i]);
        }

        if (testOperations) {
            return testJavaHashSetWithOperations(hashTable);
        }

        Instant start = Instant.now();
        for (int i = 0; i < numbersForOperations.length; i += 4) {
            hashTable.contains(numbersForOperations[i]);
            hashTable.contains(numbersForOperations[i + 1]);
            ///////
            //hashTable.contains(numbersForOperations[i + 2]);
            //hashTable.contains(numbersForOperations[i + 3]);
            hashTable.remove(numbersForOperations[i + 2]);
            hashTable.add(numbersForOperations[i + 3]);
        }
        Instant end = Instant.now();
        Duration elapsedTime = Duration.between(start, end);

        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();

        System.out.println("Java HashSet: " + elapsedTime.toMillis() + " ms" + ", heap usage: " + heapMemoryUsage);
        return new Result(elapsedTime.toMillis(), 0, heapMemoryUsage.getUsed());
    }

    public Result testJavaHashSetWithOperations(HashSet hashTable) {
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

        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();

        System.out.println("Java HashSet: " + elapsedTime.toMillis() + " ms" + ", heap usage: " + heapMemoryUsage);
        return new Result(elapsedTime.toMillis(), 0, heapMemoryUsage.getUsed());
    }

    public static void main(String[] args) {
        boolean testOperations = false;
        if (args.length != 0 && args[0].equals("-testOperations")) {
            testOperations = true;
        }

        int n = 10;
        int[] xAxis = new int[11];
        double[][][] yAxis = new double[3][4][11];
        String[] names = new String[]{"Cuckoo", "Asymmetric Cuckoo", "Blocked Cuckoo", "Java HashSet"};

        for (int i = 0; i <= 10; i++) {
            int N = i + 8;
            Driver driver = new Driver("dataset1/dataset" + N + ".txt");
            // Driver driver = new Driver(1 << N, true);

            Result cuckooTotal = new Result();
            Result asymmetricCuckooTotal = new Result();
            Result blockedCuckooTotal = new Result();
            Result hashSetTotal = new Result();

            for (int j = 0; j < n; j++) {
                StandardCuckooHashTable<Integer> cuckoo = new StandardCuckooHashTable<>(16, (double) 5 / 12);
                AsymmetricCuckooHashTable<Integer> asymmetricCuckoo = new AsymmetricCuckooHashTable<>(24, (double) 5 / 12);
                BlockedCuckooHashTable<Integer> blockedCuckooHashTable = new BlockedCuckooHashTable<>(16, 0.9, 4);
                HashSet<Integer> set = new HashSet<>();
                /*BlockedCuckooHashTable<Integer> cuckoo =  new BlockedCuckooHashTable<>(16, 0.8, 2);
                BlockedCuckooHashTable<Integer> asymmetricCuckoo =  new BlockedCuckooHashTable<>(16, 0.9, 4);
                BlockedCuckooHashTable<Integer> blockedCuckooHashTable =  new BlockedCuckooHashTable<>(16, 0.9, 8);
                BlockedCuckooHashTable<Integer> set =  new BlockedCuckooHashTable<>(16, 0.9, 8);*/

                cuckooTotal.add(driver.testCuckoo(cuckoo, "Standard Cuckoo", testOperations));
                cuckoo = null;
                System.gc();
                asymmetricCuckooTotal.add(driver.testCuckoo(asymmetricCuckoo, "Asymmetric Cuckoo", testOperations));
                asymmetricCuckoo = null;
                System.gc();
                blockedCuckooTotal.add(driver.testCuckoo(blockedCuckooHashTable, "Blocked Cuckoo", testOperations));
                blockedCuckooHashTable = null;
                System.gc();
                hashSetTotal.add(driver.testJavaHashSet(set, testOperations));
                //hashSetTotal.add(driver.testCuckoo(set,"random", testOperations));
                set = null;
                System.gc();
            }

            xAxis[i] = N;
            yAxis[0][0][i] = (double) cuckooTotal.runtime / n;
            yAxis[0][1][i] = (double) asymmetricCuckooTotal.runtime / n;
            yAxis[0][2][i] = (double) blockedCuckooTotal.runtime / n;
            yAxis[0][3][i] = (double) hashSetTotal.runtime / n;

            yAxis[1][0][i] = (double) cuckooTotal.bucketsAllocated / n;
            yAxis[1][1][i] = (double) asymmetricCuckooTotal.bucketsAllocated / n;
            yAxis[1][2][i] = (double) blockedCuckooTotal.bucketsAllocated / n;
            yAxis[1][3][i] = (double) hashSetTotal.bucketsAllocated / n;

            yAxis[2][0][i] = (double) cuckooTotal.memoryUsed / n;
            yAxis[2][1][i] = (double) asymmetricCuckooTotal.memoryUsed / n;
            yAxis[2][2][i] = (double) blockedCuckooTotal.memoryUsed / n;
            yAxis[2][3][i] = (double) hashSetTotal.memoryUsed / n;

        }

        Plotter.plotResults(xAxis, yAxis[0], names, "Cuckoo Hashing Variants Runtime", "runtime [ms]", "runtime.png");
        Plotter.plotResults(xAxis, yAxis[1], names, "Cuckoo Hashing Variants Buckets Allocated", "buckets", "buckets.png");
        Plotter.plotResults(xAxis, yAxis[2], names, "Cuckoo Hashing Variants Memory Usage", "memory", "memory.png");
    }

}
