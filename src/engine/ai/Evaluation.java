package engine.ai;

import engine.move_generation.MoveMasks;
import engine.representation.*;


import static engine.representation.Color.*;

public class Evaluation {
    private static final int PAWN_VALUE = 100, KNIGHT_VALUE = 280, BISHOP_VALUE = 320, ROOK_VALUE = 480, QUEEN_VALUE = 930, KING_VALUE = 60000;
    private static final int BAD_PAWN_STRUCTURE_PENALTY = -25;
    private static final int CHECKMATE_BONUS = KING_VALUE + 10 * QUEEN_VALUE;
    private static final int[] pawnPSTWhite = new int[]{0, 0, 0, 0, 0, 0, 0, 0, -31, 8, -7, -37, -36, -14, 3, -31, -22, 9, 5, -11, -10, -2, 3, -19, -26, 3, 10, 9, 6, 1, 0, -23, -17, 16, -2, 15, 14, 0, 15, -13, 7, 29, 21, 44, 40, 31, 44, 7, 78, 83, 86, 73, 102, 82, 85, 90, 0, 0, 0, 0, 0, 0, 0, 0};
    private static final int[] knightPSTWhite = new int[]{-74, -23, -26, -24, -19, -35, -22, -69, -23, -15, 2, 0, 2, 0, -23, -20, -18, 10, 13, 22, 18, 15, 11, -14, -1, 5, 31, 21, 22, 35, 2, 0, 24, 24, 45, 37, 33, 41, 25, 17, 10, 67, 1, 74, 73, 27, 62, -2, -3, -6, 100, -36, 4, 62, -4, -14, -66, -53, -75, -75, -10, -55, -58, -70};
    private static final int[] bishopPSTWhite = new int[]{-7, 2, -15, -12, -14, -15, -10, -10, 19,  20, 11, 6, 7, 6, 20, 16, 14, 25, 24, 15, 8, 25, 20, 15, 13, 10, 17, 23, 17, 16, 0, 7, 25, 17, 20, 34, 26, 25, 15, 10, -9, 39, -32, 41, 52, -10, 28, -14, -11, 20, 35, -42, -39, 31, 2, -22, -59, -78, -82, -76, -23,-107, -37, -50};
    private static final int[] rookPSTWhite = new int[]{-30, -24, -18, 5, -2, -18, -31, -32, -53, -38, -31, -26, -29, -43, -44, -53,-42, -28, -42, -25, -25, -35, -26, -46, -28, -35, -16, -21, -13, -29, -46, -30, 0, 5, 16, 13, 18, -4, -9, -6, 19, 35, 28, 33, 45, 27, 25, 15, 55, 29, 56, 67, 55, 62, 34, 60, 35, 29, 33, 4, 37, 33, 56, 50};
    private static final int[] queenPSTWhite = new int[]{-39, -30, -31, -13, -31, -36, -34, -42, -36, -18, 0, -19, -15, -15, -21, -38, -30, -6, -13, -11, -16, -11, -16, -27,-14, -15, -2, -5, -1, -10, -20, -22, 1, -16, 22, 17, 25, 20, -13, -6, -2, 43, 32, 60, 72, 63, 43, 2, 14, 32, 60, -10, 20, 76, 57, 24, 6, 1, -8, -104, 69, 24, 88, 26};
    private static final int[] kingPSTWhite = new int[]{17, 30, -3, -14, 6, -1, 40, 18, -4, 3, -14, 0, 0, -18, 13, 4, -47, -42, -43, 0, 0, -32, -29, -32, -55, -43, -52, CHECKMATE_BONUS, CHECKMATE_BONUS, -47, -8, -50, -55, 50, 11, CHECKMATE_BONUS, CHECKMATE_BONUS, 13, 0, -49, -62, 12, -57, 44, -67, 28, 37, -31, -32, 10, 55, 56, 56, 55, 10, 3, 4, 54, 47, -99, -99, 60, 83, -62};
    private static final int[] pawnPSTBlack = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 78, 83, 86, 73, 102, 82, 85, 90, 7, 29, 21, 44, 40, 31, 44, 7, -17, 16, -2, 15, 14, 0, 15, -13, -26, 3, 10, 9, 6, 1, 0, -23, -22, 9, 5, -11, -10, -2, 3, -19, -31, 8, -7, -37, -36, -14, 3, -31, 0, 0, 0, 0, 0, 0, 0, 0};
    private static final int[] knightPSTBlack = new int[]{-66, -53, -75, -75, -10, -55, -58, -70, -3, -6, 100, -36, 4, 62, -4, -14, 10, 67, 1, 74, 73, 27, 62, -2, 24, 24, 45, 37, 33, 41, 25, 17, -1, 5, 31, 21, 22, 35, 2, 0, -18, 10, 13, 22, 18, 15, 11, -14, -23, -15, 2, 0, 2, 0, -23, -20, -74, -23, -26, -24, -19, -35, -22, -69};
    private static final int[] bishopPSTBlack = new int[]{-59, -78, -82, -76, -23, -107, -37, -50, -11, 20, 35, -42, -39, 31, 2, -22, -9, 39, -32, 41, 52, -10, 28, -14, 25, 17, 20, 34, 26, 25, 15, 10, 13, 10, 17, 23, 17, 16, 0, 7, 14, 25, 24, 15, 8, 25, 20, 15, 19, 20, 11, 6, 7, 6, 20, 16, -7, 2, -15, -12, -14, -15, -10, -10};
    private static final int[] rookPSTBlack = new int[]{35, 29, 33, 4, 37, 33, 56, 50, 55, 29, 56, 67, 55, 62, 34, 60, 19, 35, 28, 33, 45, 27, 25, 15, 0, 5, 16, 13, 18, -4, -9, -6, -28, -35, -16, -21, -13, -29, -46, -30,-42, -28, -42, -25, -25, -35, -26, -46, -53, -38, -31, -26, -29, -43, -44, -53, -30, -24, -18, 5, -2, -18, -31, -32};
    private static final int[] queenPSTBlack = new int[]{6, 1, -8, -104, 69, 24, 88, 26, 14, 32, 60, -10, 20, 76, 57, 24, -2, 43, 32, 60, 72, 63, 43, 2, 1, -16, 22, 17, 25, 20, -13, -6, -14, -15, -2, -5, -1, -10, -20, -22,-30, -6, -13, -11, -16, -11, -16, -27, -36, -18, 0, -19, -15, -15, -21, -38, -39, -30, -31, -13, -31, -36, -34, -42};
    private static final int[] kingPSTBlack = new int[]{4, 54, 47, -99, -99, 60, 83, -62, -32, 10, 55, 56, 56, 55, 10, 3, -62, 12, -57, 44, -67, 28, 37, -31, -55, 50, 11, CHECKMATE_BONUS, CHECKMATE_BONUS, 13, 0, -49, -55, -43, -52, CHECKMATE_BONUS, CHECKMATE_BONUS, -47, -8, -50, -47, -42, -43, 0, 0, -32, -29, -32, -4, 3, -14, 0, 0, -18, 13, 4, 17, 30, -3, -14, 6, -1, 40, 18};

    public static int evaluate(Board board, MoveMasks masks) {
        long ownBoard, enemyBoard;
        int isolatedPawns, doubledPawns, blockedPawns;

        GameState state = board.getGameState(masks);
        if(state == GameState.DRAW){
            return 0;
        }else if(state == GameState.WHITE_WON | state == GameState.BLACK_WON){
            return -CHECKMATE_BONUS;
        }

        if (board.getTurn() == WHITE) {
            ownBoard = board.whitePieces;
            enemyBoard = board.blackPieces;
            isolatedPawns = getIsolatedPawnCount(board, WHITE) - getIsolatedPawnCount(board, BLACK);
            doubledPawns = getDoubledPawnCount(board, WHITE) - getDoubledPawnCount(board, BLACK);
            blockedPawns = getBlockedPawnCount(board, WHITE) - getBlockedPawnCount(board, BLACK);
        }else {
            ownBoard = board.blackPieces;
            enemyBoard = board.whitePieces;
            isolatedPawns = getIsolatedPawnCount(board, BLACK) - getIsolatedPawnCount(board, WHITE);
            doubledPawns = getDoubledPawnCount(board, BLACK) - getDoubledPawnCount(board, WHITE);
            blockedPawns = getBlockedPawnCount(board, BLACK) - getBlockedPawnCount(board, WHITE);
        }

        int value = (getSetBits(ownBoard & board.pawns) - getSetBits(enemyBoard & board.pawns)) * PAWN_VALUE;
        value = value + (getSetBits(ownBoard & board.knights) - getSetBits(enemyBoard & board.knights)) * KNIGHT_VALUE;
        value = value + (getSetBits(ownBoard & board.bishops) - getSetBits(enemyBoard & board.bishops)) * BISHOP_VALUE;
        value = value + (getSetBits(ownBoard & board.rooks) - getSetBits(enemyBoard & board.rooks)) * ROOK_VALUE;
        value = value + (getSetBits(ownBoard & board.queens) - getSetBits(enemyBoard & board.queens)) * QUEEN_VALUE;
        value = value + (getSetBits(ownBoard & board.kings) - getSetBits(enemyBoard & board.kings)) * KING_VALUE;
        value = value + (isolatedPawns + doubledPawns + blockedPawns) * BAD_PAWN_STRUCTURE_PENALTY;

        value = value + calculatePSTBonus(board);;
        return value;
    }

    private static int calculatePSTBonus(Board board){
        long whitePawns, whiteKnights, whiteBishops, whiteRooks, whiteQueens, whiteKings;
        long blackPawns, blackKnights, blackBishops, blackRooks, blackQueens, blackKings;
        int whiteScore = 0, blackScore = 0;

        whitePawns = board.pawns & board.whitePieces;
        whiteKnights = board.knights & board.whitePieces;
        whiteBishops = board.bishops & board.whitePieces;
        whiteRooks = board.rooks & board.whitePieces;
        whiteQueens = board.queens & board.whitePieces;
        whiteKings = board.kings & board.whitePieces;

        blackPawns = board.pawns & board.blackPieces;
        blackKnights = board.knights & board.blackPieces;
        blackBishops = board.bishops & board.blackPieces;
        blackRooks = board.rooks & board.blackPieces;
        blackQueens = board.queens & board.blackPieces;
        blackKings = board.kings & board.blackPieces;

        for(int i = 0; i < 64; i++){
            long index = 1L << i;
            if((board.whitePieces & index) != 0){
                if((whitePawns & index) != 0){
                    whiteScore = whiteScore + pawnPSTWhite[i];
                }else if((whiteKnights & index) != 0){
                    whiteScore = whiteScore + knightPSTWhite[i];
                }else if((whiteBishops & index) != 0){
                    whiteScore = whiteScore + bishopPSTWhite[i];
                }else if((whiteRooks & index) != 0){
                    whiteScore = whiteScore + rookPSTWhite[i];
                }else if((whiteQueens & index) != 0){
                    whiteScore = whiteScore + queenPSTWhite[i];
                }else if((whiteKings & index) != 0){
                    whiteScore = whiteScore + kingPSTWhite[i];
                }
            }
        }

        for(int i = 0; i < 64; i++){
            long index = 1L << i;
            if((board.blackPieces & index) != 0){
                if((blackPawns & index) != 0){
                    blackScore = blackScore + pawnPSTBlack[i];
                }else if((blackKnights & index) != 0){
                    blackScore = blackScore + knightPSTBlack[i];
                }else if((blackBishops & index) != 0){
                    blackScore = blackScore + bishopPSTBlack[i];
                }else if((blackRooks & index) != 0){
                    blackScore = blackScore + rookPSTBlack[i];
                }else if((blackQueens & index) != 0){
                    blackScore = blackScore + queenPSTBlack[i];
                }else if((blackKings & index) != 0){
                    blackScore = blackScore + kingPSTBlack[i];
                }
            }
        }

        if(board.getTurn() == WHITE){
            return whiteScore - blackScore;
        }else{
            return blackScore - whiteScore;
        }
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
