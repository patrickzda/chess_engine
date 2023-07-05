package engine.tools;

import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.Board;
import engine.representation.Color;
import engine.representation.Move;
import engine.representation.PieceType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MonteCarlo {

    private static final int limit = 100;
    private int currentDepth = 0;

    private int totalSimulations = 0;

    private SearchTreeNode root;
    private SearchTreeNode currentNode;

    private Board board;

    private final String originalFEN;


    public MonteCarlo(Board board) {
        this.board = board;
        root = new SearchTreeNode(null, null, null);
        originalFEN = board.toFENString();
    }

    public Move getBestMove() {
        SearchTreeNode current = root.getNextChild(board, totalSimulations);

        SearchTreeNode temp = current;
        ArrayList<Move> moves = new ArrayList<>();

        while (temp != root) {
            moves.add(temp.getMove());
            temp = temp.getParent();
        }

        for (int i = moves.size() - 1; i >= 0; i--) {
            board.doMove(moves.get(i));
        }

        Move[] legalMoves = MoveGenerator.generateLegalMoves(board, new MoveMasks());

        boolean gameFinished = board.isGameLost(new MoveMasks(), legalMoves.length);

        while (!gameFinished || currentDepth < limit) {
            Move randomMove = legalMoves[new Random().nextInt(legalMoves.length)];

            board.doMove(randomMove);


            legalMoves = MoveGenerator.generateLegalMoves(board, new MoveMasks());
        }


        return new Move(0, 0, PieceType.PAWN);
    }
}
