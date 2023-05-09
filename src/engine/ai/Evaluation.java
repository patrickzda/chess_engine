package engine.ai;

import engine.representation.Board;


import static engine.representation.Color.*;

public class Evaluation {

    static int PAWN_VALUE = 1;
    static int KNIGHT_VALUE = 3;
    static int BISHOP_VALUE = 3;
    static int ROOK_VALUE = 5;
    static int QUEEN_VALUE = 9;

    // Bewertet primitiv eine gegebene Schachstellung. Alle Figuren, die am Zug sind, werden mit ihren Wertigkeiten
    // multipliziert und die Ergebnisse aufsummiert. Davon wird dann die Wertigkeit des gegnerischen Teams abgezogen.
    public static int evaluate(Board board) {
        long ownBoard, enemyBoard;
        if (board.getTurn() == BLACK) {
            ownBoard = board.whitePieces;
            enemyBoard = board.blackPieces;
        }
        else {
            ownBoard = board.blackPieces;
            enemyBoard = board.whitePieces;
        }

        int value = (getSetBits(ownBoard & board.pawns) - getSetBits(enemyBoard & board.pawns)) * PAWN_VALUE;
        value = value + (getSetBits(ownBoard & board.knights) - getSetBits(enemyBoard & board.knights)) * KNIGHT_VALUE;
        value = value + (getSetBits(ownBoard & board.bishops) - getSetBits(enemyBoard & board.bishops)) * BISHOP_VALUE;
        value = value + (getSetBits(ownBoard & board.rooks) - getSetBits(enemyBoard & board.rooks)) * ROOK_VALUE;
        value = value + (getSetBits(ownBoard & board.queens) - getSetBits(enemyBoard & board.queens)) * QUEEN_VALUE;

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
