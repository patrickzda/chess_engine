package test;

import engine.move_generation.MoveMasks;
import engine.representation.Direction;
import org.junit.jupiter.api.Test;

import static engine.representation.Direction.*;
import static org.junit.jupiter.api.Assertions.*;

class MoveMasksTest {
//    @Test
//    public void MoveMasks() {
//        MoveMasks m = new MoveMasks();
//
//        int[] indizesHorizontal = {0, 5, 7};
//        int[] indizesVertical = {0, 3, 7};
//        int[] indizesDiagonal = {0, 4, 7, 9, 11, 15};
//        int[] indizesAntiDiagonal = {0, 6, 7, 9, 12, 15};
//
//        long[] resultsHorizontal = {255L, 280375465082880L, -72057594037927936L};
//        long[] resultsVertical = {72340172838076673L, 578721382704613384L, -9187201950435737472L};
//        long[] resultsDiagonal = {-9205322385119247871L, 577588855528488960L, 72057594037927936L, 128L, 8405024L, 36099303471055874L};
//        long[] resultsAntiDiagonal = {72624976668147840L, 258L, 1L, -9223372036854775808L, 1161999622361579520L, 145249953336295424L};
//
//        for (int i = 0; i < resultsHorizontal.length; i++) {
//            if (resultsHorizontal[i] != m.horizontals[indizesHorizontal[i]]) {
//                System.out.println("FEHLER bei den Horizontalen! Index: " + indizesHorizontal[i]);
//                System.out.println("Ergebnis: ");
//                MoveMasks.printBitBoard(m.horizontals[indizesHorizontal[i]]);
//                System.out.println("Erwartet: ");
//                MoveMasks.printBitBoard(resultsHorizontal[i]);
//            }
//            assertEquals(resultsHorizontal[i], m.horizontals[indizesHorizontal[i]]);
//        }
//
//        for (int i = 0; i < resultsVertical.length; i++) {
//            if (resultsVertical[i] != m.verticals[indizesVertical[i]]) {
//                System.out.println("FEHLER bei den Vertikalen! Index: " + indizesVertical[i]);
//                System.out.println("Ergebnis: ");
//                MoveMasks.printBitBoard(m.verticals[indizesVertical[i]]);
//                System.out.println("Erwartet: ");
//                MoveMasks.printBitBoard(resultsVertical[i]);
//            }
//            assertEquals(resultsVertical[i], m.verticals[indizesVertical[i]]);
//        }
//
//        for (int i = 0; i < resultsDiagonal.length; i++) {
//            if (resultsDiagonal[i] != m.diagonals[indizesDiagonal[i]]) {
//                System.out.println("FEHLER bei den Diagonalen! Index: " + indizesDiagonal[i]);
//                System.out.println("Ergebnis: ");
//                MoveMasks.printBitBoard(m.diagonals[indizesDiagonal[i]]);
//                System.out.println("Erwartet: ");
//                MoveMasks.printBitBoard(resultsDiagonal[i]);
//            }
//            assertEquals(resultsDiagonal[i], m.diagonals[indizesDiagonal[i]]);
//        }
//
//        for (int i = 0; i < resultsAntiDiagonal.length; i++) {
//            if (resultsAntiDiagonal[i] != m.antiDiagonals[indizesAntiDiagonal[i]]) {
//                System.out.println("FEHLER bei den Anti-Diagonalen! Index: " + indizesAntiDiagonal[i]);
//                System.out.println("Ergebnis: ");
//                MoveMasks.printBitBoard(m.antiDiagonals[indizesAntiDiagonal[i]]);
//                System.out.println("Erwartet: ");
//                MoveMasks.printBitBoard(resultsAntiDiagonal[i]);
//            }
//            assertEquals(resultsAntiDiagonal[i], m.antiDiagonals[indizesAntiDiagonal[i]]);
//        }
//    }
//
//    @Test
//    public void maskNorth() {
//        MoveMasks m = new MoveMasks();
//
//        int indizes[] = {0, 27, 63};
//        long results[] = {-2L, -268435456L, 0L};
//
//        for (int i = 0; i < results.length; i++) {
//            if (results[i] != m.maskNorth(indizes[i])) {
//                System.out.println("FEHLER maskNorth! Index: " + indizes[i]);
//                System.out.println("Ergebnis: ");
//                MoveMasks.printBitBoard(m.maskNorth(indizes[i]));
//                System.out.println("Erwartet: ");
//                MoveMasks.printBitBoard(results[i]);
//            }
//            assertEquals(results[i], m.maskNorth(indizes[i]));
//        }
//    }
//
//    @Test
//    public void maskEast() {
//        MoveMasks m = new MoveMasks();
//
//        int files[] = {0, 4, 7};
//        long results[] = {-72340172838076674L, -2242545357980376864L, 0L};
//
//        for (int i = 0; i < results.length; i++) {
//            if (results[i] != m.maskEast(files[i])) {
//                System.out.println("FEHLER maskNorth! Index: " + files[i]);
//                System.out.println("Ergebnis: ");
//                MoveMasks.printBitBoard(m.maskEast(files[i]));
//                System.out.println("Erwartet: ");
//                MoveMasks.printBitBoard(results[i]);
//            }
//            assertEquals(results[i], m.maskEast(files[i]));
//        }
//    }
//
//    @Test
//    public void maskSouth() {
//        MoveMasks m = new MoveMasks();
//
//        int indizes[] = {0, 41, 63};
//        long results[] = {0L, 2199023255551L, 9223372036854775807L};
//
//        for (int i = 0; i < results.length; i++) {
//            if (results[i] != m.maskSouth(indizes[i])) {
//                System.out.println("FEHLER maskNorth! Index: " + indizes[i]);
//                System.out.println("Ergebnis: ");
//                MoveMasks.printBitBoard(m.maskSouth(indizes[i]));
//                System.out.println("Erwartet: ");
//                MoveMasks.printBitBoard(results[i]);
//            }
//            assertEquals(results[i], m.maskSouth(indizes[i]));
//        }
//    }
//
//    @Test
//    public void maskWest() {
//        MoveMasks m = new MoveMasks();
//
//        int files[] = {0, 5, 7};
//        long results[] = {0L, 2242545357980376863L, 9187201950435737471L};
//
//        for (int i = 0; i < results.length; i++) {
//            if (results[i] != m.maskWest(files[i])) {
//                System.out.println("FEHLER maskNorth! Index: " + files[i]);
//                System.out.println("Ergebnis: ");
//                MoveMasks.printBitBoard(m.maskWest(files[i]));
//                System.out.println("Erwartet: ");
//                MoveMasks.printBitBoard(results[i]);
//            }
//            assertEquals(results[i], m.maskWest(files[i]));
//        }
//    }

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

//    @Test
//    public void allDirections() {
//        MoveMasks m = new MoveMasks();
//
//        int[] indizes = {   27, 42, 37, 14, // in der Mitte
//                            0, 7, 56, 63,   // alle 4 Ecken
//                            5, 39, 60, 8    // 4 Felder am Rand (nicht in der Ecke)
//                        };
//
//        long[] results = {  -8626317307358205367L, 1517426162373248132L, 2641485286422881314L, 4702396038313459680L,
//                            -9132982212281170946L, -9114576973767589761L, -143265226645487231L, 9205534180971414145L,
//                            2314886638996058335L, -8025202881049096056L, -1209123513620754416L, 4693051017133293059L
//                         };
//
//        for (int i = 0; i < results.length; i++) {
//            if (results[i] != m.allDirections(indizes[i])) {
//                System.out.println("FEHLER! Index: " + indizes[i]);
//                System.out.println("Ergebnis: ");
//                MoveMasks.printBitBoard(m.allDirections(indizes[i]));
//                System.out.println("Erwartet: ");
//                MoveMasks.printBitBoard((results[i]));
//            }
//            assertEquals(results[i], m.allDirections(indizes[i]));
//        }
//
//    }
}