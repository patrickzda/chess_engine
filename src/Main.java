import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.Board;
import engine.representation.Color;
import engine.representation.Move;
import engine.representation.PieceType;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Board b = new Board("r1bqk1nr/pppp1ppp/2n5/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq");
        Move m = new Move(7, 5, PieceType.ROOK);
        b.doMove(m);

        Move[] moves = MoveGenerator.generateKnightMoves(b);
        for(int i = 0; i < moves.length; i++){
            b.doMove(moves[i]);
            System.out.println(b.toFENString());
            b.undoLastMove();
        }
    }
}