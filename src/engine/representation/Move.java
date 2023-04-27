package engine.representation;

public class Move {
    private final int startFieldIndex, endFieldIndex;
    private final Color color;
    public boolean isEnPassant = false, isCastling = false, isPromotionToQueen = false, isPromotionToRook = false, isPromotionToBishop = false, isPromotionToKnight = false, isPawnTwoForward = false;

    public int getStartFieldIndex() {
        return startFieldIndex;
    }

    public int getEndFieldIndex() {
        return endFieldIndex;
    }

    public Color getColor() {
        return color;
    }

    Move(int startFieldIndex, int endFieldIndex, Color color){
        this.startFieldIndex = startFieldIndex;
        this.endFieldIndex = endFieldIndex;
        this.color = color;
    }

}
