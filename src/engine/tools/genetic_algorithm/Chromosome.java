package engine.tools.genetic_algorithm;

import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.*;
import engine.tools.EvaluationParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static engine.ai.Evaluation.getGamePhase;
import static engine.ai.Evaluation.getSetBits;
import static engine.representation.Color.BLACK;
import static engine.representation.Color.WHITE;

public class Chromosome {
    private static final double CROSSOVER_RATE = 0.25;
    private static final double MUTATION_RATE = 0.007;
    private static final int CHECKMATE_BONUS = 60000 + 10 * 930, KING_VALUE = 60000;
    private static final long NOT_A_FILE = -72340172838076674L, NOT_H_FILE = 9187201950435737471L;

    /*
    Struktur von params:
    Parameter                           Startindex
    PAWN_VALUE_MID                      0
    KNIGHT_VALUE_MID                    1
    BISHOP_VALUE_MID                    2
    ROOK_VALUE_MID                      3
    QUEEN_VALUE_MID                     4
    PAWN_VALUE_END                      5
    KNIGHT_VALUE_END                    6
    BISHOP_VALUE_END                    7
    ROOK_VALUE_END                      8
    QUEEN_VALUE_END                     9

    PAWN_PST_MID                        10
    PAWN_PST_END                        74
    KNIGHT_PST_MID                      138
    KNIGHT_PST_END                      202
    BISHOP_PST_MID                      266
    BISHOP_PST_END                      330
    ROOK_PST_MID                        394
    ROOK_PST_END                        458
    QUEEN_PST_MID                       522
    QUEEN_PST_END                       586
    KING_PST_MID                        650
    KING_PST_END                        714

    KNIGHT_MOBILITY_BONUS_MID           778
    KNIGHT_MOBILITY_BONUS_END           787
    BISHOP_MOBILITY_BONUS_MID           796
    BISHOP_MOBILITY_BONUS_END           810
    ROOK_MOBILITY_BONUS_MID             824
    ROOK_MOBILITY_BONUS_END             839
    QUEEN_MOBILITY_BONUS_MID            854
    QUEEN_MOBILITY_BONUS_END            882

    ISOLATED_PAWN_PENALTY_MID           910
    ISOLATED_PAWN_PENALTY_END           911
    DOUBLED_PAWN_PENALTY_MID            912
    DOUBLED_PAWN_PENALTY_END            913
    BLOCKED_PAWN_PENALTY_MID            914
    BLOCKED_PAWN_PENALTY_END            915

    KNIGHT_OUTPOST_BONUS_MID            916
    KNIGHT_OUTPOST_BONUS_END            917
    BISHOP_OUTPOST_BONUS_MID            918
    BISHOP_OUTPOST_BONUS_END            919

    BISHOP_PAIR_BONUS_MID               920
    BISHOP_PAIR_BONUS_END               921

    KING_MOBILITY_BONUS_MID             922
    KING_MOBILITY_BONUS_END             923
    MOVES_TO_CENTER_PENALTY_MID         924
    MOVES_TO_CENTER_PENALTY_END         925

    ROOK_ON_CLOSED_FILE_BONUS_MID       926
    ROOK_ON_CLOSED_FILE_BONUS_END       927
    ROOK_ON_SEMI_OPEN_FILE_BONUS_MID    928
    ROOK_ON_SEMI_OPEN_FILE_BONUS_END    929
    ROOK_ON_OPEN_FILE_BONUS_MID         930
    ROOK_ON_OPEN_FILE_BONUS_END         931

    ATTACKED_BY_KNIGHT_OR_BISHOP_MID    932
    ATTACKED_BY_KNIGHT_OR_BISHOP_END    937
    ATTACKED_BY_ROOK_MID                942
    ATTACKED_BY_ROOK_END                947
    */
    private Integer[] parameters;

