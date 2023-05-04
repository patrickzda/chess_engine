import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.Board;
import engine.representation.Color;
import engine.representation.Move;
import engine.representation.PieceType;
import performance.MoveGeneratorPerformance;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
       /* Board b = new Board("7k/7q/5pqp/6p1/NP3P1P/1KP3P1/2B5/8 b - - 0 1");

        Move[] moves = MoveGenerator.generateLegalMoves(b, new MoveMasks());

        System.out.println(moves.length);

        for(int i = 0; i < moves.length; i++){
            b.doMove(moves[i]);
            System.out.println(b.toFENString());
            b.undoLastMove();
        }*/
        String fens[] = new String[]{"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w -","4r1k1/1bqr1pbp/p2p2p1/4p1B1/2p1P3/PnP2N1P/BP2QPP1/3RR1K1 w -","8/6k1/5bP1/4p2p/3pP2P/1b1qBK2/p1r5/6R1 b -"};
        MoveGeneratorPerformance moveGeneratorPerformance = new MoveGeneratorPerformance();
        moveGeneratorPerformance.performanceMovegenratorOnBoards(fens,100000);
    }
}