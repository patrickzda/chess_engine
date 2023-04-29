package test;

import engine.move_generation.MoveMasks;
import engine.representation.Direction;
import org.junit.jupiter.api.Test;

import static engine.representation.Direction.*;
import static org.junit.jupiter.api.Assertions.*;

class MoveMasksTest {
    @Test
    public void MoveMasks() {
        System.out.println("Starte Test für MoveMasks");
        MoveMasks m = new MoveMasks();

        for (int i = 0; i < 8; i++) {
            System.out.println("Horizontale " + i + ":");
            long h = m.horizontals[i];

            MoveMasks.printBitBoard(h);

            System.out.println("Vertikale " + i + ":");
            long v = m.verticals[i];
            MoveMasks.printBitBoard(v);
        }

        for (int i = 0; i < 16; i++) {
            System.out.println("Hauptdiagonale " + i + ":");
            long md = m.diagonals[i];

            MoveMasks.printBitBoard(md);

            System.out.println("Nebendiagonale " + i + ":");
            long sd = m.antiDiagonals[i];
            MoveMasks.printBitBoard(sd);
        }

    }

    @Test
    public void rays() {
        MoveMasks m = new MoveMasks();
        Direction[] directions = {  NORTH, NORTH, NORTH, NORTH,
                                    NORTH_EAST, NORTH_EAST, NORTH_EAST, NORTH_EAST,
                                    EAST, EAST, EAST, EAST,
                                    SOUTH_EAST, SOUTH_EAST, SOUTH_EAST, SOUTH_EAST,
                                    SOUTH, SOUTH, SOUTH, SOUTH,
                                    SOUTH_WEST, SOUTH_WEST, SOUTH_WEST, SOUTH_WEST,
                                    WEST, WEST, WEST, WEST,
                                    NORTH_WEST, NORTH_WEST, NORTH_WEST, NORTH_WEST
                                 };
        int[] indizes = {   27, 3, 63, 59,
                            0, 35, 58, 63,
                            16, 43, 39, 7,
                            56, 10, 39, 7,
                            58, 37, 1, 7,
                            63, 34, 0, 5,
                            39, 50, 56, 24,
                            7, 19, 56, 63
                        };
        long[] results = {  578721382569869312L, 578721382704613376L, 0L, 0L,
                            -9205322385119247872L, 4620710809868173312L, 0L, 0L,
                            16646144L, 263882790666240L, 0L, 0L,
                            567382630219904L, 8L, 0L, 0L,
                            1130315200594948L, 538976288L, 0L, 0L,
                            18049651735527937L, 33619968L, 0L, 0L,
                            545460846592L, 844424930131968L, 0L, 0L,
                            72624976668147712L, 1108168671232L, 0L, 0L

                         };

        for (int i = 0; i < results.length; i++) {
            // Fehler einmal ausprinten, damit schnell zu sehen ist, wo der Fehler liegt und was falsch ist
            if (results[i] != m.rays(directions[i], indizes[i])) {
                System.out.println("FEHLER! Index: " + indizes[i] + "    Richtung: " + directions[i]);
                System.out.println("Ergebnis: ");
                MoveMasks.printBitBoard(m.rays(directions[i], indizes[i]));
                System.out.println("Erwartet: ");
                MoveMasks.printBitBoard((results[i]));
            }
            assertEquals(results[i], m.rays(directions[i], indizes[i]));

        }
    }

    @Test
    public void allDirections() {
        MoveMasks m = new MoveMasks();
        int[] indizes = {27, 0, 7, 56, 63, 5, 39, 60, 8};
        int index;

        for (int i = 0; i < indizes.length; i++) {
            index = indizes[i];
            System.out.println("index = " + index);
            MoveMasks.printBitBoard(m.allDirections(index));
            System.out.println();
        }

    }
}