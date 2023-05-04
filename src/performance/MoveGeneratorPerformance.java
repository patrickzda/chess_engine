package performance;


import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.Board;
import engine.representation.Move;

import java.util.ArrayList;
import java.util.Arrays;

public class MoveGeneratorPerformance {

    public void FunktionToTest(){
        fillArray();
    }

    public void performanceMovegenratorOnBoards(String[] fens,int passes){
        long startMilliTime = 0L;
        long stopTime = 0L;
        long elapsedTime = 0L;
        long nanoStart = 0L;
        long nanoEnd = 0L;
        long nanoElapsed = 0L;
        for (String fen : fens) {
            MoveMasks moveMasks = new MoveMasks();
            startMilliTime = System.currentTimeMillis();
            nanoStart = System.nanoTime();
            perfomanceMoveGenerator(new Board(fen), moveMasks);
            nanoEnd = System.nanoTime();
            stopTime = System.currentTimeMillis();
            nanoElapsed = nanoEnd - nanoStart;
            elapsedTime = stopTime - startMilliTime;
            String averageTime = averageExecutionTime(new Board(fen), moveMasks, passes);
            System.out.println("First execution with Board: "+fen+" took: " + elapsedTime+ " Nano Time: "+nanoElapsed);
            if (passes > 0) System.out.println("The Average of the Movegenerator with "+passes+" Executions with this Board is: " + averageTime);
        }
    }
    public void perfomanceMoveGenerator(Board board, MoveMasks movemask){
        MoveGenerator.generateLegalMoves(board, movemask);
    }

    public void fillArray(){
        String arr[] = new String[100000000];
        for (int i = 0; i < arr.length; i++){
            arr[i] = "hello_"+i ;
        }
    }
    public void fillArrayList(){
        ArrayList<String> arr = new ArrayList<String>();
        for (int i = 0; i < 100000000; i++){
            arr.add("hello_"+i);
        }
    }

    String averageExecutionTime(Board board,MoveMasks moveMasks, int passes){
        long[] times = new long[passes];
        long[] nanotimes = new long[passes];
        for (int i = 0; i < passes; i++){
            long nanoStart = System.nanoTime();
            long startTime = System.currentTimeMillis();
            perfomanceMoveGenerator(board,moveMasks);
            long nanoEnd = System.nanoTime();
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            long nanoElapsed = nanoEnd -nanoStart;
            times[i] = elapsedTime;
            nanotimes[i] = nanoElapsed;
        }
        long nanoAvg = findAverage(nanotimes);
        long avg = findAverage(times);
        return ""+avg+" milli sec nanosecAvg: "+nanoAvg;
    }
    public static long findAverage(long[] array){
        long sum = findSumWithoutUsingStream(array);
        long res = sum / array.length;
        return res;
    }
    public static long findSumWithoutUsingStream(long[] array) {
        long sum = 0;
        for (long value : array) {
            sum += value;
        }
        return sum;
    }



}
