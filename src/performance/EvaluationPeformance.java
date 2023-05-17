package performance;

import engine.ai.Evaluation;
import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.Board;

public class EvaluationPeformance {

        public void measureAveragePerformanceOfEvaluation(String[] fens, int passes){
            System.out.println("Evaulation funktion");
            long startMilliTime = 0L;
            long stopTime = 0L;
            double elapsedTime = 0d;
            long nanoStart = 0L;
            long nanoEnd = 0L;
            long nanoElapsed = 0L;
            System.out.println("Board , t in \u03BCs , t in ns , avg in \u03BCs , avg in ns");
            for (String fen : fens) {
                MoveMasks moveMasks = new MoveMasks();
                Board board = new Board(fen);
                String warmup = averageExecutionTime(board,moveMasks,passes/2);
                nanoStart = System.nanoTime();
                Evaluation.evaluate(board);
                nanoEnd = System.nanoTime();
                nanoElapsed = nanoEnd - nanoStart;
                elapsedTime = nanoElapsed/1000d;
                String averageTime = averageExecutionTime(new Board(fen), moveMasks, passes);

                if (passes > 0) System.out.println(fen + " , " + elapsedTime + " , "+nanoElapsed+" , "+averageTime);
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
                Evaluation.evaluate(board);
                long nanoEnd = System.nanoTime();
                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;
                long nanoElapsed = nanoEnd -nanoStart;
                times[i] = elapsedTime;
                nanoTimes[i] = nanoElapsed;
            }
            long nanoAvg = findAverage(nanoTimes);
            double avg = nanoAvg/1000d;
            return avg+" , "+nanoAvg;
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