    public Chromosome(){
        ArrayList<Integer> parameterList = new ArrayList<Integer>(952);
        parameters = new Integer[952];

        parameterList.add(EvaluationParams.PAWN_VALUE_MID);
        parameterList.add(EvaluationParams.KNIGHT_VALUE_MID);
        parameterList.add(EvaluationParams.BISHOP_VALUE_MID);
        parameterList.add(EvaluationParams.ROOK_VALUE_MID);
        parameterList.add(EvaluationParams.QUEEN_VALUE_MID);
        parameterList.add(EvaluationParams.PAWN_VALUE_END);
        parameterList.add(EvaluationParams.KNIGHT_VALUE_END);
        parameterList.add(EvaluationParams.BISHOP_VALUE_END);
        parameterList.add(EvaluationParams.ROOK_VALUE_END);
        parameterList.add(EvaluationParams.QUEEN_VALUE_END);

        parameterList.addAll(Arrays.stream(Arrays.copyOf(EvaluationParams.PAWN_PST_MID, 64)).boxed().toList());
        parameterList.addAll(Arrays.stream(Arrays.copyOf(EvaluationParams.PAWN_PST_END, 64)).boxed().toList());
        parameterList.addAll(Arrays.stream(Arrays.copyOf(EvaluationParams.KNIGHT_PST_MID, 64)).boxed().toList());
        parameterList.addAll(Arrays.stream(Arrays.copyOf(EvaluationParams.KNIGHT_PST_END, 64)).boxed().toList());
        parameterList.addAll(Arrays.stream(Arrays.copyOf(EvaluationParams.BISHOP_PST_MID, 64)).boxed().toList());
        parameterList.addAll(Arrays.stream(Arrays.copyOf(EvaluationParams.BISHOP_PST_END, 64)).boxed().toList());
        parameterList.addAll(Arrays.stream(Arrays.copyOf(EvaluationParams.ROOK_PST_MID, 64)).boxed().toList());
        parameterList.addAll(Arrays.stream(Arrays.copyOf(EvaluationParams.ROOK_PST_END, 64)).boxed().toList());
        parameterList.addAll(Arrays.stream(Arrays.copyOf(EvaluationParams.QUEEN_PST_MID, 64)).boxed().toList());
        parameterList.addAll(Arrays.stream(Arrays.copyOf(EvaluationParams.QUEEN_PST_END, 64)).boxed().toList());
        parameterList.addAll(Arrays.stream(Arrays.copyOf(EvaluationParams.KING_PST_MID, 64)).boxed().toList());
        parameterList.addAll(Arrays.stream(Arrays.copyOf(EvaluationParams.KING_PST_END, 64)).boxed().toList());

        parameterList.addAll(Arrays.stream(Arrays.copyOf(EvaluationParams.KNIGHT_MOBILITY_BONUS_MID, EvaluationParams.KNIGHT_MOBILITY_BONUS_MID.length)).boxed().toList());
        parameterList.addAll(Arrays.stream(Arrays.copyOf(EvaluationParams.KNIGHT_MOBILITY_BONUS_END, EvaluationParams.KNIGHT_MOBILITY_BONUS_END.length)).boxed().toList());
        parameterList.addAll(Arrays.stream(Arrays.copyOf(EvaluationParams.BISHOP_MOBILITY_BONUS_MID, EvaluationParams.BISHOP_MOBILITY_BONUS_MID.length)).boxed().toList());
        parameterList.addAll(Arrays.stream(Arrays.copyOf(EvaluationParams.BISHOP_MOBILITY_BONUS_END, EvaluationParams.BISHOP_MOBILITY_BONUS_END.length)).boxed().toList());
        parameterList.addAll(Arrays.stream(Arrays.copyOf(EvaluationParams.ROOK_MOBILITY_BONUS_MID, EvaluationParams.ROOK_MOBILITY_BONUS_MID.length)).boxed().toList());
        parameterList.addAll(Arrays.stream(Arrays.copyOf(EvaluationParams.ROOK_MOBILITY_BONUS_END, EvaluationParams.ROOK_MOBILITY_BONUS_END.length)).boxed().toList());
        parameterList.addAll(Arrays.stream(Arrays.copyOf(EvaluationParams.QUEEN_MOBILITY_BONUS_MID, EvaluationParams.QUEEN_MOBILITY_BONUS_MID.length)).boxed().toList());
        parameterList.addAll(Arrays.stream(Arrays.copyOf(EvaluationParams.QUEEN_MOBILITY_BONUS_END, EvaluationParams.QUEEN_MOBILITY_BONUS_END.length)).boxed().toList());

        parameterList.add(EvaluationParams.ISOLATED_PAWN_PENALTY_MID);
        parameterList.add(EvaluationParams.ISOLATED_PAWN_PENALTY_END);
        parameterList.add(EvaluationParams.DOUBLED_PAWN_PENALTY_MID);
        parameterList.add(EvaluationParams.DOUBLED_PAWN_PENALTY_END);
        parameterList.add(EvaluationParams.BLOCKED_PAWN_PENALTY_MID);
        parameterList.add(EvaluationParams.BLOCKED_PAWN_PENALTY_END);

        parameterList.add(EvaluationParams.KNIGHT_OUTPOST_BONUS_MID);
        parameterList.add(EvaluationParams.KNIGHT_OUTPOST_BONUS_END);
        parameterList.add(EvaluationParams.BISHOP_OUTPOST_BONUS_MID);
        parameterList.add(EvaluationParams.BISHOP_OUTPOST_BONUS_END);

        parameterList.add(EvaluationParams.BISHOP_PAIR_BONUS_MID);
        parameterList.add(EvaluationParams.BISHOP_PAIR_BONUS_END);

        parameterList.add(EvaluationParams.KING_MOBILITY_BONUS_MID);
        parameterList.add(EvaluationParams.KING_MOBILITY_BONUS_END);
        parameterList.add(EvaluationParams.MOVES_TO_CENTER_PENALTY_MID);
        parameterList.add(EvaluationParams.MOVES_TO_CENTER_PENALTY_END);

        parameterList.add(EvaluationParams.ROOK_ON_CLOSED_FILE_BONUS_MID);
        parameterList.add(EvaluationParams.ROOK_ON_CLOSED_FILE_BONUS_END);
        parameterList.add(EvaluationParams.ROOK_ON_SEMI_OPEN_FILE_BONUS_MID);
        parameterList.add(EvaluationParams.ROOK_ON_SEMI_OPEN_FILE_BONUS_END);
        parameterList.add(EvaluationParams.ROOK_ON_OPEN_FILE_BONUS_MID);
        parameterList.add(EvaluationParams.ROOK_ON_OPEN_FILE_BONUS_END);

        parameterList.addAll(Arrays.stream(Arrays.copyOf(EvaluationParams.ATTACKED_BY_KNIGHT_OR_BISHOP_MID, EvaluationParams.ATTACKED_BY_KNIGHT_OR_BISHOP_MID.length)).boxed().toList());
        parameterList.addAll(Arrays.stream(Arrays.copyOf(EvaluationParams.ATTACKED_BY_KNIGHT_OR_BISHOP_END, EvaluationParams.ATTACKED_BY_KNIGHT_OR_BISHOP_END.length)).boxed().toList());
        parameterList.addAll(Arrays.stream(Arrays.copyOf(EvaluationParams.ATTACKED_BY_ROOK_MID, EvaluationParams.ATTACKED_BY_ROOK_MID.length)).boxed().toList());
        parameterList.addAll(Arrays.stream(Arrays.copyOf(EvaluationParams.ATTACKED_BY_ROOK_END, EvaluationParams.ATTACKED_BY_ROOK_END.length)).boxed().toList());

        parameterList.toArray(parameters);
    }

