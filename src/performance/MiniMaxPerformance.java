package performance;

import engine.ai.Evaluation;
import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.Board;
import engine.representation.Color;
import engine.representation.Move;

public class MiniMaxPerformance {

    public void measureAveragePerformanceOfAlphaBeta(String[] fens, int passes){
        System.out.println("Movegenerator performance");
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
            MoveGenerator.generateLegalMoves(board, moveMasks);
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
            MoveGenerator.generateLegalMoves(board,moveMasks);
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

    private static int alphaBetaMax(Board board, int alpha, int beta, int depth, MoveMasks moveMasks) {
        Move[] moves = MoveGenerator.generateLegalMoves(board, moveMasks);

        if (depth == 0 || moves.length == 0) {
            return Evaluation.evaluate(board);
        }

        int score;

        for (int i = 0; i < moves.length; i++) {
            board.doMove(moves[i]);
            score = alphaBetaMin(board, alpha, beta, depth - 1, moveMasks);
            board.undoLastMove();

            if (score > alpha) {
                alpha = score;
            }
        }
        return alpha;
    }
    private static int alphaBetaMin(Board board, int alpha, int beta, int depth, MoveMasks moveMasks) {
        Move[] moves = MoveGenerator.generateLegalMoves(board, moveMasks);

        if (depth == 0 || moves.length == 0) {
            return -Evaluation.evaluate(board);
        }

        int score;

        for (int i = 0; i < moves.length; i++) {
            board.doMove(moves[i]);
            score = alphaBetaMax(board, alpha, beta, depth - 1, moveMasks);
            board.undoLastMove();

            if (score < beta) {
                beta = score;
            }
        }
        return beta;
    }
    // gibt den besten Move anhand der AlphaBeta Suche mit der Bewertungsfunktion zurück
    // ACHTUNG: depth muss >= 1 sein
    public static Move getBestMove(Board board, int depth, MoveMasks moveMasks) {
        Move[] moves = MoveGenerator.generateLegalMoves(board, moveMasks);

        if (depth < 1) {
            System.out.println("Suchtiefe muss mindestes 1 sein!!!\n" +
                    "Alpha-Beta Suche wird nicht funktionieren!");
            return moves[0];
        }

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        int score;
        Move bestMove = moves[0];

        if (board.getTurn() == Color.WHITE) {
            for (int i = 0; i < moves.length; i++) {
                board.doMove(moves[i]);
                score = alphaBetaMin(board, alpha, beta, depth - 1, moveMasks); // Aufruf von AlphaBeta ohne sich die Moves zu merken
                board.undoLastMove();

                if (score > alpha) {
                    alpha = score;
                    bestMove = moves[i];    // in der ersten Suchtiefe den besten Move merken
                }
            }
        }

        else {
            for (int i = 0; i < moves.length; i++) {
                board.doMove(moves[i]);
                score = alphaBetaMax(board, alpha, beta, depth - 1, moveMasks); // Aufruf von AlphaBeta ohne sich die Moves zu merken
                board.undoLastMove();

                if (score < beta) {
                    beta = score;
                    bestMove = moves[i];    // in der ersten Suchtiefe den besten Move merken
                }
            }
        }

        return bestMove;
    }
}
