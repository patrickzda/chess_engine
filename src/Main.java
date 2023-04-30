import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.Board;
import engine.representation.Color;
import engine.representation.Move;
import engine.representation.PieceType;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Board b = new Board("8/1p5p/2P3p1/8/4B3/8/2p3P1/1p5p", Color.WHITE);
        MoveMasks moveMasks = new MoveMasks();
        System.out.println();
        System.out.println(MoveGenerator.generateBishopMoves(b, moveMasks).length);

    }
}