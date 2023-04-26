package engine.move_generation;

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

    public long[] mainDiagonals;
    public long[] subDiagonals;
    public long[] horizontals;
    public long[] verticals;
    public MoveMasks() {
        // horizontalen und vertikalen errechnen
        for (int i = 0; i < 8; i++) {
            // für die Horizontalen 255 (0b11111111) um ein vierlfaches von 8 shiften
            horizontals[i] = (255L << i * 8);

            verticals[i] = (72340172838076673L << i * 8);

        }
    }
}
