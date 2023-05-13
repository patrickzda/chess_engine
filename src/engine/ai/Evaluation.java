package engine.ai;

import engine.move_generation.MoveMasks;
import engine.representation.Board;
import engine.representation.Color;
import engine.representation.Direction;
import engine.representation.Move;


import static engine.representation.Color.*;

public class Evaluation {
    private static final int PAWN_VALUE = 100, KNIGHT_VALUE = 320, BISHOP_VALUE = 330, ROOK_VALUE = 500, QUEEN_VALUE = 900, KING_VALUE = 20000;
    private static final int BAD_PAWN_STRUCTURE_PENALTY = -50;

    public static int evaluate(Board board) {
        long ownBoard, enemyBoard;
        int ownIsolatedPawns, enemyIsolatedPawns;
        int ownDoubledPawns, enemyDoubledPawns;
        int ownBlockedPawns, enemyBlockedPawns;

        if (board.getTurn() == WHITE) {
            ownBoard = board.whitePieces;
            enemyBoard = board.blackPieces;
            ownIsolatedPawns = getIsolatedPawnCount(board, WHITE);
            enemyIsolatedPawns = getIsolatedPawnCount(board, BLACK);
            ownDoubledPawns = getDoubledPawnCount(board, WHITE);
            enemyDoubledPawns = getDoubledPawnCount(board, BLACK);
            ownBlockedPawns = getBlockedPawnCount(board, WHITE);
            enemyBlockedPawns = getBlockedPawnCount(board, BLACK);
        }
        else {
            ownBoard = board.blackPieces;
            enemyBoard = board.whitePieces;
            ownIsolatedPawns = getIsolatedPawnCount(board, BLACK);
            enemyIsolatedPawns = getIsolatedPawnCount(board, WHITE);
            ownDoubledPawns = getDoubledPawnCount(board, BLACK);
            enemyDoubledPawns = getDoubledPawnCount(board, WHITE);
            ownBlockedPawns = getBlockedPawnCount(board, BLACK);
            enemyBlockedPawns = getBlockedPawnCount(board, WHITE);
        }

        int value = (getSetBits(ownBoard & board.pawns) - getSetBits(enemyBoard & board.pawns)) * PAWN_VALUE;
        value = value + (getSetBits(ownBoard & board.knights) - getSetBits(enemyBoard & board.knights)) * KNIGHT_VALUE;
        value = value + (getSetBits(ownBoard & board.bishops) - getSetBits(enemyBoard & board.bishops)) * BISHOP_VALUE;
        value = value + (getSetBits(ownBoard & board.rooks) - getSetBits(enemyBoard & board.rooks)) * ROOK_VALUE;
        value = value + (getSetBits(ownBoard & board.queens) - getSetBits(enemyBoard & board.queens)) * QUEEN_VALUE;
        value = value + (getSetBits(ownBoard & board.kings) - getSetBits(enemyBoard & board.kings)) * KING_VALUE;
        value = value + (ownBlockedPawns - enemyBlockedPawns + ownDoubledPawns - enemyDoubledPawns + ownIsolatedPawns - enemyIsolatedPawns) * BAD_PAWN_STRUCTURE_PENALTY;
        return value;
    }

    public static int getBlockedPawnCount(Board board, Color color){
        long targetSquares;
        if(color == WHITE){
            targetSquares = ((board.whitePieces & board.pawns) << 8);
        }else{
            targetSquares = ((board.blackPieces & board.pawns) >>> 8);
        }

        targetSquares = targetSquares & (board.whitePieces | board.blackPieces);

        return getSetBits(targetSquares);
    }

    public static int getDoubledPawnCount(Board board, Color color){
        MoveMasks moveMasks = new MoveMasks();
        int doubledPawns = 0;
        long pawns;
        if(color == WHITE){
            pawns = board.whitePieces & board.pawns;
        }else{
            pawns = board.blackPieces & board.pawns;
        }

        for(int i = 0; i < 8; i++){
            int pawnCount = getSetBits(pawns & moveMasks.rays(Direction.NORTH, i));
            if(pawnCount > 1){
                doubledPawns = doubledPawns + pawnCount;
            }
        }

        return doubledPawns;
    }

    public static int getIsolatedPawnCount(Board board, Color color){
        MoveMasks moveMasks = new MoveMasks();
        int isolatedPawns = 0;
        long pawns;
        if(color == WHITE){
            pawns = board.whitePieces & board.pawns;
        }else{
            pawns = board.blackPieces & board.pawns;
        }

        if(((pawns & moveMasks.rays(Direction.NORTH, 0)) != 0) && ((pawns & moveMasks.rays(Direction.NORTH, 1)) == 0)){
            isolatedPawns = isolatedPawns + getSetBits(pawns & moveMasks.rays(Direction.NORTH, 0));
        }

        if(((pawns & moveMasks.rays(Direction.NORTH, 7)) != 0) && ((pawns & moveMasks.rays(Direction.NORTH, 6)) == 0)){
            isolatedPawns = isolatedPawns + getSetBits(pawns & moveMasks.rays(Direction.NORTH, 7));
        }

        for(int i = 1; i < 7; i++){
            if(((pawns & moveMasks.rays(Direction.NORTH, i)) != 0) && ((pawns & moveMasks.rays(Direction.NORTH, i - 1)) == 0) && ((pawns & moveMasks.rays(Direction.NORTH, i + 1)) == 0)){
                isolatedPawns = isolatedPawns + getSetBits(pawns & moveMasks.rays(Direction.NORTH, i));
            }
        }

        return isolatedPawns;
    }

    private static int getSetBits(long l){
        int result = 0;
        for(int i = 0; i < 64; i++){
            if((l & (1L << i)) != 0){
                result++;
            }
        }
        return result;
    }
}
