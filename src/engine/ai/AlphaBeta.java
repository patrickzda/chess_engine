package engine.ai;

import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.*;

import static java.lang.Math.max;
import static java.lang.Math.subtractExact;

public class AlphaBeta {

    private static int alphaBetaMax(Board board, int alpha, int beta, int depth, MoveMasks moveMasks) {
        Move[] moves = MoveGenerator.generateLegalMoves(board, moveMasks);

        if (depth == 0 || board.isGameWon(moveMasks, moves.length) || moves.length == 0) {
            return Evaluation.evaluate(board, moveMasks);
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
        return alpha;
    }

    private static int alphaBetaMin(Board board, int alpha, int beta, int depth, MoveMasks moveMasks) {
        Move[] moves = MoveGenerator.generateLegalMoves(board, moveMasks);

        if (depth == 0 || board.isGameWon(moveMasks, moves.length) || moves.length == 0) {
            return -Evaluation.evaluate(board, moveMasks);
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
            score = alphaBetaMin(board, alpha, beta, depth - 1, moveMasks); // Aufruf von AlphaBeta ohne sich die Moves zu merken
            board.undoLastMove();

            if (score > alpha) {
                alpha = score;
                bestMove = moves[i];
            }
        }

        return bestMove;
    }

    public static Move getBestMoveTimed(Board board, MoveMasks moveMasks, int millis) {
        long startTime = System.nanoTime();
        long finishTime = startTime + (millis * 1000000L);
        int searchDepth = 0;
        Move bestMove = new Move(0, 0, PieceType.PAWN);

        while (System.nanoTime() < finishTime) {
            searchDepth++;
            bestMove = getBestMove(board, searchDepth, moveMasks);
        }
        return bestMove;
    }
}
