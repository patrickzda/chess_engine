package engine.representation;

public class Move {
    private final int startFieldIndex, endFieldIndex;
    private final PieceType pieceType;
    public boolean isEnPassant = false, isCastling = false, isPromotionToQueen = false, isPromotionToRook = false, isPromotionToBishop = false, isPromotionToKnight = false, isPawnTwoForward = false;
    public long whitePieces, blackPieces, kings, queens, rooks, bishops, knights, pawns;

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

    @Override
    public String toString() {
        return startFieldIndex + " -> " + endFieldIndex;
    }
}