    public int evaluate(Board board, MoveMasks masks, Move[] currentTeamMoves){
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

        //Score für das Material
        materialScoreMid = materialScoreMid + (getSetBits(whitePawns) - getSetBits(blackPawns)) * parameters[0];
        materialScoreMid = materialScoreMid + (getSetBits(whiteKnights) - getSetBits(blackKnights)) * parameters[1];
        materialScoreMid = materialScoreMid + (getSetBits(whiteBishops) - getSetBits(blackBishops)) * parameters[2];
        materialScoreMid = materialScoreMid + (getSetBits(whiteRooks) - getSetBits(blackRooks)) * parameters[3];
        materialScoreMid = materialScoreMid + (getSetBits(whiteQueens) - getSetBits(blackQueens)) * parameters[4];
        materialScoreMid = materialScoreMid + (getSetBits(whiteKings) - getSetBits(blackKings)) * KING_VALUE;

        materialScoreEnd = materialScoreEnd + (getSetBits(whitePawns) - getSetBits(blackPawns)) * parameters[5];
        materialScoreEnd = materialScoreEnd + (getSetBits(whiteKnights) - getSetBits(blackKnights)) * parameters[6];
        materialScoreEnd = materialScoreEnd + (getSetBits(whiteBishops) - getSetBits(blackBishops)) * parameters[7];
        materialScoreEnd = materialScoreEnd + (getSetBits(whiteRooks) - getSetBits(blackRooks)) * parameters[8];
        materialScoreEnd = materialScoreEnd + (getSetBits(whiteQueens) - getSetBits(blackQueens)) * parameters[9];
        materialScoreEnd = materialScoreEnd + (getSetBits(whiteKings) - getSetBits(blackKings)) * KING_VALUE;

        //Abzüge für schlechte Bauernstrukturen
        int isolatedPawnCount = getIsolatedPawnCount(board, WHITE) - getIsolatedPawnCount(board, BLACK);
        int doubledPawnCount = getDoubledPawnCount(board, WHITE) - getDoubledPawnCount(board, BLACK);
        int blockedPawnCount = getBlockedPawnCount(board, WHITE) - getBlockedPawnCount(board, BLACK);

        pawnStructureScoreMid = pawnStructureScoreMid + isolatedPawnCount * parameters[910];
        pawnStructureScoreMid = pawnStructureScoreMid + doubledPawnCount * parameters[912];
        pawnStructureScoreMid = pawnStructureScoreMid + blockedPawnCount * parameters[914];

        pawnStructureScoreEnd = pawnStructureScoreEnd + isolatedPawnCount * parameters[911];
        pawnStructureScoreEnd = pawnStructureScoreEnd + doubledPawnCount *  parameters[913];
        pawnStructureScoreEnd = pawnStructureScoreEnd + blockedPawnCount *  parameters[915];

        //Boni der PST
        for(int i = 0; i < 64; i++){
            long index = 1L << i;
            if((board.whitePieces & index) != 0){
                if((whitePawns & index) != 0){
                    openingScore = openingScore + parameters[10 + i];
                    endGameScore = endGameScore + parameters[74 + i];
                }else if((whiteKnights & index) != 0){
                    openingScore = openingScore + parameters[138 + i];
                    endGameScore = endGameScore + parameters[202 + i];
                }else if((whiteBishops & index) != 0){
                    openingScore = openingScore + parameters[266 + i];
                    endGameScore = endGameScore + parameters[330 + i];
                }else if((whiteRooks & index) != 0){
                    openingScore = openingScore + parameters[394 + i];
                    endGameScore = endGameScore + parameters[458 + i];
                }else if((whiteQueens & index) != 0){
                    openingScore = openingScore + parameters[522 + i];
                    endGameScore = endGameScore + parameters[586 + i];
                }else if((whiteKings & index) != 0){
                    openingScore = openingScore + parameters[650 + i];
                    endGameScore = endGameScore + parameters[714 + i];
                }
            }else if((board.blackPieces & index) != 0){
                int mirroredIndex = EvaluationParams.getMirroredIndex(i);
                if((blackPawns & index) != 0){
                    openingScore = openingScore - parameters[10 + mirroredIndex];
                    endGameScore = endGameScore - parameters[74 + mirroredIndex];
                }else if((blackKnights & index) != 0){
                    openingScore = openingScore - parameters[138 + mirroredIndex];
                    endGameScore = endGameScore - parameters[202 + mirroredIndex];
                }else if((blackBishops & index) != 0){
                    openingScore = openingScore - parameters[266 + mirroredIndex];
                    endGameScore = endGameScore - parameters[330 + mirroredIndex];
                }else if((blackRooks & index) != 0){
                    openingScore = openingScore - parameters[394 + mirroredIndex];
                    endGameScore = endGameScore - parameters[458 + mirroredIndex];
                }else if((blackQueens & index) != 0){
                    openingScore = openingScore - parameters[522 + mirroredIndex];
                    endGameScore = endGameScore - parameters[586 + mirroredIndex];
                }else if((blackKings & index) != 0){
                    openingScore = openingScore - parameters[650 + mirroredIndex];
                    endGameScore = endGameScore - parameters[714 + mirroredIndex];
                }
            }
        }

        board.doNullMove();
        Move[] enemyTeamMoves = MoveGenerator.generateLegalMoves(board, masks);
        board.doNullMove();

        //Boni für das Läuferpaar
        if(getSetBits(board.whitePieces & board.bishops) == 2){
            openingScore = openingScore + parameters[920];
            endGameScore = endGameScore + parameters[921];
        }

        if(getSetBits(board.blackPieces & board.bishops) == 2){
            openingScore = openingScore - parameters[920];
            endGameScore = endGameScore - parameters[921];
        }

        //Boni für Mobilität
        int[] mobilityBonus = calculateMobilityBonus(board, currentTeamMoves, enemyTeamMoves);
        openingScore = openingScore + mobilityBonus[0];
        endGameScore = endGameScore + mobilityBonus[1];

        //Boni für Outposts
        int[] outpostBonus = calculateOutpostBonus(board, masks);
        openingScore = openingScore + outpostBonus[0];
        endGameScore = endGameScore + outpostBonus[1];

        //Boni für Königsmobilität
        int[] kingMobilityBonus = calculateKingMobilityBonus(board, currentTeamMoves, enemyTeamMoves);
        openingScore = openingScore + kingMobilityBonus[0];
        endGameScore = endGameScore + kingMobilityBonus[1];

        //Boni, wenn der König in der Nähe des Zentrums ist
        int whiteKingDistanceToCenter = getKingDistanceToCenter(board, WHITE), blackKingDistanceToCenter = getKingDistanceToCenter(board, BLACK);
        openingScore = openingScore + (whiteKingDistanceToCenter - blackKingDistanceToCenter) * parameters[924];
        endGameScore = endGameScore + (whiteKingDistanceToCenter - blackKingDistanceToCenter) * parameters[925];

        //Boni für Türme auf offenen Linien
        int[] whiteRooksOnOpenFiles = getRookOnOpenFilesCount(board, masks, WHITE), blackRooksOnOpenFiles = getRookOnOpenFilesCount(board, masks, BLACK);
        openingScore = openingScore + (whiteRooksOnOpenFiles[0] - blackRooksOnOpenFiles[0]) * parameters[930];
        openingScore = openingScore + (whiteRooksOnOpenFiles[1] - blackRooksOnOpenFiles[1]) * parameters[928];
        openingScore = openingScore + (whiteRooksOnOpenFiles[2] - blackRooksOnOpenFiles[2]) * parameters[926];
        endGameScore = endGameScore + (whiteRooksOnOpenFiles[0] - blackRooksOnOpenFiles[0]) * parameters[931];
        endGameScore = endGameScore + (whiteRooksOnOpenFiles[1] - blackRooksOnOpenFiles[1]) * parameters[929];
        endGameScore = endGameScore + (whiteRooksOnOpenFiles[2] - blackRooksOnOpenFiles[2]) * parameters[927];

        //Boni für angegriffene Figuren
        if(board.getTurn() == WHITE){
            int[] attackedPieceBonus = calculateAttackedPieceBonus(board, currentTeamMoves, enemyTeamMoves);
            openingScore = openingScore + attackedPieceBonus[0];
            endGameScore = endGameScore + attackedPieceBonus[1];
        }else{
            int[] attackedPieceBonus = calculateAttackedPieceBonus(board, enemyTeamMoves, currentTeamMoves);
            openingScore = openingScore + attackedPieceBonus[0];
            endGameScore = endGameScore + attackedPieceBonus[1];
        }

        openingScore = openingScore + materialScoreMid;
        endGameScore = endGameScore + materialScoreEnd;

        openingScore = openingScore + pawnStructureScoreMid;
        endGameScore = endGameScore + pawnStructureScoreEnd;

        return (int) Math.round(((openingScore * (256 - phase)) + (endGameScore * phase)) / 256);
    }

