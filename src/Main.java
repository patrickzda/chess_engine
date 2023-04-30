import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.Board;
import engine.representation.Color;
import engine.representation.Move;
import engine.representation.PieceType;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Board b = new Board("4k3/8/8/r7/8/8/8/R3K3", Color.BLACK);
        MoveMasks moveMasks = new MoveMasks();
        MoveGenerator.generateRookMoves(b, moveMasks);
    }
}