package rs.ac.bg.etf.cuckoo;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Random;

public class Driver {

    int[] numbersForInsertion;
    int[] numbersForOperations;

    Random random;

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

    private void test(CuckooHashing hashTable) {
        for (int i = 0; i < numbersForInsertion.length; i++) {
            hashTable.insert(numbersForInsertion[i]);
        }
        for (int i = 0; i < numbersForOperations.length; i += 4) {
            hashTable.lookup(numbersForOperations[i]);
            hashTable.lookup(numbersForOperations[i + 1]);
            hashTable.remove(numbersForOperations[i + 2]);
            hashTable.insert(numbersForOperations[i + 3]);
        }
    }

    public static void main(String[] args) {
        System.out.println("GENERATING TEST DATA");
        Driver driver = new Driver("testData1.txt");
        System.out.println("GENERATED TEST DATA");
        System.out.println("RUNNING...");

        CuckooHashing<Integer> cuckoo = new CuckooHashing<>();
        AsymmetricCuckooHashing<Integer> asymmetricCuckoo = new AsymmetricCuckooHashing<>();
        HashSet<Integer> set = new HashSet<>();
        Instant start = Instant.now();
        driver.test(cuckoo);
        Instant end = Instant.now();
        Duration elapsedTime = Duration.between(start, end);
        System.out.println("Cuckoo: " + elapsedTime.toMillis());

        start = Instant.now();
        driver.test(asymmetricCuckoo);
        end = Instant.now();
        elapsedTime = Duration.between(start, end);
        System.out.println("Asymmetric Cuckoo: " + elapsedTime.toMillis());

        start = Instant.now();
        for (int i = 0; i < driver.numbersForInsertion.length; i++) {
            set.add(driver.numbersForInsertion[i]);
        }
        end = Instant.now();
        elapsedTime = Duration.between(start, end);
        System.out.println("Java: " + elapsedTime.toMillis());

    }

}