    private int getKingDistanceToCenter(Board board, Color color){
        int kingIndex;
        if(color == WHITE){
            kingIndex = Long.numberOfTrailingZeros(board.whitePieces & board.kings);
        }else{
            kingIndex = Long.numberOfTrailingZeros(board.blackPieces & board.kings);
        }
        if(kingIndex == 64){
            System.out.println(Arrays.toString(board.moves.toArray()));
            System.out.println(board.toFENString());
        }
        return EvaluationParams.SINGLE_MOVES_TO_CENTER_COUNT[kingIndex];
    }

    private int[] calculateMobilityBonus(Board board, Move[] currentTeamMoves, Move[] enemyTeamMoves){
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
                mobilityBonusMid = mobilityBonusMid + parameters[778 + knightMobilitiesWhite[i]];
                mobilityBonusEnd = mobilityBonusEnd + parameters[787 + knightMobilitiesWhite[i]];
            }

            for(int i = 0; i < bishopMobilitiesWhite.length; i++){
                mobilityBonusMid = mobilityBonusMid + parameters[796 + bishopMobilitiesWhite[i]];
                mobilityBonusEnd = mobilityBonusEnd + parameters[810 + bishopMobilitiesWhite[i]];
            }

            for(int i = 0; i < rookMobilitiesWhite.length; i++){
                mobilityBonusMid = mobilityBonusMid + parameters[824 + rookMobilitiesWhite[i]];
                mobilityBonusEnd = mobilityBonusEnd + parameters[839 + rookMobilitiesWhite[i]];
            }

