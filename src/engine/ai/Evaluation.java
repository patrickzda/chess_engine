package engine.ai;

import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.*;
import engine.tools.PST;
import engine.tools.EvaluationParams;
import engine.tools.TranspositionTable;
import engine.tools.TranspositionTableEntry;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import static engine.representation.Color.*;

public class Evaluation {
    private static final int[] PIECE_VALUES = new int[]{60000, 930, 480, 320, 280, 100};
    private static final int PAWN_VALUE = 100, KNIGHT_VALUE = 280, BISHOP_VALUE = 320, ROOK_VALUE = 480, QUEEN_VALUE = 930, KING_VALUE = 60000;
    private static final int KNIGHT_PHASE_VALUE = 1, BISHOP_PHASE_VALUE = 1, ROOK_PHASE_VALUE = 2, QUEEN_PHASE_VALUE = 4;
    private static final int TOTAL_PHASE = KNIGHT_PHASE_VALUE * 4 + BISHOP_PHASE_VALUE * 4 + ROOK_PHASE_VALUE * 4 + QUEEN_PHASE_VALUE * 2;
    private static final int BAD_PAWN_STRUCTURE_PENALTY = -25;
    private static final int CHECKMATE_BONUS = KING_VALUE + 10 * QUEEN_VALUE;
    private static final long NOT_A_FILE = -72340172838076674L, NOT_H_FILE = 9187201950435737471L;

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

    //Gibt einen Wert zwischen 0,5 (Keine Figur wurde bewegt) und 256,5 zurück (nur zwei Könige übrig)
    public static double getGamePhase(Board board){
        double phase = TOTAL_PHASE;
        phase = phase - getSetBits(board.knights) * KNIGHT_PHASE_VALUE;
        phase = phase - getSetBits(board.bishops) * BISHOP_PHASE_VALUE;
        phase = phase - getSetBits(board.rooks) * ROOK_PHASE_VALUE;
        phase = phase - getSetBits(board.queens) * QUEEN_PHASE_VALUE;
        phase = (phase * 256 + ((double) TOTAL_PHASE / 2)) / TOTAL_PHASE;
        return phase;
    }

