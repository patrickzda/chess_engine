package engine.ai;

import engine.move_generation.MoveMasks;
import engine.representation.*;
import engine.tools.PST;
import engine.tools.TaperedPST;
import engine.tools.TranspositionTable;
import engine.tools.TranspositionTableEntry;


import java.util.Arrays;

import static engine.representation.Color.*;

public class Evaluation {
    private static final int[] PIECE_VALUES = new int[]{60000, 930, 480, 320, 280, 100};
    private static final int PAWN_VALUE = 100, KNIGHT_VALUE = 280, BISHOP_VALUE = 320, ROOK_VALUE = 480, QUEEN_VALUE = 930, KING_VALUE = 60000;
    private static final int KNIGHT_PHASE_VALUE = 1, BISHOP_PHASE_VALUE = 1, ROOK_PHASE_VALUE = 2, QUEEN_PHASE_VALUE = 4;
    private static final int TOTAL_PHASE = KNIGHT_PHASE_VALUE * 4 + BISHOP_PHASE_VALUE * 4 + ROOK_PHASE_VALUE * 4 + QUEEN_PHASE_VALUE * 2;
    private static final int BAD_PAWN_STRUCTURE_PENALTY = -25;
    private static final int CHECKMATE_BONUS = KING_VALUE + 10 * QUEEN_VALUE;

