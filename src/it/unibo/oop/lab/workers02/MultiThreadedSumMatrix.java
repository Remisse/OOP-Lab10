package it.unibo.oop.lab.workers02;

import java.util.ArrayList;
import java.util.List;

/**
 * Multithreaded sum of all elements in a matrix.
 *
 */
public class MultiThreadedSumMatrix implements SumMatrix {

    /**
     * @param nWorkers
     *          Number of threads among which the sum will be split.
     */
    private final int nWorkers;

    /**
     * @param nWorkers
     *          Number of threads to use.
     */
    public MultiThreadedSumMatrix(final int nWorkers) {
        this.nWorkers = nWorkers;
    }

    @Override
    public double sum(final double[][] matrix) {
        final List<Worker> workers = new ArrayList<>(this.nWorkers);
        for (int i = 0; i < this.nWorkers; i++) {
            workers.add(new Worker(matrix));
        }
        for (final Worker w : workers) {
            w.start();
        }
        double result = 0;
        for (final Worker w : workers) {
            try {
                w.join();
                result += w.getResult();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * @param matrix
     *          Matrix of which the elements will be summed.
     * @param myId
     *          This thread's relative ID.
     * @param myStart
     *          Matrix row index from which the thread will start.
     * @param myEnd
     *          Matrix row index at which the thread will stop.
     * @param myResult
     *          Sum of all the elements assigned to this thread.
     */
    private class Worker extends Thread {
        private final double[][] matrix;
        private final int myId;
        private final int myStart;
        private final int myEnd;
        private double myResult;

        /*
         * Threads won't modify the matrix, so it's safe to ignore this.
         */
        @SuppressWarnings("PMD.ArrayIsStoredDirectly")
        Worker(final double[][] matrix) {
            this.matrix = matrix;
            /*
             * Get the thread's relative ID ("thread X of nWorkers").
             */
            this.myId = (int) this.getId() % nWorkers;
            /*
             * This will ensure that the overall work is split as evenly as possible among threads.
             */
            this.myStart = (int) (matrix[0].length * this.myId / nWorkers);
            this.myEnd = (int) (matrix[0].length * (this.myId + 1) / nWorkers);
        }

        @Override
        public void run() {
            System.out.println("Thread " + this.myId + ":\trow " + this.myStart + " to " + (this.myEnd - 1));
            for (int i = this.myStart; i < this.myEnd; i++) {
                for (int j = 0; j < this.matrix.length; j++) {
                    this.myResult += this.matrix[i][j];
                }
            }
        }

        /**
         * @return The sum of all elements assigned to this thread.
         */
        public double getResult() {
            return this.myResult;
        }
    }
}
