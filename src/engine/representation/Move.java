package engine.representation;

public class Move implements Comparable<Move>{
    private final int startFieldIndex, endFieldIndex;
    private final PieceType pieceType;
    public boolean isEnPassant = false, isCastling = false, isPromotionToQueen = false, isPromotionToRook = false, isPromotionToBishop = false, isPromotionToKnight = false, isPawnTwoForward = false;
    public long whitePieces, blackPieces, kings, queens, rooks, bishops, knights, pawns;
    public boolean hasWhiteKingMoved, hasBlackKingMoved, hasWhiteLongRookMoved, hasWhiteShortRookMoved, hasBlackLongRookMoved, hasBlackShortRookMoved;
    public int movesSinceLastPawnMoveOrCapture = 0;
    public int evaluation = 0;

    public Move(int startFieldIndex, int endFieldIndex, PieceType pieceType){
        this.startFieldIndex = startFieldIndex;
        this.endFieldIndex = endFieldIndex;
        this.pieceType = pieceType;
    }

    public int getStartFieldIndex() {
        return startFieldIndex;
    }

    public int getEndFieldIndex() {
        return endFieldIndex;
    }

    public PieceType getPieceType(){
        return pieceType;
    }

    private String indexToChessField(int index) {
        int rank = index % 8;
        int file = index / 8;
        String[] rows = {"1", "2", "3", "4", "5", "6", "7", "8"};
        String[] cols = {"a", "b", "c", "d", "e", "f", "g", "h"};

        return cols[rank] + rows[file];
    }

    @Override
    public String toString() {
        return indexToChessField(startFieldIndex) + "->" + indexToChessField(endFieldIndex);
    }

    @Override
    public int compareTo(Move other) {
        return -Integer.compare(evaluation, other.evaluation);
    }

}
