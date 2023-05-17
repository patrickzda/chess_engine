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
    private static final int[] pawnPST = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 5, 10, 10, -20, -20, 10, 10, 5, 5, -5, -10, 0, 0, -10, -5, 5, 0, 0, 0, 20, 20, 0, 0, 0, 5, 5, 10, 25, 25, 10, 5, 5, 10, 10, 20, 30, 30, 20, 10, 10, 50, 50, 50, 50, 50, 50, 50, 50, 0, 0, 0, 0, 0, 0, 0, 0};
    private static final int[] knightPST = new int[]{-50, -40, -30, -30, -30, -30, -40, -50, -40, -20, 0, 5, 5, 0, -20, -40, -30, 5, 10, 15, 15, 10, 5, -30, -30, 0, 15, 20, 20, 15, 0, -30, -30, 5, 15, 20, 20, 15, 5, -30, -30, 0, 10, 15, 15, 10, 0, -30, -40, -20, 0, 0, 0, 0, -20, -40, -50, -40, -30, -30, -30, -30, -40, -50};
    private static final int[] bishopPST = new int[]{-20, -10, -10, -10, -10, -10, -10, -20, -10, 5, 0, 0, 0, 0, 5, -10, -10, 10, 10, 10, 10, 10, 10, -10, -10, 0, 10, 10, 10, 10, 0, -10, -10, 5, 5, 10, 10, 5, 5, -10, -10, 0, 5, 10, 10, 5, 0, -10, -10, 0, 0, 0, 0, 0, 0, -10, -20, -10, -10, -10, -10, -10, -10, -20};
    private static final int[] rookPST = new int[]{0, 0, 0, 5, 5, 0, 0, 0, -5, 0, 0, 0, 0, 0, 0, -5, -5, 0, 0, 0, 0, 0, 0, -5, -5, 0, 0, 0, 0, 0, 0, -5, -5, 0, 0, 0, 0, 0, 0, -5, -5, 0, 0, 0, 0, 0, 0, -5, 5, 10, 10, 10, 10, 10, 10, 5, 0, 0, 0, 0, 0, 0, 0, 0};
    private static final int[] queenPST = new int[]{-20, -10, -10, -5, -5, -10, -10, -20, -10, 0, 5, 0, 0, 0, 0, -10, -10, 5, 5, 5, 5, 5, 0, -10, 0, 0, 5, 5, 5, 5, 0, -5, -5, 0, 5, 5, 5, 5, 0, -5, -10, 0, 5, 5, 5, 5, 0, -10, -10, 0, 0, 0, 0, 0, 0, -10, -20, -10, -10, -5, -5, -10, -10, -20};
    private static final int[] kingPST = new int[]{20, 30, 10,  0,  0, 10, 30, 20, 20, 20,  0,  0,  0,  0, 20, 20,-10,-20,-20,0,0,-20,-20,-10,-20,-30,0,20000,20000,0,-30,-20,-30,-40,0,20000,20000,0,-40,-30,-30,-40,-40,-50,-50,-40,-40,-30,-30,-40,-40,-50,-50,-40,-40,-30,-30,-40,-40,-50,-50,-40,-40,-30};

    public static int evaluate(Board board) {
        long ownBoard, enemyBoard;
        int ownIsolatedPawns, enemyIsolatedPawns;
        int ownDoubledPawns, enemyDoubledPawns;
        int ownBlockedPawns, enemyBlockedPawns;
        int pstBonus;

        if (board.getTurn() == WHITE) {
            ownBoard = board.whitePieces;
            enemyBoard = board.blackPieces;
            ownIsolatedPawns = getIsolatedPawnCount(board, WHITE);
            enemyIsolatedPawns = getIsolatedPawnCount(board, BLACK);
            ownDoubledPawns = getDoubledPawnCount(board, WHITE);
            enemyDoubledPawns = getDoubledPawnCount(board, BLACK);
            ownBlockedPawns = getBlockedPawnCount(board, WHITE);
            enemyBlockedPawns = getBlockedPawnCount(board, BLACK);
            pstBonus = calculatePSTBonus(board, WHITE);
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
            pstBonus = calculatePSTBonus(board, BLACK);
        }

        int value = (getSetBits(ownBoard & board.pawns) - getSetBits(enemyBoard & board.pawns)) * PAWN_VALUE;
        value = value + (getSetBits(ownBoard & board.knights) - getSetBits(enemyBoard & board.knights)) * KNIGHT_VALUE;
        value = value + (getSetBits(ownBoard & board.bishops) - getSetBits(enemyBoard & board.bishops)) * BISHOP_VALUE;
        value = value + (getSetBits(ownBoard & board.rooks) - getSetBits(enemyBoard & board.rooks)) * ROOK_VALUE;
        value = value + (getSetBits(ownBoard & board.queens) - getSetBits(enemyBoard & board.queens)) * QUEEN_VALUE;
        value = value + (getSetBits(ownBoard & board.kings) - getSetBits(enemyBoard & board.kings)) * KING_VALUE;
        value = value + (ownBlockedPawns - enemyBlockedPawns + ownDoubledPawns - enemyDoubledPawns + ownIsolatedPawns - enemyIsolatedPawns) * BAD_PAWN_STRUCTURE_PENALTY;
        value = value + pstBonus;
        return value;
    }

    private static int calculatePSTBonus(Board board, Color color){
        long currentTeam, enemyTeam;
        long currentPawns, currentKnights, currentBishops, currentRooks, currentQueens, currentKings;
        long enemyPawns, enemyKnights, enemyBishops, enemyRooks, enemyQueens, enemyKings;
        int result = 0;
        if(color == WHITE){
            currentTeam = board.whitePieces;
            enemyTeam = board.blackPieces;
        }else{
            currentTeam = board.blackPieces;
            enemyTeam = board.whitePieces;
        }

        currentPawns = board.pawns & currentTeam;
        currentKnights = board.knights & currentTeam;
        currentBishops = board.bishops & currentTeam;
        currentRooks = board.rooks & currentTeam;
        currentQueens = board.queens & currentTeam;
        currentKings = board.kings & currentTeam;

        enemyPawns = board.pawns & enemyTeam;
        enemyKnights = board.knights & enemyTeam;
        enemyBishops = board.bishops & enemyTeam;
        enemyRooks = board.rooks & enemyTeam;
        enemyQueens = board.queens & enemyTeam;
        enemyKings = board.kings & enemyTeam;

        for(int i = 0; i < 64; i++){
            long index = 1L << i;
            if((currentTeam & index) != 0){
                if((currentPawns & index) != 0){
                    result = result + pawnPST[i];
                }else if((currentKnights & index) != 0){
                    result = result + knightPST[i];
                }else if((currentBishops & index) != 0){
                    result = result + bishopPST[i];
                }else if((currentRooks & index) != 0){
                    result = result + rookPST[i];
                }else if((currentQueens & index) != 0){
                    result = result + queenPST[i];
                }else if((currentKings & index) != 0){
                    result = result + kingPST[i];
                }
            }else if((enemyTeam & index) != 0){
                if((enemyPawns & index) != 0){
                    result = result + pawnPST[63 - i];
                }else if((enemyKnights & index) != 0){
                    result = result + knightPST[63- i];
                }else if((enemyBishops & index) != 0){
                    result = result + bishopPST[63 - i];
                }else if((enemyRooks & index) != 0){
                    result = result + rookPST[63 - i];
                }else if((enemyQueens & index) != 0){
                    result = result + queenPST[63 - i];
                }else if((enemyKings & index) != 0){
                    result = result + kingPST[63 - i];
                }
            }
        }
        return result;
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

    public static int getSetBits(long l){
        int result = 0;
        for(int i = 0; i < 64; i++){
            if((l & (1L << i)) != 0){
                result++;
            }
        }
        return result;
    }
}
