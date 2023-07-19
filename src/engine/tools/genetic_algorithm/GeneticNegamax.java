package engine.tools.genetic_algorithm;

import engine.ai.Evaluation;
import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.*;
import engine.tools.OpeningBookReader;
import engine.tools.TranspositionTable;
import engine.tools.TranspositionTableEntry;

import java.util.Arrays;

public class GeneticNegamax {
    private static final int QUIESCENCE_SEARCH_DEPTH = 3;
    private static final int NULL_MOVE_DEPTH_REDUCTION = 2;
    private static final int NULL_MOVE_PHASE_LIMIT = 175;
    private final TranspositionTable table = new TranspositionTable();
    private final Chromosome chromosome;

    public GeneticNegamax(Chromosome chromosome){
        this.chromosome = chromosome;
    }

    private int search(Board board, int depth, MoveMasks masks, int alpha, int beta, int color, long endTime, boolean nullMoveAllowed, boolean wasNullMove){
        int startAlpha = alpha;

        TranspositionTableEntry entry = table.getEntry(board, depth);
        if(entry != null){
            if(entry.getType() == EvaluationType.EXACT){
                return entry.getEvaluation();
            }else if(entry.getType() == EvaluationType.LOWERBOUND){
                alpha = Math.max(alpha, entry.getEvaluation());
            }else if(entry.getType() == EvaluationType.UPPERBOUND){
                beta = Math.min(beta, entry.getEvaluation());
            }

            if(alpha >= beta){
                return entry.getEvaluation();
            }
        }

        if(System.nanoTime() >= endTime){
            return 0;
        }

        Move[] moves = MoveGenerator.generateLegalMoves(board, masks);

        boolean isGameLost = board.isGameLost(masks, moves.length);
        if (depth == 0 || isGameLost || moves.length == 0) {
            if(wasNullMove && !isGameLost && moves.length > 0){
                int finalEval = -quiescenceSearch(board, QUIESCENCE_SEARCH_DEPTH, masks, -beta, -alpha, -color);
                table.addEntry(board, null, depth, finalEval, EvaluationType.EXACT);
                return finalEval;
            }

            if(board.getMovesSinceLastPawnMoveOrCapture() == 0 && board.moves.size() > 0 && !isGameLost && moves.length > 0){
                Move lastMove = board.moves.get(board.moves.size() - 1);
                board.undoLastMove();
                PieceType capturedPiece = board.getCapturedPieceType(lastMove);
                board.doMove(lastMove);

                if(capturedPiece != null){
                    int finalEval = -quiescenceSearch(board, QUIESCENCE_SEARCH_DEPTH, masks, -beta, -alpha, -color);
                    table.addEntry(board, null, depth, finalEval, EvaluationType.EXACT);
                    return finalEval;
                }
            }

            int finalEval = color * chromosome.evaluate(board, masks, moves);
            table.addEntry(board, null, depth, finalEval, EvaluationType.EXACT);
            return finalEval;
        }

        //Null-Move-Pruning
        if(nullMoveAllowed && !board.isInCheck(masks) && board.moves.size() > 0 && Evaluation.getGamePhase(board) < NULL_MOVE_PHASE_LIMIT && depth >= (1 + NULL_MOVE_DEPTH_REDUCTION)){
            board.doNullMove();
            int nullMoveScore = -search(board, depth - 1 - NULL_MOVE_DEPTH_REDUCTION, masks, -beta, -beta + 1, -color, endTime, false, true);
            board.doNullMove();
            if(System.nanoTime() >= endTime){
                return 0;
            }
            if(nullMoveScore >= beta){
                return beta;
            }
        }

        Evaluation.sortMoves(table, board, moves);

        int value = Integer.MIN_VALUE, bestValue = Integer.MIN_VALUE;
        Move bestMove = moves[0];

        for(int i = 0; i < moves.length; i++){
            board.doMove(moves[i]);

            if(i == 0){
                value = Math.max(value, -search(board, depth - 1, masks, -beta, -alpha, -color, endTime, true, false));
            }else{
                int nullWindowValue = -search(board, depth - 1, masks, -alpha - 1, -alpha, -color, endTime, false, false);
                if(alpha < nullWindowValue && nullWindowValue < beta){
                    value = Math.max(value, -search(board, depth - 1, masks, -beta, -nullWindowValue, -color, endTime, true, false));
                }else{
                    value = Math.max(value, nullWindowValue);
                }
            }
            board.undoLastMove();

            if(bestValue < value){
                bestValue = value;
                bestMove = moves[i];
            }

            alpha = Math.max(alpha, value);
            if(alpha >= beta){
                break;
            }
        }

        //Abspeichern in der Transposition-Table
        EvaluationType type = EvaluationType.EXACT;
        if(value <= startAlpha){
            type = EvaluationType.UPPERBOUND;
        }else if(value >= beta){
            type = EvaluationType.LOWERBOUND;
        }

        table.addEntry(board, bestMove, depth, value, type);

        return value;
    }

