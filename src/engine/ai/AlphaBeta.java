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

        if (depth == 0 || board.isGameLost(moveMasks, moves.length) || moves.length == 0) {
            return Evaluation.evaluate(board, moveMasks);
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

        if (depth == 0 || board.isGameLost(moveMasks, moves.length) || moves.length == 0) {
            return -Evaluation.evaluate(board, moveMasks);
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

    // gibt den besten Move anhand der AlphaBeta Suche mit der Bewertungsfunktion zurück
    // ACHTUNG: depth muss >= 1 sein
    public static Move getBestMove(Board board, Move[] moves, int depth, MoveMasks moveMasks) {
        if (depth < 1) {
            throw new IllegalArgumentException("Suchtiefe muss mindestes 1 sein!!!\n" + "Alpha-Beta Suche wird nicht funktionieren!");
        }

        TranspositionTableEntry entry = transpositionTable.getEntry(board, depth);
        if (entry != null) {
            return entry.getBestMove();
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
        transpositionTable.addEntry(board, bestMove, depth, bestMove.evaluation);
        return bestMove;
    }

    public static Move getBestMoveTimed(Board board, MoveMasks moveMasks, int millis) {
        long startTime = System.nanoTime();
        long finishTime = startTime + (millis * 1000000L);
        long nextDepthSearchTime = 0;
        long lastFinishTime = System.nanoTime();
        int searchDepth = 0;
        Move bestMove = new Move(0, 0, PieceType.PAWN);
        //Moves generieren

        Move[] moves = MoveGenerator.generateLegalMoves(board, moveMasks);

        while (System.nanoTime() + nextDepthSearchTime < finishTime) {
            searchDepth++;
            bestMove = getBestMove(board, moves, searchDepth, moveMasks);      //+ Evaluationen aller Moves global speichern

            nextDepthSearchTime = (lastFinishTime - startTime) * 30;

            lastFinishTime = System.nanoTime();
            //Moves auf Basis der globalen Evaluation sortieren
            Arrays.sort(moves);
        }

        //Globale Evaluationen clearen

        long stopTime = System.nanoTime();
        long totalTime = (stopTime - startTime) / 1000000L;
        double ratio = ((double) totalTime) / millis;

        System.out.println("gegebene Zeit: " + millis + "ms\n" +
                           "gebrauchtre Zeit: " + totalTime + "ms\n" +
                           "Verhältnis: " + ratio + " (" + ratio * 100 + "%)\n" +
                           "erreichte Suchtiefe: " + searchDepth);
        return bestMove;
    }
}
