package engine.ai;

import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.*;
import java.util.Arrays;
import static java.lang.Math.max;
import static java.lang.Math.subtractExact;

public class AlphaBeta {

    static TranspositionTable transpositionTable = new TranspositionTable();

    private static int alphaBetaMax(Board board, int alpha, int beta, int depth, MoveMasks moveMasks) {
        TranspositionTableEntry entry = transpositionTable.getEntry(board, depth);
        if (entry != null) {
            return entry.getEvaluation();
        }

        Move[] moves = MoveGenerator.generateLegalMoves(board, moveMasks);
        Evaluation.sortMoves(transpositionTable, board, moves);

        if (depth == 0 || board.isGameLost(moveMasks, moves.length) || moves.length == 0) {
            int finalEvaluation = Evaluation.evaluate(board, moveMasks);
            transpositionTable.addEntry(board, new Move(0, 0, PieceType.PAWN), depth, finalEvaluation);
            return finalEvaluation;
        }

        Move bestMove = moves[0];
        int score;

        for (int i = 0; i < moves.length; i++) {
            board.doMove(moves[i]);
            score = alphaBetaMin(board, alpha, beta, depth - 1, moveMasks);
            board.undoLastMove();

            if (score >= beta) {
                transpositionTable.addEntry(board, moves[i], depth, beta);
                return beta;
            }

            if (score > alpha) {
                bestMove = moves[i];
                alpha = score;
            }
        }
        transpositionTable.addEntry(board, bestMove, depth, alpha);
        return alpha;
    }

    private static int alphaBetaMin(Board board, int alpha, int beta, int depth, MoveMasks moveMasks) {
        TranspositionTableEntry entry = transpositionTable.getEntry(board, depth);
        if (entry != null) {
            return -entry.getEvaluation();
        }

        Move[] moves = MoveGenerator.generateLegalMoves(board, moveMasks);
        Evaluation.sortMoves(transpositionTable, board, moves);

        if (depth == 0 || board.isGameLost(moveMasks, moves.length) || moves.length == 0) {
            int finalEvaluation = -Evaluation.evaluate(board, moveMasks);
            transpositionTable.addEntry(board, new Move(0, 0, PieceType.PAWN), depth, -finalEvaluation);
            return finalEvaluation;
        }

        Move bestEnemyMove = moves[0];
        int score;

        for (int i = 0; i < moves.length; i++) {
            board.doMove(moves[i]);
            score = alphaBetaMax(board, alpha, beta, depth - 1, moveMasks);
            board.undoLastMove();

            if (score <= alpha) {
                transpositionTable.addEntry(board, moves[i], depth, -alpha);
                return alpha;
            }

            if (score < beta) {
                bestEnemyMove = moves[i];
                beta = score;
            }
        }
        transpositionTable.addEntry(board, bestEnemyMove, depth, -beta);
        return beta;
    }

    public static Move getBestMove(Board board, Move[] moves, int depth, MoveMasks moveMasks) {
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
            moves[i].evaluation = score;
            board.undoLastMove();

            if (score > alpha) {
                alpha = score;
                bestMove = moves[i];
            }
        }

        //HIER MUSS NOCHMAL GESCHAUT WERDEN: ANPASSEN, JE NACHDEM WAS ANALYSIERT WERDEN SOLL
        //transpositionTable.addEntry(board, bestMove, depth, bestMove.evaluation);
        transpositionTable.clear();
        return bestMove;
    }

    public static Move getBestMoveTimed(Board board, MoveMasks moveMasks, int millis) {
        long startTime = System.nanoTime();
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

        return bestMove;
    }
}
