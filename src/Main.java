import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.Board;
import engine.representation.Color;
import engine.representation.Move;
import engine.representation.PieceType;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Board b = new Board("6R1/p2r4/1b1ppp1p/p4q1P/3n1k2/2K5/7P/5n2 b - - 0 1");

        Move[] moves = MoveGenerator.generateLegalMoves(b, new MoveMasks());

        System.out.println(moves.length);

        for(int i = 0; i < moves.length; i++){
            b.doMove(moves[i]);
            System.out.println(b.toFENString());
            b.undoLastMove();
        }
    }
}