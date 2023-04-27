package engine.move_generation;

import engine.representation.Direction;

public class MoveMasks {
    /*
    Wir benötigen jeweils Zugmasken für alle möglichen Richtungen, in welche die Läufer, die Türme und die Damen ziehen können.
    Dabei soll jede Zugmaske nur für eine Richtung sein. (Also jeweils 15 einzelne Masken pro Diagonalen, 8 für die Vertikalen und 8 für die Horizontalen.)

    Beispiele für den Läufer:

    00000000    00000000    00000000    00000000    00000000
    00000000    00000000    00000000    00000000    00000000
    00000000    00000000    00000000    00000000    00000000
    00000000    00000000    00000000    00000000    10000000
    00000000    00000000    00000000    10000000    01000000
    00000000    00000000    10000000    01000000    00100000
    00000000    10000000    01000000    00100000    00010000
    10000000    01000000    00100000    00010000    00000100        usw.

    Dabei ist das LSB unten links (A1) und das MSB oben rechts (H8).
    Die einzelnen Bitmasken werden als long abgespeichert und müssen in irgendeine schlaue Datenstruktur eingefügt werden, sodass man schnell auf sie zugreifen kann.
     */

    public long[] diagonals = new long[15];
    public long[] antiDiagonals = new long[15];
    public long[] horizontals = new long[8];
    public long[] verticals = new long[8];
    public MoveMasks() {
        // horizontalen und vertikalen errechnen
        for (int i = 0; i < 8; i++) {
            // für die Horizontalen 255 (0b11111111) um ein vierlfaches von 8 shiften
            horizontals[i] = (255L << i * 8);

            // für die vertikalen 72340172838076673L (einer vertikale reihe aus einsen) um 0-7 shiften
            verticals[i] = (72340172838076673L << i);


            diagonals[i] = (72624976668147840L << (8 * i) + 8);
            antiDiagonals[i] = (-9205322385119247871L << (8 * i) + 8);
        }

        for (int j = 8; j < 15; j++) {
            diagonals[j] = (72624976668147840L >>> (8 * (7 - j)));
            antiDiagonals[j] = (-9205322385119247871L >>> (8 * (7 - j)));
        }
    }

    public long rays(Direction dir, int index) {
        long temp;
        switch(dir) {
            case NORTH:
                System.out.println("NORTH, " + index);
                temp = -9223372036854775808L >> (62 - index);

                return verticals[index % 8] & temp;

            case NORTH_EAST:
                System.out.println("NORTH_EAST, " + index);
                temp = -9223372036854775808L >> (62 - index);
                int rank = index / 8;
                int file = index % 8;
                return antiDiagonals[(rank - file) & 15];

            case EAST:
                System.out.println("EAST, " + index);
                break;
            case SOUTH_EAST:
                System.out.println("SOUTH_EAST, " + index);

                temp = ~(-9223372036854775808L >> (63 - index));
                return diagonals[index % 8] & temp;

            case SOUTH:
                System.out.println("SOUTH, " + index);

                temp = ~(-9223372036854775808L >> (63 - index));
                return verticals[index % 8] & temp;

            case SOUTH_WEST:
                System.out.println("SOUTH_WEST, " + index);

                temp = ~(-9223372036854775808L >> (63 - index));
                return antiDiagonals[index % 8] & temp;

            case WEST:
                System.out.println("WEST, " + index);
                break;

            case NORTH_WEST:
                System.out.println("NORTH_WEST, " + index);

                temp = -9223372036854775808L >> (62 - index);
                return diagonals[index % 8] & temp;

            default:
                System.out.println("irgendwas ist gewaltig schief gelaufen...");
                break;

        }










        return 0L;
    }

    public static void printBitBoard(long bitBoard) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int i = (7 - x) * 8 + y;
                if ((bitBoard & (1L << i)) == 0) {
                    System.out.print("0");
                }
                else {
                    System.out.print("1");
                }
            }
            System.out.println("");
        }
    }
}
