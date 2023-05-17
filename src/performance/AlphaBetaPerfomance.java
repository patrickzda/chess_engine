package performance;

import engine.ai.Evaluation;
import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.Board;
import engine.representation.Color;
import engine.representation.Move;

public class AlphaBetaPerfomance {
        /*
    Hier kommt, wie in der Vorlesung beschrieben, die Alpha-Beta KI rein.
     */


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

                if (score >= beta) {    // cutoff: Dieser Move ist schlecht und es kann nicht besser werden
                    break;
                }

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

                if (score <= alpha) {    // cutoff: Dieser Move ist schlecht und es kann nicht besser werden
                    break;
                }

                if (score < beta) {
                    beta = score;
                    bestMove = moves[i];    // in der ersten Suchtiefe den besten Move merken
                }
            }
        }

        return bestMove;
    }
}
