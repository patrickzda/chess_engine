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

    public static void measureAveragePerformanceOfMiniMax(String[] fens, int passes, int depth){
        System.out.println("Alpha-Beta performance");

        double elapsedTime = 0d;
        long nanoStart = 0L;
        long nanoEnd = 0L;
        long nanoElapsed = 0L;
        System.out.println("Board, t in ms, t in ns, avg in ms, avg in ns, positions, positions/ms, depth");
        for (int i = 1; i < depth+1; i++) {
            for (String fen : fens) {
                MoveMasks moveMasks = new MoveMasks();
                Board board = new Board(fen);
                //String warmup = averageExecutionTime(board,moveMasks,passes/2);
                nanoStart = System.nanoTime();
                getBestMove(board,i,moveMasks);
                nanoEnd = System.nanoTime();
                nanoElapsed = nanoEnd - nanoStart;
                elapsedTime = nanoElapsed/1000000d;
                counterMoves = counterboards;
                String averageTime = averageExecutionTime(new Board(fen), moveMasks, passes);
                double posPerMs = counterMoves/elapsedTime;
                if (passes > 0) System.out.println(fen + ", " + elapsedTime + ", "+nanoElapsed+", "+averageTime+", "+counterMoves+", "+posPerMs+", "+i);
                counterboards= 0;
            }
        }
    }
    private static String averageExecutionTime(Board board,MoveMasks moveMasks, int passes){
        long[] nanoTimes = new long[passes];
       /* for (int i = 0; i < passes; i++){
            getBestMove(board,3,moveMasks);
        }*/
        for (int i = 0; i < passes; i++){
            long nanoStart = System.nanoTime();
            getBestMove(board,3,moveMasks);
            long nanoEnd = System.nanoTime();
            long nanoElapsed = nanoEnd -nanoStart;
            nanoTimes[i] = nanoElapsed;
        }
        long nanoAvg = findAverage(nanoTimes);
        double avg = nanoAvg/1000000d;
        return avg+", "+nanoAvg;
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
        counterboards++;
        Move[] moves = MoveGenerator.generateLegalMoves(board, moveMasks);

        if (depth == 0 || moves.length == 0) {
            return Evaluation.evaluate(board);
        }

        int score;

        for (int i = 0; i < moves.length; i++) {
            board.doMove(moves[i]);
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
        counterboards++;
        Move[] moves = MoveGenerator.generateLegalMoves(board, moveMasks);

        if (depth == 0 || moves.length == 0) {
            return -Evaluation.evaluate(board);
        }

        int score;

        for (int i = 0; i < moves.length; i++) {
            board.doMove(moves[i]);
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
        counterboards++;
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
