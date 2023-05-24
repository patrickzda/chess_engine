package performance;

import engine.ai.Evaluation;
import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.Board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EvaluationPeformance {
    public static List<List<String>> rows = new ArrayList<>();
    public void measureAveragePerformanceOfEvaluation(String[] fens, int passes){
        System.out.println("Evaulation funktion");
        long startMilliTime = 0L;
        long stopTime = 0L;
        double elapsedTime = 0d;
        long nanoStart = 0L;
        long nanoEnd = 0L;
        long nanoElapsed = 0L;
        List<String> headers = Arrays.asList("Board","avg in ms");
        rows.add(headers);
        for (String fen : fens) {
            MoveMasks moveMasks = new MoveMasks();
            Board board = new Board(fen);
            String warmup = averageExecutionTime(board,moveMasks,passes/2);
            nanoStart = System.nanoTime();
            Evaluation.evaluate(board);
            nanoEnd = System.nanoTime();
            nanoElapsed = nanoEnd - nanoStart;
            elapsedTime = nanoElapsed/1000000d;
            String averageTime = averageExecutionTime(new Board(fen), moveMasks, passes);

            if (passes > 0){

                rows.add(Arrays.asList(fen,averageTime));
            }
        }
        System.out.println(AlphaBetaPerfomance.formatAsTable(rows));
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
            Evaluation.evaluate(board);
            long nanoEnd = System.nanoTime();
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            long nanoElapsed = nanoEnd -nanoStart;
            times[i] = elapsedTime;
            nanoTimes[i] = nanoElapsed;
        }
        long nanoAvg = findAverage(nanoTimes);
        double avg = nanoAvg/1000000d;
        return avg+"";
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
