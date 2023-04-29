package engine.move_generation;

import engine.representation.Direction;

import static engine.representation.Direction.*;

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

    public long[] diagonals = new long[16];
    public long[] antiDiagonals = new long[16];
    public long[] horizontals = new long[8];
    public long[] verticals = new long[8];

    private long DIAGONAL = -9205322385119247871L;
    private long ANTI_DIAGONAL = 72624976668147840L;
    private long MSB_ONE = -9223372036854775808L;
    private long HORIZONTAL = 255L;
    private long VERTICAL = 72340172838076673L;
    private long ZEROS = 0L;
    public MoveMasks() {
        // horizontalen und vertikalen errechnen
        for (int i = 0; i < 8; i++) {
            // für die Horizontalen 255 (0b11111111) um ein vierlfaches von 8 shiften
            horizontals[i] = (HORIZONTAL << i * 8);

            // für die vertikalen 72340172838076673L (einer vertikale reihe aus einsen) um 0-7 shiften
            verticals[i] = (VERTICAL << i);

            // für die Diagonalen wird einfach die haupt- bzw. anti-Diagonale geshiftet
            diagonals[i] = (DIAGONAL << (8 * i));
            antiDiagonals[i] = (ANTI_DIAGONAL >>> (8 * i));
        }

        // aus Gründen der Diagonal-Berechnung aus dem Index muss die 9. Diagonale (index 8) leer sein
        diagonals[8] = ZEROS;
        antiDiagonals[8] = ZEROS;

        for (int j = 9; j < 16; j++) {
            // Diagonalen auch in die andere Richtung shiften, da es insgesamt 15 diagonalen gibt
            // (16, wenn man den Index 8 mitzählt)
            diagonals[j] = (DIAGONAL >>> (8 * (8 - j)));
            antiDiagonals[j] = (ANTI_DIAGONAL << (8 * (8 - j)));
        }
    }

    private long maskNorth(int index) {
        // Bitmaske, die nur in nördlicher Richtung von dem Ausgangsindex einsen hat

        // ein Edge-Case muss abgefangen werden, wenn das Feld das MSB ist, sonst wird die Maske auf nur einsen
        // gesetzt und die Richtung wird nicht abgeschnitten.
        // TODO: evtl. bessere Lösung ohne if-Statement finden
        if (index == 63) {
            return 0L;
        }

        return MSB_ONE >> (62 - index);
    }

    private long maskEast(int file) {
        // Bitmaske, die nur in östlicher Richtung von dem Ausgangsindex einsen hat

        // ein Edge-Case muss abgefangen werden, wenn das Feld am östlichen Rand liegt, sonst wird das
        // Feld auf dem der Spielstein steht auch als mögliches Zielfeld mit ausgegeben
        // TODO: evtl. bessere Lösung ohne if-Statement finden
        if (file == 7) {
            return 0L;
        }

        long mask = MSB_ONE >> (6 - file);
        mask = mask | (mask >>> 8);
        mask = mask | (mask >>> 16);
        return mask | (mask >>> 32);
    }

    private long maskSouth(int index) {
        // Bitmaske, die nur in südlicher Richtung von dem Ausgangsindex einsen hat
        return ~(MSB_ONE >> (63 - index));
    }

    private long maskWest(int file) {
        // Bitmaske, die nur in westlicher Richtung von dem Ausgangsindex einsen hat
        long mask = MSB_ONE >> (7 - file);
        mask = mask | (mask >>> 8);
        mask = mask | (mask >>> 16);
        return ~(mask | (mask >>> 32));
    }

    public long rays(Direction dir, int index) {
        long mask, res;
        int rank, file;
        switch(dir) {
            case NORTH:
                file = index & 7;   // = index % 8
                mask = maskNorth(index);

                res = verticals[file] & mask;
                break;

            case NORTH_EAST:
                rank = index >> 3;  // = index / 8
                file = index & 7;   // = index % 8
                mask = maskNorth(index);

                res = diagonals[(rank - file) & 15] & mask;
                //              ^^^^^^^^^^^^^^^^^^
                // Berechnet die gesuchte Diagonale
                break;

            case EAST:
                rank = index >> 3;  // = index / 8
                file = index & 7;   // = index % 8
                mask = maskEast(file);

                res = horizontals[rank] & mask;
                break;

            case SOUTH_EAST:
                rank = index >> 3;  // = index / 8
                file = index & 7;   // = index % 8
                mask = maskSouth(index);

                res = antiDiagonals[(rank + file) ^ 7] & mask;
                //                  ^^^^^^^^^^^^^^^^^
                // Berechnet die gesuchte anti-Diagonale
                break;

            case SOUTH:
                file = index & 7;   // = index % 8
                mask = maskSouth(index);

                res = verticals[file] & mask;
                break;

            case SOUTH_WEST:
                rank = index >> 3;  // = index / 8
                file = index & 7;   // = index % 8
                mask = maskSouth(index);

                res = diagonals[(rank - file) & 15] & mask;
                //              ^^^^^^^^^^^^^^^^^^
                // Berechnet die gesuchte Diagonale
                break;

            case WEST:
                rank = index >> 3;  // = index / 8
                file = index & 7;   // = index % 8

                mask = maskWest(file);

                res = horizontals[rank] & mask;
                break;

            case NORTH_WEST:
                rank = index >> 3;  // = index / 8
                file = index & 7;   // = index % 8

                mask = maskNorth(index);

                res = antiDiagonals[(rank + file) ^ 7] & mask;
                //                  ^^^^^^^^^^^^^^^^^
                // Berechnet die gesuchte anti-Diagonale
                break;

            default:
                // dieses Statement dürfte nie erreicht werden! Aus komplettheitsgründen wird in dem Fall
                // eine Fehlermeldung ausgegeben und eine leere Bitmaske zurückgegeben
                System.out.println("irgendwas ist gewaltig schief gelaufen...");
                res = ZEROS;
                break;

        }
        return res;
    }

    public long allDirections(int index) {
        // verodert alle Richtungen von einem Index
        return rays(NORTH, index) |
               rays(NORTH_EAST, index) |
               rays(EAST, index) |
               rays(SOUTH_EAST, index) |
               rays(SOUTH, index) |
               rays(SOUTH_WEST, index) |
               rays(WEST, index) |
               rays(NORTH_WEST, index);
    }

    public static void printBitBoard(long bitBoard) {
        // für Debugging-Zwecke. Gibt das Bitboard in der Reihenfolge von einem Schachbrett aus
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