    public static int evaluate(Board board, MoveMasks masks) {
        long ownBoard, enemyBoard;
        int isolatedPawns, doubledPawns, blockedPawns;

        GameState state = board.getGameState(masks);
        if(state == GameState.DRAW){
            return 0;
        }else if(state == GameState.WHITE_WON | state == GameState.BLACK_WON){
            return -CHECKMATE_BONUS + board.moves.size();
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

    public static int evaluateNegamax(Board board, MoveMasks masks){
        GameState state = board.getGameState(masks);
        if(state == GameState.DRAW){
            return 0;
        }else if(state == GameState.WHITE_WON){
            return CHECKMATE_BONUS - board.moves.size();
        }else if(state == GameState.BLACK_WON){
            return -CHECKMATE_BONUS + board.moves.size();
        }

        int value = 0;
        value = value + (getSetBits(board.whitePieces & board.pawns) - getSetBits(board.blackPieces & board.pawns)) * PAWN_VALUE;
        value = value + (getSetBits(board.whitePieces & board.knights) - getSetBits(board.blackPieces & board.knights)) * KNIGHT_VALUE;
        value = value + (getSetBits(board.whitePieces & board.bishops) - getSetBits(board.blackPieces & board.bishops)) * BISHOP_VALUE;
        value = value + (getSetBits(board.whitePieces & board.rooks) - getSetBits(board.blackPieces & board.rooks)) * ROOK_VALUE;
        value = value + (getSetBits(board.whitePieces & board.queens) - getSetBits(board.blackPieces & board.queens)) * QUEEN_VALUE;
        value = value + (getSetBits(board.whitePieces & board.kings) - getSetBits(board.blackPieces & board.kings)) * KING_VALUE;
        value = value + (getIsolatedPawnCount(board, WHITE) - getIsolatedPawnCount(board, BLACK)) * BAD_PAWN_STRUCTURE_PENALTY;
        value = value + (getDoubledPawnCount(board, WHITE) - getDoubledPawnCount(board, BLACK)) * BAD_PAWN_STRUCTURE_PENALTY;
        value = value + (getBlockedPawnCount(board, WHITE) - getBlockedPawnCount(board, BLACK)) * BAD_PAWN_STRUCTURE_PENALTY;
        value = value + calculatePSTBonusNegamax(board);

        return value;
    }

    public static double getGamePhase(Board board){
        double phase = TOTAL_PHASE;
        phase = phase - getSetBits(board.knights) * KNIGHT_PHASE_VALUE;
        phase = phase - getSetBits(board.bishops) * BISHOP_PHASE_VALUE;
        phase = phase - getSetBits(board.rooks) * ROOK_PHASE_VALUE;
        phase = phase - getSetBits(board.queens) * QUEEN_PHASE_VALUE;
        phase = (phase * 256 + ((double) TOTAL_PHASE / 2)) / TOTAL_PHASE;
        return phase;
    }

    private static double evaluateNegamaxNew(Board board, MoveMasks masks){
        GameState state = board.getGameState(masks);
        if(state == GameState.DRAW){
            return 0;
        }else if(state == GameState.WHITE_WON){
            return CHECKMATE_BONUS - board.moves.size();
        }else if(state == GameState.BLACK_WON){
            return -CHECKMATE_BONUS + board.moves.size();
        }

        long whitePawns, whiteKnights, whiteBishops, whiteRooks, whiteQueens, whiteKings;
        long blackPawns, blackKnights, blackBishops, blackRooks, blackQueens, blackKings;
        int openingScore = 0, endGameScore = 0, materialScore = 0, pawnStructureScore = 0;
        double phase = getGamePhase(board);

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

        materialScore = materialScore + (getSetBits(whitePawns) - getSetBits(blackPawns)) * PAWN_VALUE;
        materialScore = materialScore + (getSetBits(whiteKnights) - getSetBits(blackKnights)) * KNIGHT_VALUE;
        materialScore = materialScore + (getSetBits(whiteBishops) - getSetBits(blackBishops)) * BISHOP_VALUE;
        materialScore = materialScore + (getSetBits(whiteRooks) - getSetBits(blackRooks)) * ROOK_VALUE;
        materialScore = materialScore + (getSetBits(whiteQueens) - getSetBits(blackQueens)) * QUEEN_VALUE;
        materialScore = materialScore + (getSetBits(whiteKings) - getSetBits(blackKings)) * KING_VALUE;

        pawnStructureScore = pawnStructureScore + (getIsolatedPawnCount(board, WHITE) - getIsolatedPawnCount(board, BLACK)) * BAD_PAWN_STRUCTURE_PENALTY;
        pawnStructureScore = pawnStructureScore + (getDoubledPawnCount(board, WHITE) - getDoubledPawnCount(board, BLACK)) * BAD_PAWN_STRUCTURE_PENALTY;
        pawnStructureScore = pawnStructureScore + (getBlockedPawnCount(board, WHITE) - getBlockedPawnCount(board, BLACK)) * BAD_PAWN_STRUCTURE_PENALTY;

        for(int i = 0; i < 64; i++){
            long index = 1L << i;
            if((board.whitePieces & index) != 0){
                if((whitePawns & index) != 0){
                    openingScore = openingScore + TaperedPST.PAWN_PST_MID[i];
                    endGameScore = endGameScore + TaperedPST.PAWN_PST_END[i];
                }else if((whiteKnights & index) != 0){
                    openingScore = openingScore + TaperedPST.KNIGHT_PST_MID[i];
                    endGameScore = endGameScore + TaperedPST.KNIGHT_PST_END[i];
                }else if((whiteBishops & index) != 0){
                    openingScore = openingScore + TaperedPST.BISHOP_PST_MID[i];
                    endGameScore = endGameScore + TaperedPST.BISHOP_PST_END[i];
                }else if((whiteRooks & index) != 0){
                    openingScore = openingScore + TaperedPST.ROOK_PST_MID[i];
                    endGameScore = endGameScore + TaperedPST.ROOK_PST_END[i];
                }else if((whiteQueens & index) != 0){
                    openingScore = openingScore + TaperedPST.QUEEN_PST_MID[i];
                    endGameScore = endGameScore + TaperedPST.QUEEN_PST_END[i];
                }else if((whiteKings & index) != 0){
                    openingScore = openingScore + TaperedPST.KING_PST_MID[i];
                    endGameScore = endGameScore + TaperedPST.KING_PST_END[i];
                }
            }else if((board.blackPieces & index) != 0){
                int mirroredIndex = TaperedPST.getMirroredIndex(i);
                if((blackPawns & index) != 0){
                    openingScore = openingScore - TaperedPST.PAWN_PST_MID[mirroredIndex];
                    endGameScore = endGameScore - TaperedPST.PAWN_PST_END[mirroredIndex];
                }else if((blackKnights & index) != 0){
                    openingScore = openingScore - TaperedPST.KNIGHT_PST_MID[mirroredIndex];
                    endGameScore = endGameScore - TaperedPST.KNIGHT_PST_END[mirroredIndex];
                }else if((blackBishops & index) != 0){
                    openingScore = openingScore - TaperedPST.BISHOP_PST_MID[mirroredIndex];
                    endGameScore = endGameScore - TaperedPST.BISHOP_PST_END[mirroredIndex];
                }else if((blackRooks & index) != 0){
                    openingScore = openingScore - TaperedPST.ROOK_PST_MID[mirroredIndex];
                    endGameScore = endGameScore - TaperedPST.ROOK_PST_END[mirroredIndex];
                }else if((blackQueens & index) != 0){
                    openingScore = openingScore - TaperedPST.QUEEN_PST_MID[mirroredIndex];
                    endGameScore = endGameScore - TaperedPST.QUEEN_PST_END[mirroredIndex];
                }else if((blackKings & index) != 0){
                    openingScore = openingScore - TaperedPST.KING_PST_MID[mirroredIndex];
                    endGameScore = endGameScore - TaperedPST.KING_PST_END[mirroredIndex];
                }
            }
        }

        return (((openingScore * (256 - phase)) + (endGameScore * phase)) / 256) + materialScore + pawnStructureScore;
    }

    public static void sortMoves(TranspositionTable table, Board board, Move[] moves){
        TranspositionTableEntry entry = table.lookup(board);

        if(entry != null){
            Move bestSavedMove = entry.getBestMove();
            for(int i = 0; i < moves.length; i++){
                if(bestSavedMove.getStartFieldIndex() == moves[i].getStartFieldIndex() && bestSavedMove.getEndFieldIndex() == moves[i].getEndFieldIndex()){
                    moves[i].evaluation = moves[i].evaluation + 10000;
                    break;
                }
            }
        }

        for(int i = 0; i < moves.length; i++){
            PieceType capturedPiece = board.getCapturedPieceType(moves[i]);
            moves[i].capturedPieceType = capturedPiece;

            if(capturedPiece != null){
                moves[i].evaluation = PIECE_VALUES[capturedPiece.ordinal()] - PIECE_VALUES[moves[i].getPieceType().ordinal()];
            }

            if(moves[i].isPromotionToQueen){
                moves[i].evaluation = moves[i].evaluation + QUEEN_VALUE;
            }else if(moves[i].isPromotionToRook){
                moves[i].evaluation = moves[i].evaluation + ROOK_VALUE;
            }else if(moves[i].isPromotionToBishop){
                moves[i].evaluation = moves[i].evaluation + BISHOP_VALUE;
            }else if(moves[i].isPromotionToKnight){
                moves[i].evaluation = moves[i].evaluation + KNIGHT_VALUE;
            }
        }

        Arrays.sort(moves);
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
                    whiteScore = whiteScore + PST.pawnPSTWhite[i];
                }else if((whiteKnights & index) != 0){
                    whiteScore = whiteScore + PST.knightPSTWhite[i];
                }else if((whiteBishops & index) != 0){
                    whiteScore = whiteScore + PST.bishopPSTWhite[i];
                }else if((whiteRooks & index) != 0){
                    whiteScore = whiteScore + PST.rookPSTWhite[i];
                }else if((whiteQueens & index) != 0){
                    whiteScore = whiteScore + PST.queenPSTWhite[i];
                }else if((whiteKings & index) != 0){
                    whiteScore = whiteScore + PST.kingPSTWhite[i];
                }
            }
        }

