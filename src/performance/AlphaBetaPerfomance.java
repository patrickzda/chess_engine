package performance;

import engine.ai.Evaluation;
import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.Board;
import engine.representation.Color;
import engine.representation.Move;

public class AlphaBetaPerfomance {
    public static int counterboards = 0;
    public static int counterMoves = 0;

    public static void measureAveragePerformanceOfAlphaBeta(String[] fens, int passes, int depth){
        System.out.println("Alpha-Beta performance");

        double elapsedTime = 0d;
        long nanoStart = 0L;
        long nanoEnd = 0L;
        long nanoElapsed = 0L;
        System.out.println("Board, avg in ms, positions, positions/s, depth, move");
        for (String fen : fens) {
            for (int i = 1; i < depth+1; i++) {
                MoveMasks moveMasks = new MoveMasks();
                Board board = new Board(fen);
                nanoStart = System.nanoTime();
                Move move = getBestMove(board,i,moveMasks);
                nanoEnd = System.nanoTime();
                nanoElapsed = nanoEnd - nanoStart;
                elapsedTime = nanoElapsed/1000000d;
                counterMoves = counterboards;
                String averageTime = averageExecutionTime(new Board(fen), moveMasks, passes,i);
                double sec = nanoElapsed/1000000000d;
                int posPerMs = (int) Math.round(counterMoves/sec);
                if (passes > 0) System.out.println(fen + ", "+averageTime+", "+counterMoves+", "+posPerMs+", "+i+", "+move.toString());
                counterboards= 0;
            }
        }
    }
    private static String averageExecutionTime(Board board,MoveMasks moveMasks, int passes,int depth){
        long[] nanoTimes = new long[passes];
        for (int i = 0; i < passes; i++){
            long nanoStart = System.nanoTime();
            getBestMove(board,depth,moveMasks);
            long nanoEnd = System.nanoTime();
            long nanoElapsed = nanoEnd -nanoStart;
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


    private static int alphaBetaMax(Board board, int alpha, int beta, int depth, MoveMasks moveMasks) {

        Move[] moves = MoveGenerator.generateLegalMoves(board, moveMasks);

        if (depth == 0 || moves.length == 0) {
            return Evaluation.evaluate(board);
        }

        int score;

        for (int i = 0; i < moves.length; i++) {
            board.doMove(moves[i]);
            counterboards++;
            score = alphaBetaMin(board, alpha, beta, depth - 1, moveMasks);
            board.undoLastMove();

            if (score >= beta) {    // beta-cutoff
                return beta;
            }

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
            counterboards++;
            score = alphaBetaMax(board, alpha, beta, depth - 1, moveMasks);
            board.undoLastMove();

            if (score <= alpha) {    // alpha-cutoff
                return alpha;
            }

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
            throw new IllegalArgumentException("Suchtiefe muss mindestes 1 sein!!!\n" + "Alpha-Beta Suche wird nicht funktionieren!");
        }

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        int score;
        Move bestMove = moves[0];

        for (int i = 0; i < moves.length; i++) {
            board.doMove(moves[i]);
            counterboards++;
            score = alphaBetaMin(board, alpha, beta, depth - 1, moveMasks); // Aufruf von AlphaBeta ohne sich die Moves zu merken
            board.undoLastMove();

            if (score > alpha) {
                alpha = score;
                bestMove = moves[i];    // in der ersten Suchtiefe den besten Move merken
            }
        }

        return bestMove;
    }
}