            for(int i = 0; i < queenMobilitiesWhite.length; i++){
                mobilityBonusMid = mobilityBonusMid + parameters[854 + queenMobilitiesWhite[i]];
                mobilityBonusEnd = mobilityBonusEnd + parameters[882 + queenMobilitiesWhite[i]];
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
                mobilityBonusMid = mobilityBonusMid - parameters[778 + knightMobilitiesBlack[i]];
                mobilityBonusEnd = mobilityBonusEnd - parameters[787 + knightMobilitiesBlack[i]];
            }

            for(int i = 0; i < bishopMobilitiesBlack.length; i++){
                mobilityBonusMid = mobilityBonusMid - parameters[796 + bishopMobilitiesBlack[i]];
                mobilityBonusEnd = mobilityBonusEnd - parameters[810 + bishopMobilitiesBlack[i]];
            }

            for(int i = 0; i < rookMobilitiesBlack.length; i++){
                mobilityBonusMid = mobilityBonusMid - parameters[824 + rookMobilitiesBlack[i]];
                mobilityBonusEnd = mobilityBonusEnd - parameters[839 + rookMobilitiesBlack[i]];
            }

            for(int i = 0; i < queenMobilitiesBlack.length; i++){
                mobilityBonusMid = mobilityBonusMid - parameters[854 + queenMobilitiesBlack[i]];
                mobilityBonusEnd = mobilityBonusEnd - parameters[882 + queenMobilitiesBlack[i]];
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
                mobilityBonusMid = mobilityBonusMid - parameters[778 + knightMobilitiesBlack[i]];
                mobilityBonusEnd = mobilityBonusEnd - parameters[787 + knightMobilitiesBlack[i]];
            }

