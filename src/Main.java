import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.Board;
import engine.representation.Color;
import engine.representation.Move;
import engine.representation.PieceType;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Board b = new Board("r3k2r/pppp1ppp/1bnq1nb1/4p3/2B1P3/5N2/PPP3PP/2KR3R", Color.BLACK);

        Move[] moves = MoveGenerator.generateKingMoves(b);
        for(int i = 0; i < moves.length; i++){
            b.doMove(moves[i]);
            System.out.println(b.toFENString());
            b.undoLastMove();
        }
    }
}