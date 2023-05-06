package engine.ai;

import engine.representation.Board;


import static engine.representation.Color.*;

public class Evaluation {
    /*
    Enthält unterschiedliche Methoden, um eine übergebene Stellung zu analysieren.
    Für den Anfang sollte eine Methode, die einfach nur den Materialunterschied zwischen den Spielenden berechnet, ausreichen?
     */

    static int PAWN_VALUE = 1;
    static int KNIGHT_VALUE = 3;
    static int BISHOP_VALUE = 3;
    static int ROOK_VALUE = 5;
    static int QUEEN_VALUE = 9;

    // Bewertet primitiv eine gegebene Schachstellung. Alle Figuren, die am Zug sind, werden mit ihren Wertigkeiten
    // multipliziert und die Ergebnisse aufsummiert. Davon wird dann die Wertigkeit des gegnerischen Teams abgezogen.
    public static int evaluate(Board b) {
        long ownBoard, enemyBoard;
        if (b.getTurn() == WHITE) {
            ownBoard = b.whitePieces;
            enemyBoard = b.blackPieces;
        }
        else {
            ownBoard = b.blackPieces;
            enemyBoard = b.whitePieces;
        }

        int value = (getSetBits(ownBoard & b.pawns) - getSetBits(enemyBoard & b.pawns)) * PAWN_VALUE;
        value = value + (getSetBits(ownBoard & b.knights) - getSetBits(enemyBoard & b.knights)) * KNIGHT_VALUE;
        value = value + (getSetBits(ownBoard & b.bishops) - getSetBits(enemyBoard & b.bishops)) * BISHOP_VALUE;
        value = value + (getSetBits(ownBoard & b.rooks) - getSetBits(enemyBoard & b.rooks)) * ROOK_VALUE;
        value = value + (getSetBits(ownBoard & b.queens) - getSetBits(enemyBoard & b.queens)) * QUEEN_VALUE;

        return value;
    }

    public static int getSetBits(long l){
        int result = 0;
        for(int i = 0; i < 64; i++){
            if((l & (1L << i)) != 0){
                result++;
            }
        }
        return result;
    }
}
