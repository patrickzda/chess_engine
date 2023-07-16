package engine.tools;

import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.Board;
import engine.representation.Color;
import engine.representation.Move;
import engine.representation.PieceType;

public class SearchTreeNode {
    private static final double c = Math.sqrt(2);
    private final Move move;
    private final SearchTreeNode parent;
    private SearchTreeNode[] children;
    private double visits = 0;
    private int score = 0;

    private final Color color;

    private boolean explored = false;
    public SearchTreeNode(Move move, SearchTreeNode parent, Color color) {
        this.move = move;
        this.parent = parent;
        this.color = color;
    }

    public void visit() {
        visits++;
    }

    public void addScore(double score) {
        this.score += score;
        visits++;
        explored = true;
    }

    public double calculateUCB(int totalSimulations) {
        return (score / visits) + c * Math.sqrt(Math.log(totalSimulations)/visits);
    }

    public SearchTreeNode getParent() {
        return parent;
    }

    public Move getMove() {
        return move;
    }

    public int getScore() { return score; }
    public double getVisits() { return visits; }

    private void generateChildren(Board board) {
        Move[] moves = MoveGenerator.generateLegalMoves(board, new MoveMasks());
        children = new SearchTreeNode[moves.length];

        for (int i = 0; i < moves.length; i++) {
            children[i] = new SearchTreeNode(moves[i], this, color == Color.BLACK ? Color.WHITE : Color.BLACK);
        }
    }

    public SearchTreeNode getNextChild(Board board, int totalSimulations) {
        // wenn die Children noch nicht generiert wurden, tu das und returne den ersten
        if (children == null) {
            this.generateChildren(board);
            return children[0];
        }

        // dann checken, ob noch unerkundete Children existieren
        for (SearchTreeNode child : children) {
            if (!child.isExplored()) {
                return child;
            }
        }

        // Ansonsten den mit dem höchsten UCB Score nehmen
        int highestUCBIndex = 0;
        double highestUCB = 0;
        for (int i = 0; i < children.length; i++) {
            double UCB = children[i].calculateUCB(totalSimulations);
            if (UCB > highestUCB) {
                highestUCB = UCB;
                highestUCBIndex = i;
            }
        }

        board.doMove(children[highestUCBIndex].getMove());
        SearchTreeNode result = children[highestUCBIndex].getNextChild(board, totalSimulations);
        board.undoLastMove();

        return result;

    }

    public Move getMoveWithBestRatio() {
        double bestRatio = -Double.MAX_VALUE;
        Move bestMove = new Move(0, 0, PieceType.PAWN);

        for (SearchTreeNode child: children) {
            if (child.getScore() / child.getVisits() > bestRatio) {
                bestRatio = child.getScore() / child.getVisits();
                bestMove = child.getMove();
            }
        }

        return bestMove;
    }

    public boolean isExplored() {
        return this.explored;
    }

    public SearchTreeNode[] getChildren() {
        return children;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder(50);
        print(buffer, "", "");
        return buffer.toString();
    }

    private void print(StringBuilder buffer, String prefix, String childrenPrefix) {
        buffer.append(prefix);
        buffer.append(move + ": " + score);
        buffer.append('\n');
        if (children != null) {
            for (int i = 0; i < children.length; i++) {
                SearchTreeNode next = children[i];
                if (i < children.length - 1) {
                    next.print(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
                } else {
                    next.print(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
                }
            }
        }
    }
}
