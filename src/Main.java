import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.Board;
import engine.representation.Color;
import engine.representation.Move;
import engine.representation.PieceType;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Board b = new Board("2P1k3/2q5/8/2r1B3/8/2P5/2P5/R3K3", Color.BLACK);
        MoveMasks moveMasks = new MoveMasks();
        System.out.println(MoveGenerator.generateRookMoves(b, moveMasks).length);

    }
}