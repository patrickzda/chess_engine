package engine.ai;

import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.*;
import test.AlphaBetaTest;

import java.util.Arrays;

import static java.lang.Math.*;

public class AlphaBeta {
    public TranspositionTable table;
    private final double EFFECTIVE_BRANCHING_FACTOR = Math.sqrt(35), FURTHER_SEARCH_PERCENTAGE = 0.35;
    private final int MINIMAL_WINDOW_OFFSET = 50;

    public int maxHitCount = 0, maxErrorCount = 0, minHitCount = 0, minErrorCount = 0;

    public AlphaBeta(){
        table = new TranspositionTable();
    }

    //Bedingung für Cutoff: Sei E der Elternknoten, A der aktuelle Knoten und K die Menge an Kinderknoten, die Situation wird aus sich von E betrachtet: Falls die Bewertung von E besser ist als die Bewertung von A, so wird K abgeschnitten

    private int alphaBetaMax(Board board, int alpha, int beta, int depth, MoveMasks moveMasks) {
        TranspositionTableEntry entry = table.getEntry(board, depth);

        if(entry != null){
            if(entry.getType() == EvaluationType.EXACT){
                return Math.max(alpha, entry.getEvaluation());
            }
            else if(entry.getType() == EvaluationType.LOWERBOUND){
                alpha = Math.max(alpha, entry.getEvaluation());
            }else if(entry.getType() == EvaluationType.UPPERBOUND){
                beta = Math.min(beta, entry.getEvaluation());
            }
            //
            if(alpha >= beta){
                return entry.getEvaluation();
            }
        }

        Move[] moves = MoveGenerator.generateLegalMoves(board, moveMasks);
        Evaluation.sortMoves(table, board, moves);
        EvaluationType type = EvaluationType.LOWERBOUND;

        if (depth == 0 || board.isGameLost(moveMasks, moves.length) || moves.length == 0) {
            int finalEval = Evaluation.evaluate(board, moveMasks);

            table.addEntry(board, new Move(0, 0, PieceType.PAWN), depth, finalEval, EvaluationType.EXACT);
            return finalEval;
        }

        Move bestMove = moves[0];
        int score;

        for (int i = 0; i < moves.length; i++) {
            board.doMove(moves[i]);

            score = alphaBetaMin(board, alpha, beta, depth - 1, moveMasks);
            board.undoLastMove();

            if (score >= beta) {        //Beta-Cutoff, wenn ich einen Kind-Knoten habe, welcher besser bewertet ist, als Beta → Beta ist der aktuell negativste Wert, welcher erreicht werden kann
                table.addEntry(board, moves[i], depth, -beta, EvaluationType.UPPERBOUND);
                return beta;
            }

            if (score > alpha) {
                type = EvaluationType.EXACT;
                bestMove = moves[i];
                alpha = score;
            }
        }

        table.addEntry(board, bestMove, depth, alpha, type);
        return alpha;
    }

    private int alphaBetaMin(Board board, int alpha, int beta, int depth, MoveMasks moveMasks) {
        TranspositionTableEntry entry = table.getEntry(board, depth);

        if(entry != null){
            if(entry.getType() == EvaluationType.EXACT){
                return Math.min(beta, -entry.getEvaluation());
            }
            else if(entry.getType() == EvaluationType.LOWERBOUND){
                alpha = Math.max(alpha, entry.getEvaluation());
            }else if(entry.getType() == EvaluationType.UPPERBOUND){
                beta = Math.min(beta, entry.getEvaluation());
            }
            //
            if(alpha >= beta){
                return -entry.getEvaluation();
            }
        }

        Move[] moves = MoveGenerator.generateLegalMoves(board, moveMasks);
        Evaluation.sortMoves(table, board, moves);
        EvaluationType type = EvaluationType.LOWERBOUND;

        if (depth == 0 || board.isGameLost(moveMasks, moves.length) || moves.length == 0) {
            int finalEval = -Evaluation.evaluate(board, moveMasks);

            table.addEntry(board, new Move(0, 0, PieceType.PAWN), depth, -finalEval, EvaluationType.EXACT);
            return finalEval;
        }

        Move bestEnemyMove = moves[0];
        int score;

        for (int i = 0; i < moves.length; i++) {
            board.doMove(moves[i]);
            score = alphaBetaMax(board, alpha, beta, depth - 1, moveMasks);
            board.undoLastMove();

            if (score <= alpha) {       //Alpha-Cutoff, wenn ich einen Kind-Knoten habe, welcher schlechter bewertet ist, als Alpha → Alpha ist der aktuell positivste Wert, welcher erreicht werden kann
                table.addEntry(board, moves[i], depth, -alpha, EvaluationType.UPPERBOUND);
                return alpha;
            }

            if (score < beta) {
                type = EvaluationType.EXACT;
                bestEnemyMove = moves[i];
                beta = score;
            }
        }

        table.addEntry(board, bestEnemyMove, depth, -beta, type);

        return beta;
    }

    public Move getBestMove(Board board, Move[] moves, int depth, int alpha, int beta, MoveMasks moveMasks) {
        if (depth < 1) {
            throw new IllegalArgumentException("Suchtiefe muss mindestens 1 sein!");
        }

        if(moves.length == 0){
            return null;
        }

        int score, bestScore = Integer.MIN_VALUE;
        Move bestMove = moves[0];

        for (int i = 0; i < moves.length; i++) {
            board.doMove(moves[i]);
            score = alphaBetaMin(board, alpha, beta, depth - 1, moveMasks);
            moves[i].evaluation = score;
            board.undoLastMove();

            //System.out.println(moves[i].toString() + ": " + score);

            if (score > bestScore) {
                bestScore = score;
                bestMove = moves[i];
            }
        }

        table.addEntry(board, bestMove, depth, bestMove.evaluation, EvaluationType.EXACT);

        return bestMove;
    }

    public void clearTable(){
        table.clear();
    }

    public void printTTStats(){
        table.printStats();
    }

    public Move getBestMoveTimed(Board board, MoveMasks moveMasks, int millis) {
        long startTime = System.nanoTime(), beginningTime = System.nanoTime();
        long finishTime = startTime + (millis * 1000000L);
        long nextDepthSearchTime = 0;
        int searchDepth = 0;
        int alpha = Integer.MIN_VALUE, beta = Integer.MAX_VALUE;

        //int mwsSuccess = 0;

        Move bestMove = new Move(0, 0, PieceType.PAWN);
        Move[] moves = MoveGenerator.generateLegalMoves(board, moveMasks);

        while (System.nanoTime() + nextDepthSearchTime < finishTime) {
            searchDepth++;

            startTime = System.nanoTime();
            bestMove = getBestMove(board, moves, searchDepth, alpha, beta, moveMasks);

            //else{
            //    mwsSuccess++;
            //}

            if(searchDepth >= 2){
                nextDepthSearchTime = Math.round((System.nanoTime() - startTime) * EFFECTIVE_BRANCHING_FACTOR);
            }

            Arrays.sort(moves);
        }

        long stopTime = System.nanoTime();
        long totalTime = (stopTime - beginningTime) / 1000000L;
        double ratio = ((double) totalTime) / millis;

        //System.out.println("gegebene Zeit: " + millis + "ms\n" + "gebrauchte Zeit: " + totalTime + "ms\n" + "Verhältnis: " + ratio + " (" + ratio * 100 + "%)\n" + "erreichte Suchtiefe: " + searchDepth);
        //System.out.println("Anteil erfolgreicher MWS Durchläufe: " + ((double) mwsSuccess / (double) searchDepth));

        return bestMove;
    }

}