package engine.representation;

public class TranspositionTableEntry {
    private final long zobristKey;
    private final Move bestMove;
    private final int depth, evaluation;

    TranspositionTableEntry(long zobristKey, Move bestMove, int depth, int evaluation){
        this.zobristKey = zobristKey;
        this.bestMove = bestMove;
        this.depth = depth;
        this.evaluation = evaluation;
    }

    public long getZobristKey() {
        return zobristKey;
    }

    public Move getBestMove() {
        return bestMove;
    }

    public int getDepth() {
        return depth;
    }

    public int getEvaluation() {
        return evaluation;
    }
}
