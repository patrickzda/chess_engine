package performance;

import engine.ai.Evaluation;
import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.*;
import engine.tools.TranspositionTable;
import engine.tools.TranspositionTableEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NegamaxPerformance {
    public static int counterboards = 0;
    public static int counterMoves = 0;
    public static List<List<String>> rows = new ArrayList<>();
    private static final double EFFECTIVE_BRANCHING_FACTOR = Math.sqrt(35);
    private static final int QUIESCENCE_SEARCH_DEPTH = 2;
    private static final TranspositionTable table = new TranspositionTable();
    private static int search(Board board, int depth, MoveMasks masks, int alpha, int beta, int color){
        int startAlpha = alpha;

        //Lesen aus der Transposition-Table
        TranspositionTableEntry entry = table.getEntry(board, depth);
        if(entry != null){
            if(entry.getType() == EvaluationType.EXACT){
                return entry.getEvaluation();
            }else if(entry.getType() == EvaluationType.LOWERBOUND){
                alpha = Math.max(alpha, entry.getEvaluation());
            }else if(entry.getType() == EvaluationType.UPPERBOUND){
                beta = Math.min(beta, entry.getEvaluation());
            }

            if(alpha >= beta){
                return entry.getEvaluation();
            }
        }

        Move[] moves = MoveGenerator.generateLegalMoves(board, masks);

        if (depth == 0 || board.isGameLost(masks, moves.length) || moves.length == 0) {
            int finalEval = color * Evaluation.evaluateNegamax(board, masks);
            table.addEntry(board, null, depth, finalEval, EvaluationType.EXACT);
            return finalEval;
        }

        //Zugsortierung
        Evaluation.sortMoves(table, board, moves);

        int value = Integer.MIN_VALUE, bestValue = Integer.MIN_VALUE;
        Move bestMove = moves[0];

        for(int i = 0; i < moves.length; i++){
            board.doMove(moves[i]);
            counterboards++;

            value = Math.max(value, -search(board, depth - 1, masks, -beta, -alpha, -color));

            //PVS
            //if(i == 0){
            //    value = Math.max(value, -search(board, depth - 1, masks, -beta, -alpha, -color));
            //}else{
            //    int nullWindowValue = -search(board, depth - 1, masks, -alpha - 1, -alpha, -color);
            //    if(alpha < nullWindowValue && nullWindowValue < beta){
            //        value = Math.max(value, -search(board, depth - 1, masks, -beta, -nullWindowValue, -color));
            //    }else{
            //        value = Math.max(value, nullWindowValue);
            //    }
            //}
            board.undoLastMove();

            if(bestValue < value){
                bestValue = value;
                bestMove = moves[i];
            }

            alpha = Math.max(alpha, value);
            if(alpha >= beta){
                break;
            }
        }

        //Abspeichern in der Transposition-Table
        EvaluationType type = EvaluationType.EXACT;
        if(value <= startAlpha){
            type = EvaluationType.UPPERBOUND;
        }else if(value >= beta){
            type = EvaluationType.LOWERBOUND;
        }

        table.addEntry(board, bestMove, depth, value, type);

        return value;
    }
    //Ruhesuche, um den Horizonteffekt zu umgehen
    public static Move getBestMove(Board board, int depth, MoveMasks masks){
        Move[] moves = MoveGenerator.generateLegalMoves(board, masks);

        if(moves.length == 0){
            return null;
        }

        int bestScore = Integer.MIN_VALUE;
        Move bestMove = moves[0];

        int color = 1;
        if(board.getTurn() == Color.WHITE){
            color = -1;
        }
        int alpha = -Integer.MAX_VALUE;
        int beta = Integer.MAX_VALUE;

        for(int i = 0; i < moves.length; i++){
            board.doMove(moves[i]);
            counterboards++;
            int score = -search(board, depth - 1, masks, -beta, -alpha, color);
            board.undoLastMove();

            if(score > bestScore){
                alpha = score;
                bestScore = score;
                bestMove = moves[i];
            }
        }

        bestMove.evaluation = bestScore;
        return bestMove;
    }

    public static Move getBestMoveTimed(Board board, int timeInMilliseconds, MoveMasks masks){
        long endTime = System.nanoTime() + timeInMilliseconds * 1000000L, nextDepthSearchTime = 0L;
        int currentSearchDepth = 1;
        Move bestMove = null;

        while(System.nanoTime() + nextDepthSearchTime < endTime){
            long startTime = System.nanoTime();
            bestMove = getBestMove(board, currentSearchDepth, masks);
            currentSearchDepth++;
            nextDepthSearchTime = (long) ((System.nanoTime() - startTime) * EFFECTIVE_BRANCHING_FACTOR);
        }

        //System.out.println("REACHED DEPTH " + currentSearchDepth + " in " + (System.nanoTime() - (endTime - timeInMilliseconds * 1000000L)) / 1000000L + " ms");

        return bestMove;
    }
    public static void clearTable(){
        table.clear();
    }
    public static void measureAveragePerformanceOfNegamax(String[] fens, int passes, int depth){
        System.out.println("Negamax benchmark");

        double elapsedTime = 0d;
        long nanoStart = 0L;
        long nanoEnd = 0L;
        long nanoElapsed = 0L;
        List<String> headers = Arrays.asList("Board", "avg in ms", "positions", "positions/s", "depth", "move");
        rows.add(headers);
        for (String fen : fens) {
            for (int i = 1; i < depth+1; i++) {
                MoveMasks moveMasks = new MoveMasks();
                Board board = new Board(fen);
                nanoStart = System.nanoTime();
                Move move = getBestMove(board,i,moveMasks);
                nanoEnd = System.nanoTime();
                clearTable();
                nanoElapsed = nanoEnd - nanoStart;
                elapsedTime = nanoElapsed/1000000d;
                counterMoves = counterboards;
                double averageTime = averageExecutionTime(new Board(fen), moveMasks, passes,i);
                long posPerMs = Math.round((counterMoves/averageTime)*1000);
                if (passes > 0){
                    rows.add(Arrays.asList(fen,averageTime+"",counterMoves+"",posPerMs+"",i+"",move.toString()));
                    //System.out.println(fen + ", "+averageTime+", "+counterMoves+", "+posPerMs+", "+i+", "+move.toString());
                }
                counterboards= 0;
            }
        }
        System.out.println(formatAsTable(rows));
    }
    private static double averageExecutionTime(Board board,MoveMasks moveMasks, int passes,int depth){
        long[] nanoTimes = new long[passes];
        for (int i = 0; i < passes; i++){
            long nanoStart = System.nanoTime();
            getBestMove(board,depth,moveMasks);
            long nanoEnd = System.nanoTime();
            clearTable();
            long nanoElapsed = nanoEnd-nanoStart;
            nanoTimes[i] = nanoElapsed;
        }
        long nanoAvg = findAverage(nanoTimes);
        double avg = nanoAvg/1000000d;
        return avg;
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
    public static String formatAsTable(List<List<String>> rows) {
        int[] maxLengths = new int[rows.get(0).size()];
        for (List<String> row : rows)
        {
            for (int i = 0; i < row.size(); i++)
            {
                maxLengths[i] = Math.max(maxLengths[i], row.get(i).length());
            }
        }

        StringBuilder formatBuilder = new StringBuilder();
        for (int maxLength : maxLengths)
        {
            formatBuilder.append("%-").append(maxLength + 2).append("s");
        }
        String format = formatBuilder.toString();

        StringBuilder result = new StringBuilder();
        for (List<String> row : rows)
        {
            result.append(String.format(format, row.toArray(new String[0]))).append("\n");
        }
        return result.toString();
    }
}