        for(int i = 0; i < 64; i++){
            long index = 1L << i;
            if((board.blackPieces & index) != 0){
                if((blackPawns & index) != 0){
                    blackScore = blackScore + PST.pawnPSTBlack[i];
                }else if((blackKnights & index) != 0){
                    blackScore = blackScore + PST.knightPSTBlack[i];
                }else if((blackBishops & index) != 0){
                    blackScore = blackScore + PST.bishopPSTBlack[i];
                }else if((blackRooks & index) != 0){
                    blackScore = blackScore + PST.rookPSTBlack[i];
                }else if((blackQueens & index) != 0){
                    blackScore = blackScore + PST.queenPSTBlack[i];
                }else if((blackKings & index) != 0){
                    blackScore = blackScore + PST.kingPSTBlack[i];
                }
            }
        }

        if(board.getTurn() == WHITE){
            return whiteScore - blackScore;
        }else{
            return blackScore - whiteScore;
        }
    }

    private static int calculatePSTBonusNegamax(Board board){
        long whitePawns, whiteKnights, whiteBishops, whiteRooks, whiteQueens, whiteKings;
        long blackPawns, blackKnights, blackBishops, blackRooks, blackQueens, blackKings;
        int score = 0;

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
                    score = score + PST.pawnPSTWhite[i];
                }else if((whiteKnights & index) != 0){
                    score = score + PST.knightPSTWhite[i];
                }else if((whiteBishops & index) != 0){
                    score = score + PST.bishopPSTWhite[i];
                }else if((whiteRooks & index) != 0){
                    score = score + PST.rookPSTWhite[i];
                }else if((whiteQueens & index) != 0){
                    score = score + PST.queenPSTWhite[i];
                }else if((whiteKings & index) != 0){
                    score = score + PST.kingPSTWhite[i];
                }
            }
        }

        for(int i = 0; i < 64; i++){
            long index = 1L << i;
            if((board.blackPieces & index) != 0){
                if((blackPawns & index) != 0){
                    score = score - PST.pawnPSTBlack[i];
                }else if((blackKnights & index) != 0){
                    score = score - PST.knightPSTBlack[i];
                }else if((blackBishops & index) != 0){
                    score = score - PST.bishopPSTBlack[i];
                }else if((blackRooks & index) != 0){
                    score = score - PST.rookPSTBlack[i];
                }else if((blackQueens & index) != 0){
                    score = score - PST.queenPSTBlack[i];
                }else if((blackKings & index) != 0){
                    score = score - PST.kingPSTBlack[i];
                }
            }
        }

        return score;
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
