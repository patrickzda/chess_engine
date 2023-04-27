package test;

import engine.move_generation.MoveMasks;
import org.junit.jupiter.api.Test;

import static engine.representation.Direction.*;
import static org.junit.jupiter.api.Assertions.*;

class MoveMasksTest {
    @Test
    public void MoveMasks() {
        System.out.println("Starte Test für MoveMasks");
        MoveMasks m = new MoveMasks();

//        for (int i = 0; i < 8; i++) {
//            System.out.println("Horizontale " + i + ":");
//            long h = m.horizontals[i];
//
//            MoveMasks.printBitBoard(h);
//
//            System.out.println("Vertikale " + i + ":");
//            long v = m.verticals[i];
//            MoveMasks.printBitBoard(v);
//        }

        for (int i = 0; i < 15; i++) {
//            System.out.println("Hauptdiagonale " + i + ":");
//            long md = m.diagonals[i];
//
//            MoveMasks.printBitBoard(md);

            System.out.println("Nebendiagonale " + i + ":");
            long sd = m.antiDiagonals[i];
            MoveMasks.printBitBoard(sd);
        }

    }

    @Test
    public void rays() {
        MoveMasks m = new MoveMasks();
        MoveMasks.printBitBoard(m.rays(NORTH, 27));
        System.out.println();
        MoveMasks.printBitBoard(m.rays(SOUTH, 27));
        System.out.println();
        MoveMasks.printBitBoard(m.rays(NORTH_EAST, 27));
        System.out.println();
        MoveMasks.printBitBoard(m.rays(SOUTH_EAST, 27));
        System.out.println();
        MoveMasks.printBitBoard(m.rays(NORTH_WEST, 27));
        System.out.println();
        MoveMasks.printBitBoard(m.rays(SOUTH_WEST, 27));
        System.out.println();
    }
}