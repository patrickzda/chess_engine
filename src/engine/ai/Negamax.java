package engine.ai;

import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.*;
import engine.tools.TranspositionTable;
import engine.tools.TranspositionTableEntry;

public class Negamax {
    private static final double EFFECTIVE_BRANCHING_FACTOR = Math.sqrt(35);
    private static final int QUIESCENCE_SEARCH_DEPTH = 3;
    private static final TranspositionTable table = new TranspositionTable();

    private static int search(Board board, int depth, MoveMasks masks, int alpha, int beta, int color, long endTime){
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

        if (depth == 0 || board.isGameLost(masks, moves.length) || moves.length == 0) {
            if(board.getMovesSinceLastPawnMoveOrCapture() == 0 && board.moves.size() > 0){
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
            int finalEval = color * Evaluation.evaluateNegamaxNew(board, masks, moves);
            table.addEntry(board, null, depth, finalEval, EvaluationType.EXACT);
            return finalEval;
        }

        Evaluation.sortMoves(table, board, moves);

        int value = Integer.MIN_VALUE, bestValue = Integer.MIN_VALUE;
        Move bestMove = moves[0];

        for(int i = 0; i < moves.length; i++){
            board.doMove(moves[i]);

            if(i == 0){
                value = Math.max(value, -search(board, depth - 1, masks, -beta, -alpha, -color, endTime));
            }else{
                int nullWindowValue = -search(board, depth - 1, masks, -alpha - 1, -alpha, -color, endTime);
                if(alpha < nullWindowValue && nullWindowValue < beta){
                    value = Math.max(value, -search(board, depth - 1, masks, -beta, -nullWindowValue, -color, endTime));
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

    private static int quiescenceSearch(Board board, int depth, MoveMasks masks, int alpha, int beta, int color){
        Move[] moves = MoveGenerator.generateLegalMoves(board, masks);

        if (depth == 0 || board.isGameLost(masks, moves.length) || moves.length == 0) {
            return color * Evaluation.evaluateNegamaxNew(board, masks, moves);
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
            value = color * Evaluation.evaluateNegamaxNew(board, masks, moves);
        }

        return value;
    }

    public static Move getBestMove(Board board, int depth, MoveMasks masks){
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
            int score = -search(board, depth - 1, masks, -beta, -alpha, color, Long.MAX_VALUE);
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

    private static Move getBestMove(Board board, int depth, MoveMasks masks, long endTime){
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
            int score = -search(board, depth - 1, masks, -beta, -alpha, color, endTime);
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

    public static Move getBestMoveTimed(Board board, int timeInMilliseconds, MoveMasks masks){
        long endTime = System.nanoTime() + timeInMilliseconds * 1000000L, nextDepthSearchTime = 0L;
        int currentSearchDepth = 1;
        Move bestMove = null;

        while(System.nanoTime() + nextDepthSearchTime < endTime){
            long startTime = System.nanoTime();
            Move current = getBestMove(board, currentSearchDepth, masks, endTime);
            if(System.nanoTime() < endTime){
                bestMove = current;
            }
            currentSearchDepth++;
            nextDepthSearchTime = (long) ((System.nanoTime() - startTime) * EFFECTIVE_BRANCHING_FACTOR);
        }

        //System.out.println("REACHED DEPTH " + currentSearchDepth + " in " + (System.nanoTime() - (endTime - timeInMilliseconds * 1000000L)) / 1000000L + " ms");

        return bestMove;
    }

    public static void clearTable(){
        table.clear();
    }

}
