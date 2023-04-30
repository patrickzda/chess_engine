import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.Board;
import engine.representation.Color;
import engine.representation.Move;
import engine.representation.PieceType;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Board b = new Board("8/8/8/8/3Q4/8/8/8", Color.WHITE);
        MoveMasks moveMasks = new MoveMasks();
        System.out.println();
        System.out.println(MoveGenerator.generateQueenMoves(b, moveMasks).length);

    }
}