    private int quiescenceSearch(Board board, int depth, MoveMasks masks, int alpha, int beta, int color){
        Move[] moves = MoveGenerator.generateLegalMoves(board, masks);

        if (depth == 0 || board.isGameLost(masks, moves.length) || moves.length == 0) {
            return color * chromosome.evaluate(board, masks, moves);
        }

        Evaluation.sortMoves(table, board, moves);
        int value = Integer.MIN_VALUE;

        for(int i = 0; i < moves.length; i++){
            if(moves[i].capturedPieceType != null && moves[i].getPieceType().ordinal() >= moves[i].capturedPieceType.ordinal()){
                board.doMove(moves[i]);
                value = Math.max(value, -quiescenceSearch(board, depth - 1, masks, -beta, -alpha, -color));
                board.undoLastMove();

                alpha = Math.max(alpha, value);
                if(alpha >= beta){
                    break;
                }
            }
        }

        if(value == Integer.MIN_VALUE){
            value = color * chromosome.evaluate(board, masks, moves);
        }

        return value;
    }

    public Move[] getSortedMoves(Board board, int depth, MoveMasks masks){
        Move[] moves = MoveGenerator.generateLegalMoves(board, masks);
        if(moves.length == 0){
            return null;
        }

        int alpha = -Integer.MAX_VALUE, beta = Integer.MAX_VALUE;

        int color = 1;
        if(board.getTurn() == Color.WHITE){
            color = -1;
        }

        for(int i = 0; i < moves.length; i++){
            board.doMove(moves[i]);
            int score = -search(board, depth - 1, masks, -beta, -alpha, color, Long.MAX_VALUE, true, false);
            board.undoLastMove();

            if(score > alpha){
                alpha = score;
            }
            moves[i].evaluation = score;
        }

        Arrays.sort(moves);
        return moves;
    }

    private Move getBestMove(Board board, int depth, MoveMasks masks, long endTime){
        Move[] moves = MoveGenerator.generateLegalMoves(board, masks);
        if(moves.length == 0){
            return null;
        }

        int bestScore = Integer.MIN_VALUE, alpha = -Integer.MAX_VALUE, beta = Integer.MAX_VALUE;
        Move bestMove = moves[0];

        int color = 1;
        if(board.getTurn() == Color.WHITE){
            color = -1;
        }

        for(int i = 0; i < moves.length; i++){
            board.doMove(moves[i]);
            int score = -search(board, depth - 1, masks, -beta, -alpha, color, endTime, true, false);
            board.undoLastMove();

            if(score > bestScore){
                alpha = score;
                bestScore = score;
                bestMove = moves[i];
            }
        }

        bestMove.evaluation = bestScore;
        return bestMove;
    }

    public Move getBestMoveTimed(Board board, int timeInMilliseconds, MoveMasks masks){
        long endTime = System.nanoTime() + timeInMilliseconds * 1000000L;
        int currentSearchDepth = 1;
        Move bestMove = getBestMove(board, currentSearchDepth, masks, endTime);

        while(System.nanoTime() < endTime){
            Move current = getBestMove(board, currentSearchDepth, masks, endTime);
            if(System.nanoTime() < endTime){
                bestMove = current;
            }
            currentSearchDepth++;
        }

        //System.out.println("REACHED DEPTH " + (currentSearchDepth - 1) + " in " + (System.nanoTime() - (endTime - timeInMilliseconds * 1000000L)) / 1000000L + " ms");

        return bestMove;
    }

    public void clearTable(){
        table.clear();
    }

}
