package rs.ac.bg.etf.cuckoo;

import java.io.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Generator {

    private static Random random = new Random();

    public static void generateTestDataFile(int N, String filePath) {
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(filePath))) {
            int[][] testData = generateTestData(N);
            int[] numbersForInsertion = testData[0];
            int[] numbersForOperations = testData[1];

            dataOutputStream.writeInt(numbersForInsertion.length);
            for (int i = 0; i < numbersForInsertion.length; i++) {
                dataOutputStream.writeInt(numbersForInsertion[i]);
            }
            dataOutputStream.writeInt(numbersForOperations.length);
            for (int i = 0; i < numbersForOperations.length; i++) {
                dataOutputStream.writeInt(numbersForOperations[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int[][] generateTestData(int N) {
        int[] numbersForInsertion;
        int[] numbersForOperations;

        Set<Integer> set = new HashSet<>();
        while (set.size() < N) {
            set.add(random.nextInt(Integer.MAX_VALUE));
        }
        numbersForInsertion = set.stream().mapToInt(Integer::intValue).toArray();

        numbersForOperations = new int[3 * N * 4];
        for (int i = 0; i < numbersForOperations.length; i += 4) {
            // Generate number for successful lookup
            Integer[] setArray = set.toArray(new Integer[0]);
            int randomIndex = random.nextInt(setArray.length);
            int randomLookup = setArray[randomIndex];
            randomIndex = random.nextInt(setArray.length);
            int randomDeletion = setArray[randomIndex];
            numbersForOperations[i] = randomLookup;

            // Generate number for successful or unsuccessful lookup
            int randomNumber = random.nextInt(Integer.MAX_VALUE);
            numbersForOperations[i + 1] = randomNumber;

            // Generate random number to be deleted
            numbersForOperations[i + 2] = randomDeletion;
            set.remove(randomDeletion);

            // Generate random number to be inserted
            int randomInsertion = random.nextInt(Integer.MAX_VALUE);
            numbersForOperations[i + 3] = randomInsertion;
            set.add(randomInsertion);

            if (i % 10000 == 0) {
                System.out.println(i);
            }
        }
        return new int[][]{numbersForInsertion, numbersForOperations};
    }

    public static int[][] readTestDataFromFile(String filePath) {
        try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(filePath))) {
            int length = dataInputStream.readInt();
            int[] numbersForInsertion = new int[length];
            for (int i = 0; i < length; i++) {
                numbersForInsertion[i] = dataInputStream.readInt();
            }
            length = dataInputStream.readInt();
            int[] numbersForOperations = new int[length];
            for (int i = 0; i < length; i++) {
                numbersForOperations[i] = dataInputStream.readInt();
            }
            return new int[][]{numbersForInsertion, numbersForOperations};
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        Generator.generateTestDataFile(100000, "testData1.txt");
    }

}
