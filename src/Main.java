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

        Board board = new Board();
        MoveMasks masks = new MoveMasks();
        Random random = new Random();
        int counter = 0;

        Move[] legalMoves = MoveGenerator.generateLegalMoves(board, masks);
        while(legalMoves.length > 0 && !board.isKingOfTheHill() && counter < 40){
            board.doMove(legalMoves[random.nextInt(legalMoves.length)]);
            System.out.println(board.toFENString());
            legalMoves = MoveGenerator.generateLegalMoves(board, masks);
            counter++;
        }
    }
}