package engine.ai;

import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.*;
import java.util.Arrays;

import static java.lang.Math.*;

public class AlphaBeta {

    static final TranspositionTable table = new TranspositionTable();

    private static int alphaBetaMax(Board board, int alpha, int beta, int depth, MoveMasks moveMasks) {
        TranspositionTableEntry entry = table.getEntry(board, depth);
        if(entry != null){
            return entry.getEvaluation();
        }

        Move[] moves = MoveGenerator.generateLegalMoves(board, moveMasks);
        Evaluation.sortMoves(table, board, moves);

        if (depth == 0 || board.isGameLost(moveMasks, moves.length) || moves.length == 0) {
            int finalEval = Evaluation.evaluate(board, moveMasks);
            table.addEntry(board, new Move(0, 0, PieceType.PAWN), depth, finalEval, EvaluationType.EXACT);
            return finalEval;
        }

        Move bestMove = moves[0];
        int score;

        for (int i = 0; i < moves.length; i++) {
            board.doMove(moves[i]);
            score = alphaBetaMin(board, alpha, beta, depth - 1, moveMasks);
            board.undoLastMove();

            if (score >= beta) {
                return beta;
            }

            if (score > alpha) {
                bestMove = moves[i];
                alpha = score;
            }
        }

        table.addEntry(board, bestMove, depth, -alpha, EvaluationType.EXACT);
        return alpha;
    }

    private static int alphaBetaMin(Board board, int alpha, int beta, int depth, MoveMasks moveMasks) {
        TranspositionTableEntry entry = table.getEntry(board, depth);
        if(entry != null){
            return -entry.getEvaluation();
        }

        Move[] moves = MoveGenerator.generateLegalMoves(board, moveMasks);
        Evaluation.sortMoves(table, board, moves);

        if (depth == 0 || board.isGameLost(moveMasks, moves.length) || moves.length == 0) {
            int finalEval = -Evaluation.evaluate(board, moveMasks);
            table.addEntry(board, new Move(0, 0, PieceType.PAWN), depth, -finalEval, EvaluationType.EXACT);
            return finalEval;
        }

        Move bestEnemyMove = moves[0];
        int score;

        for (int i = 0; i < moves.length; i++) {
            board.doMove(moves[i]);
            score = alphaBetaMax(board, alpha, beta, depth - 1, moveMasks);
            board.undoLastMove();

            if (score <= alpha) {
                return alpha;
            }

            if (score < beta) {
                bestEnemyMove = moves[i];
                beta = score;
            }
        }
        table.addEntry(board, bestEnemyMove, depth, -beta, EvaluationType.EXACT);
        return beta;
    }

    public static Move getBestMove(Board board, Move[] moves, int depth, MoveMasks moveMasks) {
        if (depth < 1) {
            throw new IllegalArgumentException("Suchtiefe muss mindestes 1 sein!!!\n" + "Alpha-Beta Suche wird nicht funktionieren!");
        }

        if(moves.length == 0){
            return null;
        }

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        int score;
        Move bestMove = moves[0];

        for (int i = 0; i < moves.length; i++) {
            board.doMove(moves[i]);
            score = alphaBetaMin(board, alpha, beta, depth - 1, moveMasks); // Aufruf von AlphaBeta ohne sich die Moves zu merken
            moves[i].evaluation = score;
            board.undoLastMove();

            if (score > alpha) {
                alpha = score;
                bestMove = moves[i];
            }
        }

        table.addEntry(board, bestMove, depth, bestMove.evaluation, EvaluationType.EXACT);

        return bestMove;
    }

    public static Move getBestMoveTimed(Board board, MoveMasks moveMasks, int millis) {
        long startTime = System.nanoTime(), beginningTime = System.nanoTime();
        long finishTime = startTime + (millis * 1000000L);

        long nextDepthSearchTime = 0;
        int searchDepth = 0;

        Move bestMove = new Move(0, 0, PieceType.PAWN);
        Move[] moves = MoveGenerator.generateLegalMoves(board, moveMasks);

        while (System.nanoTime() + nextDepthSearchTime < finishTime) {
            searchDepth++;
            startTime = System.nanoTime();

            bestMove = getBestMove(board, moves, searchDepth, moveMasks);

            nextDepthSearchTime = (System.nanoTime() - startTime) * 10;

            Arrays.sort(moves);
        }

        long stopTime = System.nanoTime();
        long totalTime = (stopTime - beginningTime) / 1000000L;
        double ratio = ((double) totalTime) / millis;

        System.out.println("gegebene Zeit: " + millis + "ms\n" +
                "gebrauchte Zeit: " + totalTime + "ms\n" +
                "Verhältnis: " + ratio + " (" + ratio * 100 + "%)\n" +
                "erreichte Suchtiefe: " + searchDepth);

        return bestMove;
    }
}
