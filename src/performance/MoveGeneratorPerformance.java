package performance;


import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.Board;

import java.util.ArrayList;

public class MoveGeneratorPerformance {

    public void measureAveragePerformanceOnBoards(String[] fens, int passes){
        long startMilliTime = 0L;
        long stopTime = 0L;
        long elapsedTime = 0L;
        long nanoStart = 0L;
        long nanoEnd = 0L;
        long nanoElapsed = 0L;
        for (String fen : fens) {
            MoveMasks moveMasks = new MoveMasks();
            Board board = new Board(fen);
            String warmup = averageExecutionTime(board,moveMasks,passes/2);
            System.out.println("Warmup: "+warmup);
            startMilliTime = System.currentTimeMillis();
            nanoStart = System.nanoTime();
            MoveGenerator.generateLegalMoves(board, moveMasks);
            nanoEnd = System.nanoTime();
            stopTime = System.currentTimeMillis();
            nanoElapsed = nanoEnd - nanoStart;
            elapsedTime = stopTime - startMilliTime;
            String averageTime = averageExecutionTime(new Board(fen), moveMasks, passes);
            System.out.println("First execution with Board: "+fen+" took: " + elapsedTime+ " Milli sec Nano Time: "+nanoElapsed);
            if (passes > 0) System.out.println("The Average of the Movegenerator with "+passes+" Executions with this Board is: " + averageTime);
        }
    }

    private String averageExecutionTime(Board board,MoveMasks moveMasks, int passes){
        long[] times = new long[passes];
        long[] nanoTimes = new long[passes];
        for (int i = 0; i < passes; i++){
            MoveGenerator.generateLegalMoves(board,moveMasks);
        }
        for (int i = 0; i < passes; i++){
            long nanoStart = System.nanoTime();
            long startTime = System.currentTimeMillis();
            MoveGenerator.generateLegalMoves(board,moveMasks);
            long nanoEnd = System.nanoTime();
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            long nanoElapsed = nanoEnd -nanoStart;
            times[i] = elapsedTime;
            nanoTimes[i] = nanoElapsed;
        }
        long nanoAvg = findAverage(nanoTimes);
        long avg = findAverage(times);
        return avg+" milli sec nanosecAvg: "+nanoAvg;
    }
    private static long findAverage(long[] array){
        long sum = findSum(array);
        long res = sum / array.length;
        return res;
    }
    private static long findSum(long[] array) {
        long sum = 0;
        for (long value : array) {
            sum += value;
        }
        return sum;
    }

}
