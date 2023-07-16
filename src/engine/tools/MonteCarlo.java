package engine.tools;

import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.*;

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

    private Move exploreNextMove() {
        // finde den nächsten zu erkundenden Zustand
        SearchTreeNode current = root.getNextChild(board, totalSimulations);

        SearchTreeNode temp = current;
        ArrayList<Move> moves = new ArrayList<>();

        // Alle Moves, die zu diesem Zustand führen merken...
        while (temp != root) {
            moves.add(temp.getMove());
            temp = temp.getParent();
        }

        // ...und auf dem Board ausführen
        for (int i = moves.size() - 1; i >= 0; i--) {
            board.doMove(moves.get(i));
            currentDepth++;
        }
        int childToExploreDepth = currentDepth;

        // von hier aus alle legalen Züge generieren
        Move[] legalMoves = MoveGenerator.generateLegalMoves(board, new MoveMasks());

        GameState gameState = board.getGameState(new MoveMasks());

        // so lange random Rollout machen, bis ein Spielende oder die maximale Suchtiefe erreicht ist
        while ((gameState != GameState.DRAW && gameState != GameState.BLACK_WON && gameState != GameState.WHITE_WON) || currentDepth < limit) {
            Move randomMove = legalMoves[new Random().nextInt(legalMoves.length)];

            board.doMove(randomMove);
            currentDepth++;

            legalMoves = MoveGenerator.generateLegalMoves(board, new MoveMasks());
            gameState = board.getGameState(new MoveMasks());
        }

        // Am Ende den Score berechnen
        int score = 0;
        if (board.isGameLost(new MoveMasks(), legalMoves.length)) {
            score = -1;
        }

        if ((currentDepth - childToExploreDepth) % 2 != 0) {
            score *= -1;
        }

        // Backtracking und Scores anpassen
        while (current != root) {
            current.addScore(score);
            score *= -1;
            current = current.getParent();
        }
        root.addScore(score);

        // Original Boardzustand wieder herstellen
        board = new Board(originalFEN);

        Move bestMove = new Move(0, 0, PieceType.PAWN);
        int bestScore = Integer.MIN_VALUE;
        SearchTreeNode children[] = root.getChildren();

        // den Zug der zum besten Score führt auswählen
        for (SearchTreeNode child : children) {
            if (child.getScore() > bestScore) {
                bestScore = child.getScore();
                bestMove = child.getMove();
            }

        }

        return bestMove;
    }

    public Move getBestMove(int iterations) {
        for (int i = 0; i < iterations; i++) {
            totalSimulations++;
            this.exploreNextMove();
        }

        for (int i = 0; i < root.getChildren().length; i++) {
            SearchTreeNode child = root.getChildren()[i];
            System.out.println(i + ": Move: " + child.getMove() + ", Score: " + child.getScore() + ", Ratio: " + child.getScore() / child.getVisits());
        }


        return root.getMoveWithBestRatio();
    }

    public void printTree() {
        System.out.println(root.toString());
    }
}

