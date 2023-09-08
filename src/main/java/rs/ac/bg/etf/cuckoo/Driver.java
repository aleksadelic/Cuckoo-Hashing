package rs.ac.bg.etf.cuckoo;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Random;

public class Driver {

    private int[] numbersForInsertion;
    private int[] numbersForOperations;
    private Random random;

    public Driver(int N) {
        this.random = new Random();
        this.initializeData(N);
    }

    public Driver(String filePath) {
        this.random = new Random();
        this.initializeDataFromFile(filePath);
    }

    private void initializeData(int N) {
        int[][] testData = Generator.generateTestData(N);
        numbersForInsertion = testData[0];
        numbersForOperations = testData[1];
    }

    private void initializeDataFromFile(String fileName) {
        int[][] testData = Generator.readTestDataFromFile(fileName);
        numbersForInsertion = testData[0];
        numbersForOperations = testData[1];
    }

    private void testCuckoo(CuckooHashTable hashTable) {
        for (int i = 0; i < numbersForInsertion.length; i++) {
            hashTable.insert(numbersForInsertion[i]);
        }
        for (int i = 0; i < numbersForOperations.length; i += 4) {
            hashTable.contains(numbersForOperations[i]);
            hashTable.contains(numbersForOperations[i + 1]);
            hashTable.remove(numbersForOperations[i + 2]);
            hashTable.insert(numbersForOperations[i + 3]);
        }
    }

    private void testJavaHashSet(HashSet hashTable) {
        for (int i = 0; i < numbersForInsertion.length; i++) {
            hashTable.add(numbersForInsertion[i]);
        }
        for (int i = 0; i < numbersForOperations.length; i += 4) {
            hashTable.contains(numbersForOperations[i]);
            hashTable.contains(numbersForOperations[i + 1]);
            hashTable.remove(numbersForOperations[i + 2]);
            hashTable.add(numbersForOperations[i + 3]);
        }
    }

    public static void main(String[] args) {
        Driver driver = new Driver("testData1.txt");

        StandardCuckooHashTable<Integer> cuckoo = new StandardCuckooHashTable<>();
        AsymmetricCuckooHashTable<Integer> asymmetricCuckoo = new AsymmetricCuckooHashTable<>();
        HashSet<Integer> set = new HashSet<>();

        Instant start = Instant.now();
        driver.testCuckoo(cuckoo);
        Instant end = Instant.now();
        Duration elapsedTime = Duration.between(start, end);
        System.out.println("Cuckoo: " + elapsedTime.toMillis());

        start = Instant.now();
        driver.testCuckoo(asymmetricCuckoo);
        end = Instant.now();
        elapsedTime = Duration.between(start, end);
        System.out.println("Asymmetric Cuckoo: " + elapsedTime.toMillis());

        start = Instant.now();
        driver.testJavaHashSet(set);
        end = Instant.now();
        elapsedTime = Duration.between(start, end);
        System.out.println("Java HashSet: " + elapsedTime.toMillis());
    }

}