            for(int i = 0; i < bishopMobilitiesBlack.length; i++){
                mobilityBonusMid = mobilityBonusMid - parameters[796 + bishopMobilitiesBlack[i]];
                mobilityBonusEnd = mobilityBonusEnd - parameters[810 + bishopMobilitiesBlack[i]];
            }

            for(int i = 0; i < rookMobilitiesBlack.length; i++){
                mobilityBonusMid = mobilityBonusMid - parameters[824 + rookMobilitiesBlack[i]];
                mobilityBonusEnd = mobilityBonusEnd - parameters[839 + rookMobilitiesBlack[i]];
            }

            for(int i = 0; i < queenMobilitiesBlack.length; i++){
                mobilityBonusMid = mobilityBonusMid - parameters[854 + queenMobilitiesBlack[i]];
                mobilityBonusEnd = mobilityBonusEnd - parameters[882 + queenMobilitiesBlack[i]];
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
                mobilityBonusMid = mobilityBonusMid + parameters[778 + knightMobilitiesWhite[i]];
                mobilityBonusEnd = mobilityBonusEnd + parameters[787 + knightMobilitiesWhite[i]];
            }

            for(int i = 0; i < bishopMobilitiesWhite.length; i++){
                mobilityBonusMid = mobilityBonusMid + parameters[796 + bishopMobilitiesWhite[i]];
                mobilityBonusEnd = mobilityBonusEnd + parameters[810 + bishopMobilitiesWhite[i]];
            }

            for(int i = 0; i < rookMobilitiesWhite.length; i++){
                mobilityBonusMid = mobilityBonusMid + parameters[824 + rookMobilitiesWhite[i]];
                mobilityBonusEnd = mobilityBonusEnd + parameters[839 + rookMobilitiesWhite[i]];
            }

