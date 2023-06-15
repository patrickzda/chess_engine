package engine.tools;

import engine.representation.EvaluationType;
import engine.representation.Move;

public class TranspositionTableEntry {
    private final long zobristKey;
    private final Move bestMove;
    private final int depth, evaluation;
    private final EvaluationType type;

    TranspositionTableEntry(long zobristKey, Move bestMove, int depth, int evaluation, EvaluationType type){
        this.zobristKey = zobristKey;
        this.bestMove = bestMove;
        this.depth = depth;
        this.evaluation = evaluation;
        this.type = type;
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

    public EvaluationType getType() {
        return type;
    }
}