    public static int evaluateNegamaxNew(Board board, MoveMasks masks, Move[] currentTeamMoves){
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
        int openingScore = 0, endGameScore = 0, materialScoreMid = 0, materialScoreEnd = 0, pawnStructureScoreMid = 0, pawnStructureScoreEnd = 0;
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

        materialScoreMid = materialScoreMid + (getSetBits(whitePawns) - getSetBits(blackPawns)) * EvaluationParams.PAWN_VALUE_MID;
        materialScoreMid = materialScoreMid + (getSetBits(whiteKnights) - getSetBits(blackKnights)) * EvaluationParams.KNIGHT_VALUE_MID;
        materialScoreMid = materialScoreMid + (getSetBits(whiteBishops) - getSetBits(blackBishops)) * EvaluationParams.BISHOP_VALUE_MID;
        materialScoreMid = materialScoreMid + (getSetBits(whiteRooks) - getSetBits(blackRooks)) * EvaluationParams.ROOK_VALUE_MID;
        materialScoreMid = materialScoreMid + (getSetBits(whiteQueens) - getSetBits(blackQueens)) * EvaluationParams.QUEEN_VALUE_MID;
        materialScoreMid = materialScoreMid + (getSetBits(whiteKings) - getSetBits(blackKings)) * KING_VALUE;

        materialScoreEnd = materialScoreEnd + (getSetBits(whitePawns) - getSetBits(blackPawns)) * EvaluationParams.PAWN_VALUE_END;
        materialScoreEnd = materialScoreEnd + (getSetBits(whiteKnights) - getSetBits(blackKnights)) * EvaluationParams.KNIGHT_VALUE_END;
        materialScoreEnd = materialScoreEnd + (getSetBits(whiteBishops) - getSetBits(blackBishops)) * EvaluationParams.BISHOP_VALUE_END;
        materialScoreEnd = materialScoreEnd + (getSetBits(whiteRooks) - getSetBits(blackRooks)) * EvaluationParams.ROOK_VALUE_END;
        materialScoreEnd = materialScoreEnd + (getSetBits(whiteQueens) - getSetBits(blackQueens)) * EvaluationParams.QUEEN_VALUE_END;
        materialScoreEnd = materialScoreEnd + (getSetBits(whiteKings) - getSetBits(blackKings)) * KING_VALUE;

        int isolatedPawnCount = getIsolatedPawnCount(board, WHITE) - getIsolatedPawnCount(board, BLACK);
        int doubledPawnCount = getDoubledPawnCount(board, WHITE) - getDoubledPawnCount(board, BLACK);
        int blockedPawnCount = getBlockedPawnCount(board, WHITE) - getBlockedPawnCount(board, BLACK);

        pawnStructureScoreMid = pawnStructureScoreMid + isolatedPawnCount * EvaluationParams.ISOLATED_PAWN_PENALTY_MID;
        pawnStructureScoreMid = pawnStructureScoreMid + doubledPawnCount * EvaluationParams.DOUBLED_PAWN_PENALTY_MID;
        pawnStructureScoreMid = pawnStructureScoreMid + blockedPawnCount * EvaluationParams.BLOCKED_PAWN_PENALTY_MID;

        pawnStructureScoreEnd = pawnStructureScoreEnd + isolatedPawnCount * EvaluationParams.ISOLATED_PAWN_PENALTY_END;
        pawnStructureScoreEnd = pawnStructureScoreEnd + doubledPawnCount *  EvaluationParams.DOUBLED_PAWN_PENALTY_END;
        pawnStructureScoreEnd = pawnStructureScoreEnd + blockedPawnCount *  EvaluationParams.BLOCKED_PAWN_PENALTY_END;

        for(int i = 0; i < 64; i++){
            long index = 1L << i;
            if((board.whitePieces & index) != 0){
                if((whitePawns & index) != 0){
                    openingScore = openingScore + EvaluationParams.PAWN_PST_MID[i];
                    endGameScore = endGameScore + EvaluationParams.PAWN_PST_END[i];
                }else if((whiteKnights & index) != 0){
                    openingScore = openingScore + EvaluationParams.KNIGHT_PST_MID[i];
                    endGameScore = endGameScore + EvaluationParams.KNIGHT_PST_END[i];
                }else if((whiteBishops & index) != 0){
                    openingScore = openingScore + EvaluationParams.BISHOP_PST_MID[i];
                    endGameScore = endGameScore + EvaluationParams.BISHOP_PST_END[i];
                }else if((whiteRooks & index) != 0){
                    openingScore = openingScore + EvaluationParams.ROOK_PST_MID[i];
                    endGameScore = endGameScore + EvaluationParams.ROOK_PST_END[i];
                }else if((whiteQueens & index) != 0){
                    openingScore = openingScore + EvaluationParams.QUEEN_PST_MID[i];
                    endGameScore = endGameScore + EvaluationParams.QUEEN_PST_END[i];
                }else if((whiteKings & index) != 0){
                    openingScore = openingScore + EvaluationParams.KING_PST_MID[i];
                    endGameScore = endGameScore + EvaluationParams.KING_PST_END[i];
                }
            }else if((board.blackPieces & index) != 0){
                int mirroredIndex = EvaluationParams.getMirroredIndex(i);
                if((blackPawns & index) != 0){
                    openingScore = openingScore - EvaluationParams.PAWN_PST_MID[mirroredIndex];
                    endGameScore = endGameScore - EvaluationParams.PAWN_PST_END[mirroredIndex];
                }else if((blackKnights & index) != 0){
                    openingScore = openingScore - EvaluationParams.KNIGHT_PST_MID[mirroredIndex];
                    endGameScore = endGameScore - EvaluationParams.KNIGHT_PST_END[mirroredIndex];
                }else if((blackBishops & index) != 0){
                    openingScore = openingScore - EvaluationParams.BISHOP_PST_MID[mirroredIndex];
                    endGameScore = endGameScore - EvaluationParams.BISHOP_PST_END[mirroredIndex];
                }else if((blackRooks & index) != 0){
                    openingScore = openingScore - EvaluationParams.ROOK_PST_MID[mirroredIndex];
                    endGameScore = endGameScore - EvaluationParams.ROOK_PST_END[mirroredIndex];
                }else if((blackQueens & index) != 0){
                    openingScore = openingScore - EvaluationParams.QUEEN_PST_MID[mirroredIndex];
                    endGameScore = endGameScore - EvaluationParams.QUEEN_PST_END[mirroredIndex];
                }else if((blackKings & index) != 0){
                    openingScore = openingScore - EvaluationParams.KING_PST_MID[mirroredIndex];
                    endGameScore = endGameScore - EvaluationParams.KING_PST_END[mirroredIndex];
                }
            }
        }

        board.doNullMove();
        Move[] enemyTeamMoves = MoveGenerator.generateLegalMoves(board, masks);
        board.doNullMove();

        if(getSetBits(board.whitePieces & board.bishops) == 2){
            openingScore = openingScore + EvaluationParams.BISHOP_PAIR_BONUS_MID;
            endGameScore = endGameScore + EvaluationParams.BISHOP_PAIR_BONUS_END;
        }

        if(getSetBits(board.blackPieces & board.bishops) == 2){
            openingScore = openingScore - EvaluationParams.BISHOP_PAIR_BONUS_MID;
            endGameScore = endGameScore - EvaluationParams.BISHOP_PAIR_BONUS_END;
        }

        int[] mobilityBonus = calculateMobilityBonus(board, currentTeamMoves, enemyTeamMoves);
        openingScore = openingScore + mobilityBonus[0];
        endGameScore = endGameScore + mobilityBonus[1];

        int[] outpostBonus = calculateOutpostBonus(board, masks);
        openingScore = openingScore + outpostBonus[0];
        endGameScore = endGameScore + outpostBonus[1];

        //Testen
        //int[] kingMobilityBonus = calculateKingMobilityBonus(board, currentTeamMoves, enemyTeamMoves);
        //openingScore = openingScore + kingMobilityBonus[0];
        //endGameScore = endGameScore + kingMobilityBonus[1];

        openingScore = openingScore + materialScoreMid;
        endGameScore = endGameScore + materialScoreEnd;

        openingScore = openingScore + pawnStructureScoreMid;
        endGameScore = endGameScore + pawnStructureScoreEnd;

        return (int) Math.round(((openingScore * (256 - phase)) + (endGameScore * phase)) / 256);
    }