            for(int i = 0; i < queenMobilitiesWhite.length; i++){
                mobilityBonusMid = mobilityBonusMid + parameters[854 + queenMobilitiesWhite[i]];
                mobilityBonusEnd = mobilityBonusEnd + parameters[882 + queenMobilitiesWhite[i]];
            }
        }

        return new int[]{mobilityBonusMid, mobilityBonusEnd};
    }

    private int[] calculateOutpostBonus(Board board, MoveMasks masks){
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
                        outpostBonusMid = outpostBonusMid + parameters[916];
                        outpostBonusEnd = outpostBonusEnd + parameters[917];
                    }
                }
            }
        }

        for(int i = 0; i < whiteBishopIndices.size(); i++){
            int index = whiteBishopIndices.get(i);
            if(index >= 24 && index <= 47){
                if((((1L << (index - 7)) & board.pawns & board.whitePieces & NOT_A_FILE) != 0) || (((1L << (index - 9)) & board.pawns & board.whitePieces & NOT_H_FILE) != 0)){
                    if((masks.rays(Direction.NORTH, index - 1) & NOT_H_FILE & board.pawns & board.blackPieces) == 0 && (masks.rays(Direction.NORTH, index + 1) & NOT_A_FILE & board.pawns & board.blackPieces) == 0){
                        outpostBonusMid = outpostBonusMid + parameters[918];
                        outpostBonusEnd = outpostBonusEnd + parameters[919];
                    }
                }
            }
        }

        for(int i = 0; i < blackKnightIndices.size(); i++){
            int index = blackKnightIndices.get(i);
            if(index >= 16 && index <= 39){
                if((((1L << (index + 7)) & board.pawns & board.blackPieces & NOT_H_FILE) != 0) || (((1L << (index + 9)) & board.pawns & board.blackPieces & NOT_A_FILE) != 0)){
                    if((masks.rays(Direction.SOUTH, index - 1) & NOT_H_FILE & board.pawns & board.whitePieces) == 0 && (masks.rays(Direction.SOUTH, index + 1) & NOT_A_FILE & board.pawns & board.whitePieces) == 0){
                        outpostBonusMid = outpostBonusMid - parameters[916];
                        outpostBonusEnd = outpostBonusEnd - parameters[917];
                    }
                }
            }
        }

        for(int i = 0; i < blackBishopIndices.size(); i++){
            int index = blackBishopIndices.get(i);
            if(index >= 16 && index <= 39){
                if((((1L << (index + 7)) & board.pawns & board.blackPieces & NOT_H_FILE) != 0) || (((1L << (index + 9)) & board.pawns & board.blackPieces & NOT_A_FILE) != 0)){
                    if((masks.rays(Direction.SOUTH, index - 1) & NOT_H_FILE & board.pawns & board.whitePieces) == 0 && (masks.rays(Direction.SOUTH, index + 1) & NOT_A_FILE & board.pawns & board.whitePieces) == 0){
                        outpostBonusMid = outpostBonusMid - parameters[918];
                        outpostBonusEnd = outpostBonusEnd - parameters[919];
                    }
                }
            }
        }

        return new int[]{outpostBonusMid, outpostBonusEnd};
    }

    private int[] calculateAttackedPieceBonus(Board board, Move[] whiteMoves, Move[] blackMoves){
        int openingScore = 0, endGameScore = 0;

        for(int i = 0; i < whiteMoves.length; i++){
            long endFieldMask = 1L << whiteMoves[i].getEndFieldIndex();
            PieceType pieceType = whiteMoves[i].getPieceType();
            if((pieceType == PieceType.KNIGHT || pieceType == PieceType.BISHOP || pieceType == PieceType.ROOK) && ((endFieldMask & board.blackPieces) != 0L)){
                if(pieceType == PieceType.ROOK){
                    if((endFieldMask & board.knights) != 0){
                        if(((1L << (whiteMoves[i].getEndFieldIndex() + 7)) & board.blackPieces & board.pawns & NOT_H_FILE) != 0L){
                            continue;
                        }
                        if(((1L << (whiteMoves[i].getEndFieldIndex() + 9)) & board.blackPieces & board.pawns & NOT_A_FILE) != 0L){
                            continue;
                        }
                        openingScore = openingScore + parameters[942 + 0];
                        endGameScore = endGameScore + parameters[947 + 0];
                    }else if((endFieldMask & board.bishops) != 0){
                        if(((1L << (whiteMoves[i].getEndFieldIndex() + 7)) & board.blackPieces & board.pawns & NOT_H_FILE) != 0L){
                            continue;
                        }
                        if(((1L << (whiteMoves[i].getEndFieldIndex() + 9)) & board.blackPieces & board.pawns & NOT_A_FILE) != 0L){
                            continue;
                        }
                        openingScore = openingScore + parameters[942 + 1];
                        endGameScore = endGameScore + parameters[947 + 1];
                    }else if((endFieldMask & board.rooks) != 0){
                        openingScore = openingScore + parameters[942 + 2];
                        endGameScore = endGameScore + parameters[947 + 2];
                    }else if((endFieldMask & board.queens) != 0){
                        openingScore = openingScore + parameters[942 + 3];
                        endGameScore = endGameScore + parameters[947 + 3];
                    }else if((endFieldMask & board.kings) != 0){
                        openingScore = openingScore + parameters[942 + 4];
                        endGameScore = endGameScore + parameters[947 + 4];
                    }
                }else{
                    if((endFieldMask & board.knights) != 0){
                        openingScore = openingScore + parameters[932 + 0];
                        endGameScore = endGameScore + parameters[937 + 0];
                    }else if((endFieldMask & board.bishops) != 0){
                        openingScore = openingScore + parameters[932 + 1];
                        endGameScore = endGameScore + parameters[937 + 1];
                    }else if((endFieldMask & board.rooks) != 0){
                        openingScore = openingScore + parameters[932 + 2];
                        endGameScore = endGameScore + parameters[937 + 2];
                    }else if((endFieldMask & board.queens) != 0){
                        openingScore = openingScore + parameters[932 + 3];
                        endGameScore = endGameScore + parameters[937 + 3];
                    }else if((endFieldMask & board.kings) != 0){
                        openingScore = openingScore + parameters[932 + 4];
                        endGameScore = endGameScore + parameters[937 + 4];
                    }
                }
            }
        }

        for(int i = 0; i < blackMoves.length; i++){
            long endFieldMask = 1L << blackMoves[i].getEndFieldIndex();
            PieceType pieceType = blackMoves[i].getPieceType();
            if((pieceType == PieceType.KNIGHT || pieceType == PieceType.BISHOP || pieceType == PieceType.ROOK) && ((endFieldMask & board.whitePieces) != 0L)){
                if(pieceType == PieceType.ROOK){
                    if((endFieldMask & board.knights) != 0){
                        if(((1L << (blackMoves[i].getEndFieldIndex() - 7)) & board.whitePieces & board.pawns & NOT_A_FILE) != 0L){
                            continue;
                        }
                        if(((1L << (blackMoves[i].getEndFieldIndex() - 9)) & board.whitePieces & board.pawns & NOT_H_FILE) != 0L){
                            continue;
                        }
                        openingScore = openingScore - parameters[942 + 0];
                        endGameScore = endGameScore - parameters[947 + 0];
                    }else if((endFieldMask & board.bishops) != 0){
                        if(((1L << (blackMoves[i].getEndFieldIndex() - 7)) & board.whitePieces & board.pawns & NOT_A_FILE) != 0L){
                            continue;
                        }
                        if(((1L << (blackMoves[i].getEndFieldIndex() - 9)) & board.whitePieces & board.pawns & NOT_H_FILE) != 0L){
                            continue;
                        }
                        openingScore = openingScore - parameters[942 + 1];
                        endGameScore = endGameScore - parameters[947 + 1];
                    }else if((endFieldMask & board.rooks) != 0){
                        openingScore = openingScore - parameters[942 + 2];
                        endGameScore = endGameScore - parameters[947 + 2];
                    }else if((endFieldMask & board.queens) != 0){
                        openingScore = openingScore - parameters[942 + 3];
                        endGameScore = endGameScore - parameters[947 + 3];
                    }else if((endFieldMask & board.kings) != 0){
                        openingScore = openingScore - parameters[942 + 4];
                        endGameScore = endGameScore - parameters[947 + 4];
                    }
                }else{
                    if((endFieldMask & board.knights) != 0){
                        openingScore = openingScore - parameters[932 + 0];
                        endGameScore = endGameScore - parameters[937 + 0];
                    }else if((endFieldMask & board.bishops) != 0){
                        openingScore = openingScore - parameters[932 + 1];
                        endGameScore = endGameScore - parameters[937 + 1];
                    }else if((endFieldMask & board.rooks) != 0){
                        openingScore = openingScore - parameters[932 + 2];
                        endGameScore = endGameScore - parameters[937 + 2];
                    }else if((endFieldMask & board.queens) != 0){
                        openingScore = openingScore - parameters[932 + 3];
                        endGameScore = endGameScore - parameters[937 + 3];
                    }else if((endFieldMask & board.kings) != 0){
                        openingScore = openingScore - parameters[932 + 4];
                        endGameScore = endGameScore - parameters[937 + 4];
                    }
                }
            }
        }

        return new int[]{openingScore, endGameScore};
    }

    private int[] calculateKingMobilityBonus(Board board, Move[] currentTeamMoves, Move[] enemyTeamMoves){
        int currenTeamBonusMid = 0, currenTeamBonusEnd = 0, enemyTeamBonusMid = 0, enemyTeamBonusEnd = 0;

        for(int i = 0; i < currentTeamMoves.length; i++){
            if(currentTeamMoves[i].getPieceType() == PieceType.KING){
                currenTeamBonusMid = currenTeamBonusMid + parameters[922];
                currenTeamBonusEnd = currenTeamBonusEnd + parameters[923];
            }
        }

        for(int i = 0; i < enemyTeamMoves.length; i++){
            if(enemyTeamMoves[i].getPieceType() == PieceType.KING){
                enemyTeamBonusMid = enemyTeamBonusMid + parameters[922];
                enemyTeamBonusEnd = enemyTeamBonusEnd + parameters[923];
            }
        }

        if(board.getTurn() == WHITE){
            return new int[]{currenTeamBonusMid - enemyTeamBonusMid, currenTeamBonusEnd - enemyTeamBonusEnd};
        }else{
            return new int[]{enemyTeamBonusMid - currenTeamBonusMid, enemyTeamBonusEnd - currenTeamBonusEnd};
        }
    }

    private int[] getRookOnOpenFilesCount(Board board, MoveMasks masks, Color color){
        long rooks;
        long enemyPawns, ownPawns;
        int openFilesCount = 0, semiOpenFilesCount = 0, closedFilesCount = 0;

        if(color == WHITE){
            rooks = board.whitePieces & board.rooks;
            ownPawns = board.whitePieces & board.pawns;
            enemyPawns = board.blackPieces & board.pawns;
        }else{
            rooks = board.blackPieces & board.rooks;
            ownPawns = board.blackPieces & board.pawns;
            enemyPawns = board.whitePieces & board.pawns;
        }

        for(int i = 0; i < 64; i++){
            if((rooks & (1L << i)) != 0){
                if(((masks.rays(Direction.NORTH, i) | masks.rays(Direction.SOUTH, i)) & ownPawns) == 0L){
                    if(((masks.rays(Direction.NORTH, i) | masks.rays(Direction.SOUTH, i)) & enemyPawns) == 0L){
                        openFilesCount++;
                    }else{
                        semiOpenFilesCount++;
                    }
                }else{
                    closedFilesCount++;
                }
            }
        }

        return new int[]{openFilesCount, semiOpenFilesCount, closedFilesCount};
    }

    private int getBlockedPawnCount(Board board, Color color){
        long targetSquares;
        if(color == WHITE){
            targetSquares = ((board.whitePieces & board.pawns) << 8);
        }else{
            targetSquares = ((board.blackPieces & board.pawns) >>> 8);
        }

        targetSquares = targetSquares & (board.whitePieces | board.blackPieces);

        return getSetBits(targetSquares);
    }

    private int getDoubledPawnCount(Board board, Color color){
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

    private int getIsolatedPawnCount(Board board, Color color){
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

}
