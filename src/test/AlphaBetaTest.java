package test;

import engine.ai.AlphaBeta;
import engine.ai.Evaluation;
import engine.ai.Negamax;
import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.Board;
import engine.representation.Move;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class AlphaBetaTest {

    //Nochmal schauen wegen folgender Stellung: "4r1k1/1bqr1pbp/p2p2p1/4p1B1/2p1P3/PnP2N1P/BP2QPP1/3RR1K1 w Qq - 0 1"

    @Test
    void getBestMove() {
        MoveMasks masks = new MoveMasks();
        String[] fens = new String[]{"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", "2k5/6q1/3P1P2/4N3/8/1K6/8/8 w - - 0 1", "6k1/r4ppp/r7/1b6/8/8/4QPPP/4R1K1 w - - 0 1", "r2qk2r/p1p1p1P1/1pn4b/1N1Pb3/1PB1N1nP/8/1B1PQPp1/R3K2R b Qkq - 0 1", "r1bq4/pp1p1k1p/2p2p1p/2b5/3Nr1Q1/2N1P3/PPPK1PPP/3R1B1R w - - 0 1", "3r1rk1/p1p1qp1p/1p2b1p1/6n1/R1PNp3/2QP2P1/3B1P1P/5RK1 w - - 0 1", "3r4/7p/2p2kp1/2P2p2/3P4/2K3P1/8/5R2 b - - 0 1", "5rk1/1p4pp/2R1p3/p5Q1/P4P2/6qr/2n3PP/5RK1 w - - 0 1", "r1b2rk1/4qpp1/4p2R/p2pP3/2pP2QP/4P1P1/PqB4K/8 w - - 0 1"};
        int depth = 4;

        //AlphaBeta alphaBeta = new AlphaBeta();
        for(int i = 0; i < fens.length; i++){
            Board b = new Board(fens[i]);
            Move basicAlphaBetaMove = bestMove(b, MoveGenerator.generateLegalMoves(b, masks), depth, masks);
            //System.out.println("---");
            Move newAlphaBetaMove = Negamax.getBestMove(b, depth, masks);
            Negamax.clearTable();
            //alphaBeta.printTTStats();
            //alphaBeta.clearTable();

            if(basicAlphaBetaMove != null){
                System.out.println("TESTING: " + fens[i]);
                System.out.println(basicAlphaBetaMove.toString() + " | " + newAlphaBetaMove.toString());
                System.out.println(basicAlphaBetaMove.evaluation + " | " + newAlphaBetaMove.evaluation);
                assertEquals(basicAlphaBetaMove.toString(), newAlphaBetaMove.toString());
            }
        }
    }

    public static int alphaBetaMax(Board board, int alpha, int beta, int depth, MoveMasks moveMasks) {

        Move[] moves = MoveGenerator.generateLegalMoves(board, moveMasks);

        if (depth == 0 || board.isGameLost(moveMasks, moves.length) || moves.length == 0) {
            return Evaluation.evaluate(board, moveMasks);
        }

        int score;

        for (int i = 0; i < moves.length; i++) {
            board.doMove(moves[i]);
            score = alphaBetaMin(board, alpha, beta, depth - 1, moveMasks);
            board.undoLastMove();

            if (score >= beta) {
                return beta;
            }

            if (score > alpha) {
                alpha = score;
            }
        }

        return alpha;
    }

    public static int alphaBetaMin(Board board, int alpha, int beta, int depth, MoveMasks moveMasks) {
        Move[] moves = MoveGenerator.generateLegalMoves(board, moveMasks);

        if (depth == 0 || board.isGameLost(moveMasks, moves.length) || moves.length == 0) {
            return -Evaluation.evaluate(board, moveMasks);
        }

        int score;

        for (int i = 0; i < moves.length; i++) {
            board.doMove(moves[i]);
            score = alphaBetaMax(board, alpha, beta, depth - 1, moveMasks);
            board.undoLastMove();

            if (score <= alpha) {
                return alpha;
            }

            if (score < beta) {
                beta = score;
            }
        }

        return beta;
    }

    public static Move bestMove(Board board, Move[] moves, int depth, MoveMasks moveMasks) {

        if (depth < 1) {
            throw new IllegalArgumentException("Suchtiefe muss mindestens 1 sein!");
        }

        if(moves.length == 0){
            return null;
        }


        int score, bestScore = Integer.MIN_VALUE;
        Move bestMove = moves[0];

        for (int i = 0; i < moves.length; i++) {
            board.doMove(moves[i]);
            score = alphaBetaMin(board, Integer.MIN_VALUE, Integer.MAX_VALUE, depth - 1, moveMasks); // Aufruf von AlphaBeta ohne sich die Moves zu merken
            board.undoLastMove();
            moves[i].evaluation = score;

            //System.out.printf(moves[i] + ": " + score + ", ");

            if (score > bestScore) {
                bestScore = score;
                bestMove = moves[i];    // in der ersten Suchtiefe den besten Move merken
            }
        }
        //System.out.println();
        bestMove.evaluation = bestScore;
        return bestMove;
    }
}