    public static void sortMoves(TranspositionTable table, Board board, Move[] moves){
        TranspositionTableEntry entry = table.lookup(board);

        if(entry != null){
            Move bestSavedMove = entry.getBestMove();
            for(int i = 0; i < moves.length; i++){
                if(bestSavedMove != null && bestSavedMove.getStartFieldIndex() == moves[i].getStartFieldIndex() && bestSavedMove.getEndFieldIndex() == moves[i].getEndFieldIndex()){
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

    private static int getKingDistanceToCenter(Board board, Color color){
        int kingIndex;
        if(color == WHITE){
            kingIndex = Long.numberOfTrailingZeros(board.whitePieces & board.kings);
        }else{
            kingIndex = Long.numberOfTrailingZeros(board.blackPieces & board.kings);
        }
        return EvaluationParams.SINGLE_MOVES_TO_CENTER_COUNT[kingIndex];
    }

    private static int[] calculateKingMobilityBonus(Board board, Move[] currentTeamMoves, Move[] enemyTeamMoves){
        int currenTeamBonusMid = 0, currenTeamBonusEnd = 0, enemyTeamBonusMid = 0, enemyTeamBonusEnd = 0;
        for(int i = 0; i < currentTeamMoves.length; i++){
            if(currentTeamMoves[i].getPieceType() == PieceType.KING){
                currenTeamBonusMid = currenTeamBonusMid + EvaluationParams.KING_MOBILITY_BONUS_MID;
                currenTeamBonusEnd = currenTeamBonusEnd + EvaluationParams.KING_MOBILITY_BONUS_END;
            }
        }

        for(int i = 0; i < enemyTeamMoves.length; i++){
            if(enemyTeamMoves[i].getPieceType() == PieceType.KING){
                enemyTeamBonusMid = enemyTeamBonusMid + EvaluationParams.KING_MOBILITY_BONUS_MID;
                enemyTeamBonusEnd = enemyTeamBonusEnd + EvaluationParams.KING_MOBILITY_BONUS_END;
            }
        }

        if(board.getTurn() == WHITE){
            return new int[]{currenTeamBonusMid - enemyTeamBonusMid, currenTeamBonusEnd - enemyTeamBonusEnd};
        }else{
            return new int[]{enemyTeamBonusMid - currenTeamBonusMid, enemyTeamBonusEnd - currenTeamBonusEnd};
        }
    }

    private static int[] calculateMobilityBonus(Board board, Move[] currentTeamMoves, Move[] enemyTeamMoves){
        int mobilityBonusMid = 0, mobilityBonusEnd = 0;
        if(board.getTurn() == WHITE){
            ArrayList<Integer> knightPositionsWhite = MoveGenerator.getSetBitIndices(board.knights & board.whitePieces);
            ArrayList<Integer> bishopPositionsWhite = MoveGenerator.getSetBitIndices(board.bishops & board.whitePieces);
            ArrayList<Integer> rookPositionsWhite = MoveGenerator.getSetBitIndices(board.rooks & board.whitePieces);
            ArrayList<Integer> queenPositionsWhite = MoveGenerator.getSetBitIndices(board.queens & board.whitePieces);

            int[] knightMobilitiesWhite = new int[knightPositionsWhite.size()], bishopMobilitiesWhite = new int[bishopPositionsWhite.size()], rookMobilitiesWhite = new int[rookPositionsWhite.size()], queenMobilitiesWhite = new int[queenPositionsWhite.size()];
            for(int i = 0; i < currentTeamMoves.length; i++){
                PieceType currentPiece = currentTeamMoves[i].getPieceType();
                if(currentPiece == PieceType.KNIGHT){
                    knightMobilitiesWhite[knightPositionsWhite.indexOf(currentTeamMoves[i].getStartFieldIndex())]++;
                }else if(currentPiece == PieceType.BISHOP){
                    bishopMobilitiesWhite[bishopPositionsWhite.indexOf(currentTeamMoves[i].getStartFieldIndex())]++;
                }else if(currentPiece == PieceType.ROOK){
                    rookMobilitiesWhite[rookPositionsWhite.indexOf(currentTeamMoves[i].getStartFieldIndex())]++;
                }else if(currentPiece == PieceType.QUEEN){
                    queenMobilitiesWhite[queenPositionsWhite.indexOf(currentTeamMoves[i].getStartFieldIndex())]++;
                }
            }

            for(int i = 0; i < knightMobilitiesWhite.length; i++){
                mobilityBonusMid = mobilityBonusMid + EvaluationParams.KNIGHT_MOBILITY_BONUS_MID[knightMobilitiesWhite[i]];
                mobilityBonusEnd = mobilityBonusEnd + EvaluationParams.KNIGHT_MOBILITY_BONUS_END[knightMobilitiesWhite[i]];
            }

            for(int i = 0; i < bishopMobilitiesWhite.length; i++){
                mobilityBonusMid = mobilityBonusMid + EvaluationParams.BISHOP_MOBILITY_BONUS_MID[bishopMobilitiesWhite[i]];
                mobilityBonusEnd = mobilityBonusEnd + EvaluationParams.BISHOP_MOBILITY_BONUS_END[bishopMobilitiesWhite[i]];
            }

            for(int i = 0; i < rookMobilitiesWhite.length; i++){
                mobilityBonusMid = mobilityBonusMid + EvaluationParams.ROOK_MOBILITY_BONUS_MID[rookMobilitiesWhite[i]];
                mobilityBonusEnd = mobilityBonusEnd + EvaluationParams.ROOK_MOBILITY_BONUS_END[rookMobilitiesWhite[i]];
            }

            for(int i = 0; i < queenMobilitiesWhite.length; i++){
                mobilityBonusMid = mobilityBonusMid + EvaluationParams.QUEEN_MOBILITY_BONUS_MID[queenMobilitiesWhite[i]];
                mobilityBonusEnd = mobilityBonusEnd + EvaluationParams.QUEEN_MOBILITY_BONUS_END[queenMobilitiesWhite[i]];
            }

            ArrayList<Integer> knightPositionsBlack = MoveGenerator.getSetBitIndices(board.knights & board.blackPieces);
            ArrayList<Integer> bishopPositionsBlack = MoveGenerator.getSetBitIndices(board.bishops & board.blackPieces);
            ArrayList<Integer> rookPositionsBlack = MoveGenerator.getSetBitIndices(board.rooks & board.blackPieces);
            ArrayList<Integer> queenPositionsBlack = MoveGenerator.getSetBitIndices(board.queens & board.blackPieces);

            int[] knightMobilitiesBlack = new int[knightPositionsBlack.size()], bishopMobilitiesBlack = new int[bishopPositionsBlack.size()], rookMobilitiesBlack = new int[rookPositionsBlack.size()], queenMobilitiesBlack = new int[queenPositionsBlack.size()];

            for(int i = 0; i < enemyTeamMoves.length; i++){
                PieceType currentPiece = enemyTeamMoves[i].getPieceType();
                if(currentPiece == PieceType.KNIGHT){
                    knightMobilitiesBlack[knightPositionsBlack.indexOf(enemyTeamMoves[i].getStartFieldIndex())]++;
                }else if(currentPiece == PieceType.BISHOP){
                    bishopMobilitiesBlack[bishopPositionsBlack.indexOf(enemyTeamMoves[i].getStartFieldIndex())]++;
                }else if(currentPiece == PieceType.ROOK){
                    rookMobilitiesBlack[rookPositionsBlack.indexOf(enemyTeamMoves[i].getStartFieldIndex())]++;
                }else if(currentPiece == PieceType.QUEEN){
                    queenMobilitiesBlack[queenPositionsBlack.indexOf(enemyTeamMoves[i].getStartFieldIndex())]++;
                }
            }

            for(int i = 0; i < knightMobilitiesBlack.length; i++){
                mobilityBonusMid = mobilityBonusMid - EvaluationParams.KNIGHT_MOBILITY_BONUS_MID[knightMobilitiesBlack[i]];
                mobilityBonusEnd = mobilityBonusEnd - EvaluationParams.KNIGHT_MOBILITY_BONUS_END[knightMobilitiesBlack[i]];
            }

            for(int i = 0; i < bishopMobilitiesBlack.length; i++){
                mobilityBonusMid = mobilityBonusMid - EvaluationParams.BISHOP_MOBILITY_BONUS_MID[bishopMobilitiesBlack[i]];
                mobilityBonusEnd = mobilityBonusEnd - EvaluationParams.BISHOP_MOBILITY_BONUS_END[bishopMobilitiesBlack[i]];
            }

            for(int i = 0; i < rookMobilitiesBlack.length; i++){
                mobilityBonusMid = mobilityBonusMid - EvaluationParams.ROOK_MOBILITY_BONUS_MID[rookMobilitiesBlack[i]];
                mobilityBonusEnd = mobilityBonusEnd - EvaluationParams.ROOK_MOBILITY_BONUS_END[rookMobilitiesBlack[i]];
            }

            for(int i = 0; i < queenMobilitiesBlack.length; i++){
                mobilityBonusMid = mobilityBonusMid - EvaluationParams.QUEEN_MOBILITY_BONUS_MID[queenMobilitiesBlack[i]];
                mobilityBonusEnd = mobilityBonusEnd - EvaluationParams.QUEEN_MOBILITY_BONUS_END[queenMobilitiesBlack[i]];
            }
        }else{
            ArrayList<Integer> knightPositionsBlack = MoveGenerator.getSetBitIndices(board.knights & board.blackPieces);
            ArrayList<Integer> bishopPositionsBlack = MoveGenerator.getSetBitIndices(board.bishops & board.blackPieces);
            ArrayList<Integer> rookPositionsBlack = MoveGenerator.getSetBitIndices(board.rooks & board.blackPieces);
            ArrayList<Integer> queenPositionsBlack = MoveGenerator.getSetBitIndices(board.queens & board.blackPieces);

            int[] knightMobilitiesBlack = new int[knightPositionsBlack.size()], bishopMobilitiesBlack = new int[bishopPositionsBlack.size()], rookMobilitiesBlack = new int[rookPositionsBlack.size()], queenMobilitiesBlack = new int[queenPositionsBlack.size()];

            for(int i = 0; i < currentTeamMoves.length; i++){
                PieceType currentPiece = currentTeamMoves[i].getPieceType();
                if(currentPiece == PieceType.KNIGHT){
                    knightMobilitiesBlack[knightPositionsBlack.indexOf(currentTeamMoves[i].getStartFieldIndex())]++;
                }else if(currentPiece == PieceType.BISHOP){
                    bishopMobilitiesBlack[bishopPositionsBlack.indexOf(currentTeamMoves[i].getStartFieldIndex())]++;
                }else if(currentPiece == PieceType.ROOK){
                    rookMobilitiesBlack[rookPositionsBlack.indexOf(currentTeamMoves[i].getStartFieldIndex())]++;
                }else if(currentPiece == PieceType.QUEEN){
                    queenMobilitiesBlack[queenPositionsBlack.indexOf(currentTeamMoves[i].getStartFieldIndex())]++;
                }
            }

            for(int i = 0; i < knightMobilitiesBlack.length; i++){
                mobilityBonusMid = mobilityBonusMid - EvaluationParams.KNIGHT_MOBILITY_BONUS_MID[knightMobilitiesBlack[i]];
                mobilityBonusEnd = mobilityBonusEnd - EvaluationParams.KNIGHT_MOBILITY_BONUS_END[knightMobilitiesBlack[i]];
            }

            for(int i = 0; i < bishopMobilitiesBlack.length; i++){
                mobilityBonusMid = mobilityBonusMid - EvaluationParams.BISHOP_MOBILITY_BONUS_MID[bishopMobilitiesBlack[i]];
                mobilityBonusEnd = mobilityBonusEnd - EvaluationParams.BISHOP_MOBILITY_BONUS_END[bishopMobilitiesBlack[i]];
            }

            for(int i = 0; i < rookMobilitiesBlack.length; i++){
                mobilityBonusMid = mobilityBonusMid - EvaluationParams.ROOK_MOBILITY_BONUS_MID[rookMobilitiesBlack[i]];
                mobilityBonusEnd = mobilityBonusEnd - EvaluationParams.ROOK_MOBILITY_BONUS_END[rookMobilitiesBlack[i]];
            }

            for(int i = 0; i < queenMobilitiesBlack.length; i++){
                mobilityBonusMid = mobilityBonusMid - EvaluationParams.QUEEN_MOBILITY_BONUS_MID[queenMobilitiesBlack[i]];
                mobilityBonusEnd = mobilityBonusEnd - EvaluationParams.QUEEN_MOBILITY_BONUS_END[queenMobilitiesBlack[i]];
            }

            ArrayList<Integer> knightPositionsWhite = MoveGenerator.getSetBitIndices(board.knights & board.whitePieces);
            ArrayList<Integer> bishopPositionsWhite = MoveGenerator.getSetBitIndices(board.bishops & board.whitePieces);
            ArrayList<Integer> rookPositionsWhite = MoveGenerator.getSetBitIndices(board.rooks & board.whitePieces);
            ArrayList<Integer> queenPositionsWhite = MoveGenerator.getSetBitIndices(board.queens & board.whitePieces);

            int[] knightMobilitiesWhite = new int[knightPositionsWhite.size()], bishopMobilitiesWhite = new int[bishopPositionsWhite.size()], rookMobilitiesWhite = new int[rookPositionsWhite.size()], queenMobilitiesWhite = new int[queenPositionsWhite.size()];

            for(int i = 0; i < enemyTeamMoves.length; i++){
                PieceType currentPiece = enemyTeamMoves[i].getPieceType();
                if(currentPiece == PieceType.KNIGHT){
                    knightMobilitiesWhite[knightPositionsWhite.indexOf(enemyTeamMoves[i].getStartFieldIndex())]++;
                }else if(currentPiece == PieceType.BISHOP){
                    bishopMobilitiesWhite[bishopPositionsWhite.indexOf(enemyTeamMoves[i].getStartFieldIndex())]++;
                }else if(currentPiece == PieceType.ROOK){
                    rookMobilitiesWhite[rookPositionsWhite.indexOf(enemyTeamMoves[i].getStartFieldIndex())]++;
                }else if(currentPiece == PieceType.QUEEN){
                    queenMobilitiesWhite[queenPositionsWhite.indexOf(enemyTeamMoves[i].getStartFieldIndex())]++;
                }
            }

            for(int i = 0; i < knightMobilitiesWhite.length; i++){
                mobilityBonusMid = mobilityBonusMid + EvaluationParams.KNIGHT_MOBILITY_BONUS_MID[knightMobilitiesWhite[i]];
                mobilityBonusEnd = mobilityBonusEnd + EvaluationParams.KNIGHT_MOBILITY_BONUS_END[knightMobilitiesWhite[i]];
            }

            for(int i = 0; i < bishopMobilitiesWhite.length; i++){
                mobilityBonusMid = mobilityBonusMid + EvaluationParams.BISHOP_MOBILITY_BONUS_MID[bishopMobilitiesWhite[i]];
                mobilityBonusEnd = mobilityBonusEnd + EvaluationParams.BISHOP_MOBILITY_BONUS_END[bishopMobilitiesWhite[i]];
            }

            for(int i = 0; i < rookMobilitiesWhite.length; i++){
                mobilityBonusMid = mobilityBonusMid + EvaluationParams.ROOK_MOBILITY_BONUS_MID[rookMobilitiesWhite[i]];
                mobilityBonusEnd = mobilityBonusEnd + EvaluationParams.ROOK_MOBILITY_BONUS_END[rookMobilitiesWhite[i]];
            }

            for(int i = 0; i < queenMobilitiesWhite.length; i++){
                mobilityBonusMid = mobilityBonusMid + EvaluationParams.QUEEN_MOBILITY_BONUS_MID[queenMobilitiesWhite[i]];
                mobilityBonusEnd = mobilityBonusEnd + EvaluationParams.QUEEN_MOBILITY_BONUS_END[queenMobilitiesWhite[i]];
            }
        }

        return new int[]{mobilityBonusMid, mobilityBonusEnd};
    }

    private static int[] calculateOutpostBonus(Board board, MoveMasks masks){
        ArrayList<Integer> whiteKnightIndices = MoveGenerator.getSetBitIndices(board.knights & board.whitePieces);
        ArrayList<Integer> whiteBishopIndices = MoveGenerator.getSetBitIndices(board.bishops & board.whitePieces);
        ArrayList<Integer> blackKnightIndices = MoveGenerator.getSetBitIndices(board.knights & board.blackPieces);
        ArrayList<Integer> blackBishopIndices = MoveGenerator.getSetBitIndices(board.bishops & board.blackPieces);

        int outpostBonusMid = 0, outpostBonusEnd = 0;

        for(int i = 0; i < whiteKnightIndices.size(); i++){
            int index = whiteKnightIndices.get(i);
            if(index >= 24 && index <= 47){
                if((((1L << (index - 7)) & board.pawns & board.whitePieces & NOT_A_FILE) != 0) || (((1L << (index - 9)) & board.pawns & board.whitePieces & NOT_H_FILE) != 0)){
                    if((masks.rays(Direction.NORTH, index - 1) & NOT_H_FILE & board.pawns & board.blackPieces) == 0 && (masks.rays(Direction.NORTH, index + 1) & NOT_A_FILE & board.pawns & board.blackPieces) == 0){
                        outpostBonusMid  = outpostBonusMid + EvaluationParams.KNIGHT_OUTPOST_BONUS_MID;
                        outpostBonusEnd  = outpostBonusEnd + EvaluationParams.KNIGHT_OUTPOST_BONUS_END;
                    }
                }
            }
        }

        for(int i = 0; i < whiteBishopIndices.size(); i++){
            int index = whiteBishopIndices.get(i);
            if(index >= 24 && index <= 47){
                if((((1L << (index - 7)) & board.pawns & board.whitePieces & NOT_A_FILE) != 0) || (((1L << (index - 9)) & board.pawns & board.whitePieces & NOT_H_FILE) != 0)){
                    if((masks.rays(Direction.NORTH, index - 1) & NOT_H_FILE & board.pawns & board.blackPieces) == 0 && (masks.rays(Direction.NORTH, index + 1) & NOT_A_FILE & board.pawns & board.blackPieces) == 0){
                        outpostBonusMid  = outpostBonusMid + EvaluationParams.BISHOP_OUTPOST_BONUS_MID;
                        outpostBonusEnd  = outpostBonusEnd + EvaluationParams.BISHOP_OUTPOST_BONUS_END;
                    }
                }
            }
        }

        for(int i = 0; i < blackKnightIndices.size(); i++){
            int index = blackKnightIndices.get(i);
            if(index >= 16 && index <= 39){
                if((((1L << (index + 7)) & board.pawns & board.blackPieces & NOT_H_FILE) != 0) || (((1L << (index + 9)) & board.pawns & board.blackPieces & NOT_A_FILE) != 0)){
                    if((masks.rays(Direction.SOUTH, index - 1) & NOT_H_FILE & board.pawns & board.whitePieces) == 0 && (masks.rays(Direction.SOUTH, index + 1) & NOT_A_FILE & board.pawns & board.whitePieces) == 0){
                        outpostBonusMid  = outpostBonusMid - EvaluationParams.KNIGHT_OUTPOST_BONUS_MID;
                        outpostBonusEnd  = outpostBonusEnd - EvaluationParams.KNIGHT_OUTPOST_BONUS_END;
                    }
                }
            }
        }

        for(int i = 0; i < blackBishopIndices.size(); i++){
            int index = blackBishopIndices.get(i);
            if(index >= 16 && index <= 39){
                if((((1L << (index + 7)) & board.pawns & board.blackPieces & NOT_H_FILE) != 0) || (((1L << (index + 9)) & board.pawns & board.blackPieces & NOT_A_FILE) != 0)){
                    if((masks.rays(Direction.SOUTH, index - 1) & NOT_H_FILE & board.pawns & board.whitePieces) == 0 && (masks.rays(Direction.SOUTH, index + 1) & NOT_A_FILE & board.pawns & board.whitePieces) == 0){
                        outpostBonusMid  = outpostBonusMid - EvaluationParams.BISHOP_OUTPOST_BONUS_MID;
                        outpostBonusEnd  = outpostBonusEnd - EvaluationParams.BISHOP_OUTPOST_BONUS_END;
                    }
                }
            }
        }

        return new int[]{outpostBonusMid, outpostBonusEnd};
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
