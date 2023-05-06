import engine.ai.Evaluation;
import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.Board;
import engine.representation.Color;
import engine.representation.Move;
import engine.representation.PieceType;
import performance.MoveGeneratorPerformance;

import java.util.ArrayList;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        //String fens[] = new String[]{"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq","4r1k1/1bqr1pbp/p2p2p1/4p1B1/2p1P3/PnP2N1P/BP2QPP1/3RR1K1 w KQkq","8/6k1/5bP1/4p2p/3pP2P/1b1qBK2/p1r5/6R1 b KQkq"};
        //MoveGeneratorPerformance moveGeneratorPerformance = new MoveGeneratorPerformance();
        //moveGeneratorPerformance.performanceMovegenratorOnBoards(fens,100000);

        int MAX_MOVES = 1000;

        Board board = new Board();
        MoveMasks masks = new MoveMasks();
        int bestScore = Integer.MIN_VALUE;
        int counter = 0;
        int evaluation;
        Random random = new Random();

        ArrayList<Move> bestMoves = new ArrayList<>();

        Move[] legalMoves = MoveGenerator.generateLegalMoves(board, masks);
        while(legalMoves.length > 0 && !board.isGameWon(masks) && counter < MAX_MOVES){

            for (int i = 0; i < legalMoves.length; i++) {
                board.doMove(legalMoves[i]);
                evaluation = -Evaluation.evaluate(board);
                if (evaluation > bestScore) {
                    bestMoves = new ArrayList<>();
                    bestMoves.add(legalMoves[i]);
                    bestScore = evaluation;
                }
                else if (evaluation == bestScore) {
                    bestMoves.add(legalMoves[i]);
                }
                board.undoLastMove();
            }
            board.doMove(bestMoves.get(random.nextInt(bestMoves.size())));

            System.out.println(board.toFENString());

            legalMoves = MoveGenerator.generateLegalMoves(board, masks);
            counter++;
            bestScore = Integer.MIN_VALUE;
        }
        if (board.isGameWon(masks)) {
            if (board.getTurn() ==Color.WHITE) {
                System.out.print("BLACK ");
            }
            else System.out.print("WHITE ");

            System.out.println("hat nach " + counter + " Zügen gewonnen!");
        }
        else {
            if (counter >= MAX_MOVES) {
                System.out.println("Das dauert mir hier alles zu lange...");
            }
            else {
                System.out.println("Unentschieden nach " + counter + " Zügen");
            }
        }
    }
}