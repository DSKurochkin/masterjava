package ru.javaops.masterjava.matrix;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        final int step = getStep(matrixSize);
        List<Future<int[][]>> futures = new LinkedList<>();

        for (int i = step; i <= matrixSize; i += step) {
            int[][] subA = getSubAMatrix(i - step, i, matrixA);
            futures.add(executor.submit(() -> singleThreadMultiply(subA, matrixB)));
        }

        boolean ready = false;
        while (!ready) {
            ready = futures.stream().allMatch(Future::isDone);
        }
        for (int i = 0; i < futures.size(); i++) {
            int[][] sub = futures.get(i).get();
            for (int j = 0; j < sub.length; j++) {
                for (int k = 0; k < matrixSize; k++) {
                    matrixC[i * step + j][k] = sub[j][k];
                }
            }
        }
        return matrixC;
    }

    private static int getStep(int size) {
        int res = size / 10;
        return res > 10 ? res : 1;
    }

    private static int[][] getSubAMatrix(int start, int end, int[][] source) {
        int size = end - start;
        int subIndex = 0;
        int[][] sub = new int[size][source.length];
        for (int i = start; i < end; i++) {
            for (int j = 0; j < source[0].length; j++) {
                sub[subIndex][j] = source[i][j];

            }
            subIndex++;
        }
        return sub;
    }

    // TODO optimize by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int rowsA = matrixA.length;
        final int columnsA = matrixA[0].length;
        final int rowsB = matrixB[0].length;
        final int[][] matrixC = new int[rowsA][columnsA];
        int[] currentColumn = new int[rowsB];

        try {
            for (int j = 0; ; j++) {
                for (int k = 0; k < columnsA; k++) {
                    currentColumn[k] = matrixB[k][j];
                }

                for (int i = 0; i < rowsA; i++) {
                    int[] currentRow = matrixA[i];
                    int sum = 0;
                    for (int k = 0; k < columnsA; k++) {
                        sum += currentRow[k] * currentColumn[k];
                    }
                    matrixC[i][j] = sum;
                }
            }
        } catch (IndexOutOfBoundsException ignored) {
        }

        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
