package engine.move_generation;

import engine.representation.*;

import java.util.ArrayList;
import java.util.Collections;

public class MoveGenerator {

    private static final long notAFile = -72340172838076674L, notHFile = 9187201950435737471L, onlySecondRank = 65280L, onlySeventhRank = 71776119061217280L;
    private static final long notABFile = -217020518514230020L, notGHFile = 4557430888798830399L;
    private static final long whiteShortCastling = 96L, whiteLongCastling = 14L, blackShortCastling = 6917529027641081856L, blackLongCastling = 1008806316530991104L;

    public static Move[] generateLegalMoves(Board current, MoveMasks moveMasks){
        Move[] moves = generatePseudoLegalMoves(current, moveMasks);
        ArrayList<Move> legalMoves = new ArrayList<Move>(moves.length);

        for(int i = 0; i < moves.length; i++){
            current.doMove(moves[i]);
            if(current.getTurn() == Color.WHITE){
                int blackKingIndex = Long.numberOfTrailingZeros(current.blackPieces & current.kings);
                if(!isAttacked(current, moveMasks, blackKingIndex, Color.WHITE)){
                    legalMoves.add(moves[i]);
                }
            }else{
                int whiteKingIndex = Long.numberOfTrailingZeros(current.whitePieces & current.kings);
                if(!isAttacked(current, moveMasks, whiteKingIndex, Color.BLACK)){
                    legalMoves.add(moves[i]);
                }
            }
            current.undoLastMove();
        }

        return legalMoves.toArray(new Move[0]);
    }

    public static Move[] generatePseudoLegalMoves(Board current, MoveMasks moveMasks){
        Move[] kingMoves = generateKingMoves(current, moveMasks);
        Move[] queenMoves = generateQueenMoves(current, moveMasks);
        Move[] rookMoves = generateRookMoves(current, moveMasks);
        Move[] bishopMoves = generateBishopMoves(current, moveMasks);
        Move[] knightMoves = generateKnightMoves(current);
        Move[] pawnMoves = generatePawnMoves(current);

        ArrayList<Move> moves = new ArrayList<Move>(kingMoves.length + queenMoves.length + rookMoves.length + bishopMoves.length + knightMoves.length + pawnMoves.length);
        Collections.addAll(moves, kingMoves);
        Collections.addAll(moves, queenMoves);
        Collections.addAll(moves, rookMoves);
        Collections.addAll(moves, bishopMoves);
        Collections.addAll(moves, knightMoves);
        Collections.addAll(moves, pawnMoves);

        return moves.toArray(new Move[0]);
    }

    public static ArrayList<Integer> getSetBitIndices(long l){
        ArrayList<Integer> result = new ArrayList<Integer>(10);
        for(int i = 0; i < 64; i++){
            if((l & (1L << i)) != 0){
                result.add(i);
            }
        }
        return result;
    }

    private static void addPawnPromotions(int startIndex, int endIndex, ArrayList<Move> moves){
        Move promotionToQueen = new Move(startIndex, endIndex, PieceType.PAWN);
        promotionToQueen.isPromotionToQueen = true;
        moves.add(promotionToQueen);
        Move promotionToRook = new Move(startIndex, endIndex, PieceType.PAWN);
        promotionToRook.isPromotionToRook = true;
        moves.add(promotionToRook);
        Move promotionToBishop = new Move(startIndex, endIndex, PieceType.PAWN);
        promotionToBishop.isPromotionToBishop = true;
        moves.add(promotionToBishop);
        Move promotionToKnight = new Move(startIndex, endIndex, PieceType.PAWN);
        promotionToKnight.isPromotionToKnight = true;
        moves.add(promotionToKnight);
    }

    public static boolean isAttacked(Board current, MoveMasks moveMasks, int fieldIndex, Color attackingColor){
        long currentTeam, enemyTeam;
        long fieldMask = 1L << fieldIndex;
        long blockers = current.whitePieces | current.blackPieces;
        if(attackingColor == Color.BLACK){
            currentTeam = current.whitePieces;
            enemyTeam = current.blackPieces;

            long pawnCaptures = (((fieldMask & notAFile) << 7) | ((fieldMask & notHFile) << 9)) & (current.pawns & enemyTeam);

            if(pawnCaptures != 0){
                return true;
            }
        }else{
            currentTeam = current.blackPieces;
            enemyTeam = current.whitePieces;

            long pawnCaptures = (((fieldMask & notHFile) >>> 7) | ((fieldMask & notAFile) >>> 9)) & (current.pawns & enemyTeam);
            if(pawnCaptures != 0){
                return true;
            }
        }

        long kingCaptures = (fieldMask << 1 & notAFile) | (fieldMask << 7 & notHFile) | fieldMask << 8 | (fieldMask << 9 & notAFile) | (fieldMask >>> 1 & notHFile) | (fieldMask >>> 7 & notAFile) | fieldMask >>> 8 | (fieldMask >>> 9 & notHFile);
        if((kingCaptures & enemyTeam & current.kings) != 0){
            return true;
        }

        long knightCaptures = ((fieldMask << 6) & notGHFile) | ((fieldMask << 10) & notABFile) | ((fieldMask << 15) & notHFile) | ((fieldMask << 17) & notAFile) | ((fieldMask >>> 6) & notABFile) | ((fieldMask >>> 10) & notGHFile) | ((fieldMask >>> 15) & notAFile) | ((fieldMask >>> 17) & notHFile);
        if((knightCaptures & enemyTeam & current.knights) != 0){
            return true;
        }

        long straightSlidingPieceCaptures = (moveMasks.rays(Direction.NORTH, fieldIndex) & ~moveMasks.rays(Direction.NORTH, Long.numberOfTrailingZeros(blockers & moveMasks.rays(Direction.NORTH, fieldIndex)))) & ~currentTeam;
        straightSlidingPieceCaptures = straightSlidingPieceCaptures | (moveMasks.rays(Direction.EAST, fieldIndex) & ~moveMasks.rays(Direction.EAST, Long.numberOfTrailingZeros(blockers & moveMasks.rays(Direction.EAST, fieldIndex)))) & ~currentTeam;
        straightSlidingPieceCaptures = straightSlidingPieceCaptures | (moveMasks.rays(Direction.SOUTH, fieldIndex) & ~moveMasks.rays(Direction.SOUTH, 63 - Long.numberOfLeadingZeros(blockers & moveMasks.rays(Direction.SOUTH, fieldIndex)))) & ~currentTeam;
        straightSlidingPieceCaptures = straightSlidingPieceCaptures | (moveMasks.rays(Direction.WEST, fieldIndex) & ~moveMasks.rays(Direction.WEST, 63 - Long.numberOfLeadingZeros(blockers & moveMasks.rays(Direction.WEST, fieldIndex)))) & ~currentTeam;

        if(((straightSlidingPieceCaptures & enemyTeam & current.rooks) != 0) || ((straightSlidingPieceCaptures & enemyTeam & current.queens) != 0)){
            return true;
        }

        long diagonalSlidingPieceCaptures = (moveMasks.rays(Direction.NORTH_EAST, fieldIndex) & ~moveMasks.rays(Direction.NORTH_EAST, Long.numberOfTrailingZeros(blockers & moveMasks.rays(Direction.NORTH_EAST, fieldIndex)))) & ~currentTeam;
        diagonalSlidingPieceCaptures = diagonalSlidingPieceCaptures | (moveMasks.rays(Direction.NORTH_WEST, fieldIndex) & ~moveMasks.rays(Direction.NORTH_WEST, Long.numberOfTrailingZeros(blockers & moveMasks.rays(Direction.NORTH_WEST, fieldIndex)))) & ~currentTeam;
        diagonalSlidingPieceCaptures = diagonalSlidingPieceCaptures | (moveMasks.rays(Direction.SOUTH_EAST, fieldIndex) & ~moveMasks.rays(Direction.SOUTH_EAST, 63 - Long.numberOfLeadingZeros(blockers & moveMasks.rays(Direction.SOUTH_EAST, fieldIndex)))) & ~currentTeam;
        diagonalSlidingPieceCaptures = diagonalSlidingPieceCaptures | (moveMasks.rays(Direction.SOUTH_WEST, fieldIndex) & ~moveMasks.rays(Direction.SOUTH_WEST, 63 - Long.numberOfLeadingZeros(blockers & moveMasks.rays(Direction.SOUTH_WEST, fieldIndex)))) & ~currentTeam;

        if(((diagonalSlidingPieceCaptures & enemyTeam & current.bishops) != 0) || ((diagonalSlidingPieceCaptures & enemyTeam & current.queens) != 0)){
            return true;
        }

        return false;
    }

    public static Move[] generateKingMoves(Board current, MoveMasks moveMasks){
        ArrayList<Move> kingMoves = new ArrayList<Move>();
        boolean hasKingMoved, hasShortRookMoved, hasLongRookMoved;
        long currentTeam;
        long shortCastlingMask, longCastlingMask;
        Move shortCastle, longCastle;
        int kingIndex, shortCastlePassingIndex, longCastlePassingIndex;
        Color attackingColor;

        if(current.getTurn() == Color.WHITE){
            currentTeam = current.whitePieces;
            hasKingMoved = current.getHasWhiteKingMoved();
            hasShortRookMoved = current.getHasWhiteShortRookMoved();
            hasLongRookMoved = current.getHasWhiteLongRookMoved();

            shortCastlingMask = whiteShortCastling;
            longCastlingMask = whiteLongCastling;
            shortCastle = new Move(4, 6, PieceType.KING);
            longCastle = new Move(4, 2, PieceType.KING);
            kingIndex = 4;
            shortCastlePassingIndex = 5;
            longCastlePassingIndex = 3;
            attackingColor = Color.BLACK;
        }else{
            currentTeam = current.blackPieces;
            hasKingMoved = current.getHasBlackKingMoved();
            hasShortRookMoved = current.getHasBlackShortRookMoved();
            hasLongRookMoved = current.getHasBlackLongRookMoved();

            shortCastlingMask = blackShortCastling;
            longCastlingMask = blackLongCastling;
            shortCastle = new Move(60, 62, PieceType.KING);
            longCastle = new Move(60, 58, PieceType.KING);
            kingIndex = 60;
            shortCastlePassingIndex = 61;
            longCastlePassingIndex = 59;
            attackingColor = Color.WHITE;
        }
        shortCastle.isCastling = true;
        longCastle.isCastling = true;

        long king = current.kings & currentTeam;
        long possibleKingMoves = (king << 1 & notAFile) | (king << 7 & notHFile) | king << 8 | (king << 9 & notAFile) | (king >>> 1 & notHFile) | (king >>> 7 & notAFile) | king >>> 8 | (king >>> 9 & notHFile);
        possibleKingMoves = possibleKingMoves & ~currentTeam;

        int kingPosition = Long.numberOfTrailingZeros(king);

        if((possibleKingMoves & (king << 1)) != 0){
            kingMoves.add(new Move(kingPosition, kingPosition + 1, PieceType.KING));
        }

        if((possibleKingMoves & (king << 7)) != 0){
            kingMoves.add(new Move(kingPosition, kingPosition + 7, PieceType.KING));
        }

        if((possibleKingMoves & (king << 8)) != 0){
            kingMoves.add(new Move(kingPosition, kingPosition + 8, PieceType.KING));
        }

        if((possibleKingMoves & (king << 9)) != 0){
            kingMoves.add(new Move(kingPosition, kingPosition + 9, PieceType.KING));
        }

        if((possibleKingMoves & (king >>> 1)) != 0){
            kingMoves.add(new Move(kingPosition, kingPosition - 1, PieceType.KING));
        }

        if((possibleKingMoves & (king >>> 7)) != 0){
            kingMoves.add(new Move(kingPosition, kingPosition - 7, PieceType.KING));
        }

        if((possibleKingMoves & (king >>> 8)) != 0){
            kingMoves.add(new Move(kingPosition, kingPosition - 8, PieceType.KING));
        }

        if((possibleKingMoves & (king >>> 9)) != 0){
            kingMoves.add(new Move(kingPosition, kingPosition - 9, PieceType.KING));
        }

        if(!hasKingMoved){
            if(!hasShortRookMoved && ((current.whitePieces | current.blackPieces) & shortCastlingMask) == 0 && !isAttacked(current, moveMasks, shortCastlePassingIndex, attackingColor) && !isAttacked(current, moveMasks, kingIndex, attackingColor)){
                kingMoves.add(shortCastle);
            }

            if(!hasLongRookMoved && ((current.whitePieces | current.blackPieces) & longCastlingMask) == 0 && !isAttacked(current, moveMasks, longCastlePassingIndex, attackingColor) && !isAttacked(current, moveMasks, kingIndex, attackingColor)){
                kingMoves.add(longCastle);
            }
        }

        return kingMoves.toArray(new Move[0]);
    }

    public static Move[] generateQueenMoves(Board current, MoveMasks moveMasks){
        ArrayList<Move> queenMoves = new ArrayList<Move>();
        long currentTeam, blockers = current.whitePieces | current.blackPieces;;

        if(current.getTurn() == Color.WHITE){
            currentTeam = current.whitePieces;
        }else{
            currentTeam = current.blackPieces;
        }
        long queen = current.queens & currentTeam;

        if(queen == 0L){
            return new Move[0];
        }

        ArrayList<Integer> queenIndices = getSetBitIndices(queen);

        for(int i = 0; i < queenIndices.size(); i++){
            int queenIndex = queenIndices.get(i);

            int maskedBlockerIndex = Long.numberOfTrailingZeros(blockers & moveMasks.rays(Direction.NORTH, queenIndex));
            long result = (moveMasks.rays(Direction.NORTH, queenIndex) & ~moveMasks.rays(Direction.NORTH, maskedBlockerIndex)) & ~currentTeam;

            maskedBlockerIndex = Long.numberOfTrailingZeros(blockers & moveMasks.rays(Direction.NORTH_EAST, queenIndex));
            result = result | (moveMasks.rays(Direction.NORTH_EAST, queenIndex) & ~moveMasks.rays(Direction.NORTH_EAST, maskedBlockerIndex)) & ~currentTeam;

            maskedBlockerIndex = Long.numberOfTrailingZeros(blockers & moveMasks.rays(Direction.EAST, queenIndex));
            result = result | (moveMasks.rays(Direction.EAST, queenIndex) & ~moveMasks.rays(Direction.EAST, maskedBlockerIndex)) & ~currentTeam;

            maskedBlockerIndex = 63 - Long.numberOfLeadingZeros(blockers & moveMasks.rays(Direction.SOUTH_EAST, queenIndex));
            result = result | (moveMasks.rays(Direction.SOUTH_EAST, queenIndex) & ~moveMasks.rays(Direction.SOUTH_EAST, maskedBlockerIndex)) & ~currentTeam;

            maskedBlockerIndex = 63 - Long.numberOfLeadingZeros(blockers & moveMasks.rays(Direction.SOUTH, queenIndex));
            result = result | (moveMasks.rays(Direction.SOUTH, queenIndex) & ~moveMasks.rays(Direction.SOUTH, maskedBlockerIndex)) & ~currentTeam;

            maskedBlockerIndex = 63 - Long.numberOfLeadingZeros(blockers & moveMasks.rays(Direction.SOUTH_WEST, queenIndex));
            result = result | (moveMasks.rays(Direction.SOUTH_WEST, queenIndex) & ~moveMasks.rays(Direction.SOUTH_WEST, maskedBlockerIndex)) & ~currentTeam;

            maskedBlockerIndex = 63 - Long.numberOfLeadingZeros(blockers & moveMasks.rays(Direction.WEST, queenIndex));
            result = result | (moveMasks.rays(Direction.WEST, queenIndex) & ~moveMasks.rays(Direction.WEST, maskedBlockerIndex)) & ~currentTeam;

            maskedBlockerIndex = Long.numberOfTrailingZeros(blockers & moveMasks.rays(Direction.NORTH_WEST, queenIndex));
            result = result | (moveMasks.rays(Direction.NORTH_WEST, queenIndex) & ~moveMasks.rays(Direction.NORTH_WEST, maskedBlockerIndex)) & ~currentTeam;

            for(int j = 0; j < 64; j++){
                if((result & (1L << j)) != 0L){
                    queenMoves.add(new Move(queenIndex, j, PieceType.QUEEN));
                }
            }
        }

        return queenMoves.toArray(new Move[0]);
    }

    public static Move[] generateRookMoves(Board current, MoveMasks moveMasks){
        ArrayList<Move> rookMoves = new ArrayList<Move>();
        long currentTeam, blockers = current.whitePieces | current.blackPieces;;

        if(current.getTurn() == Color.WHITE){
            currentTeam = current.whitePieces;
        }else{
            currentTeam = current.blackPieces;
        }
        long rooks = current.rooks & currentTeam;

        if(rooks == 0L){
            return new Move[0];
        }

        ArrayList<Integer> rookIndices = getSetBitIndices(rooks);

        for(int index = 0; index < rookIndices.size(); index++){
            int currentRookIndex = rookIndices.get(index);
            int maskedBlockerIndex = Long.numberOfTrailingZeros(blockers & moveMasks.rays(Direction.NORTH, currentRookIndex));
            long result = (moveMasks.rays(Direction.NORTH, currentRookIndex) & ~moveMasks.rays(Direction.NORTH, maskedBlockerIndex)) & ~currentTeam;

            maskedBlockerIndex = Long.numberOfTrailingZeros(blockers & moveMasks.rays(Direction.EAST, currentRookIndex));
            result = result | (moveMasks.rays(Direction.EAST, currentRookIndex) & ~moveMasks.rays(Direction.EAST, maskedBlockerIndex)) & ~currentTeam;

            maskedBlockerIndex = 63 - Long.numberOfLeadingZeros(blockers & moveMasks.rays(Direction.SOUTH, currentRookIndex));
            result = result | (moveMasks.rays(Direction.SOUTH, currentRookIndex) & ~moveMasks.rays(Direction.SOUTH, maskedBlockerIndex)) & ~currentTeam;

            maskedBlockerIndex = 63 - Long.numberOfLeadingZeros(blockers & moveMasks.rays(Direction.WEST, currentRookIndex));
            result = result | (moveMasks.rays(Direction.WEST, currentRookIndex) & ~moveMasks.rays(Direction.WEST, maskedBlockerIndex)) & ~currentTeam;

            for(int i = 0; i < 64; i++){
                if((result & (1L << i)) != 0L){
                    rookMoves.add(new Move(currentRookIndex, i, PieceType.ROOK));
                }
            }
        }

        return rookMoves.toArray(new Move[0]);
    }

    public static Move[] generateBishopMoves(Board current, MoveMasks moveMasks){
        ArrayList<Move> bishopMoves = new ArrayList<Move>();
        long currentTeam, blockers = current.whitePieces | current.blackPieces;;

        if(current.getTurn() == Color.WHITE){
            currentTeam = current.whitePieces;
        }else{
            currentTeam = current.blackPieces;
        }
        long bishops = current.bishops & currentTeam;

        if(bishops == 0L){
            return new Move[0];
        }

        ArrayList<Integer> bishopIndices = getSetBitIndices(bishops);

        for(int index = 0; index < bishopIndices.size(); index++){
            int currentBishopIndex = bishopIndices.get(index);

            int maskedBlockerIndex = Long.numberOfTrailingZeros(blockers & moveMasks.rays(Direction.NORTH_EAST, currentBishopIndex));
            long result = (moveMasks.rays(Direction.NORTH_EAST, currentBishopIndex) & ~moveMasks.rays(Direction.NORTH_EAST, maskedBlockerIndex)) & ~currentTeam;

            maskedBlockerIndex = Long.numberOfTrailingZeros(blockers & moveMasks.rays(Direction.NORTH_WEST, currentBishopIndex));
            result = result | (moveMasks.rays(Direction.NORTH_WEST, currentBishopIndex) & ~moveMasks.rays(Direction.NORTH_WEST, maskedBlockerIndex)) & ~currentTeam;

            maskedBlockerIndex = 63 - Long.numberOfLeadingZeros(blockers & moveMasks.rays(Direction.SOUTH_EAST, currentBishopIndex));
            result = result | (moveMasks.rays(Direction.SOUTH_EAST, currentBishopIndex) & ~moveMasks.rays(Direction.SOUTH_EAST, maskedBlockerIndex)) & ~currentTeam;

            maskedBlockerIndex = 63 - Long.numberOfLeadingZeros(blockers & moveMasks.rays(Direction.SOUTH_WEST, currentBishopIndex));
            result = result | (moveMasks.rays(Direction.SOUTH_WEST, currentBishopIndex) & ~moveMasks.rays(Direction.SOUTH_WEST, maskedBlockerIndex)) & ~currentTeam;

            for(int i = 0; i < 64; i++){
                if((result & (1L << i)) != 0L){
                    bishopMoves.add(new Move(currentBishopIndex, i, PieceType.BISHOP));
                }
            }
        }

        return bishopMoves.toArray(new Move[0]);
    }

    public static Move[] generateKnightMoves(Board current){
        ArrayList<Move> knightMoves = new ArrayList<Move>();
        long currentTeam;
        if(current.getTurn() == Color.WHITE){
            currentTeam = current.whitePieces;
        }else{
            currentTeam = current.blackPieces;
        }
        long knights = current.knights & currentTeam;

        if(knights == 0L){
            return new Move[0];
        }

        ArrayList<Integer> knightIndices = getSetBitIndices(knights);

        for(int index = 0; index < knightIndices.size(); index++){
            int currentKnightIndex = knightIndices.get(index);
            long currentKnight = 1L << currentKnightIndex;

            long possibleKnightMoves = ((currentKnight << 6) & notGHFile) | ((currentKnight << 10) & notABFile) | ((currentKnight << 15) & notHFile) | ((currentKnight << 17) & notAFile) | ((currentKnight >>> 6) & notABFile) | ((currentKnight >>> 10) & notGHFile) | ((currentKnight >>> 15) & notAFile) | ((currentKnight >>> 17) & notHFile);
            possibleKnightMoves = possibleKnightMoves & ~currentTeam;

            if((possibleKnightMoves & (currentKnight << 6)) != 0){
                knightMoves.add(new Move(currentKnightIndex, currentKnightIndex + 6, PieceType.KNIGHT));
            }

            if((possibleKnightMoves & (currentKnight << 10)) != 0){
                knightMoves.add(new Move(currentKnightIndex, currentKnightIndex + 10, PieceType.KNIGHT));
            }

            if((possibleKnightMoves & (currentKnight << 15)) != 0){
                knightMoves.add(new Move(currentKnightIndex, currentKnightIndex + 15, PieceType.KNIGHT));
            }

            if((possibleKnightMoves & (currentKnight << 17)) != 0){
                knightMoves.add(new Move(currentKnightIndex, currentKnightIndex + 17, PieceType.KNIGHT));
            }

            if((possibleKnightMoves & (currentKnight >>> 6)) != 0){
                knightMoves.add(new Move(currentKnightIndex, currentKnightIndex - 6, PieceType.KNIGHT));
            }

            if((possibleKnightMoves & (currentKnight >>> 10)) != 0){
                knightMoves.add(new Move(currentKnightIndex, currentKnightIndex - 10, PieceType.KNIGHT));
            }

            if((possibleKnightMoves & (currentKnight >>> 15)) != 0){
                knightMoves.add(new Move(currentKnightIndex, currentKnightIndex - 15, PieceType.KNIGHT));
            }

            if((possibleKnightMoves & (currentKnight >>> 17)) != 0){
                knightMoves.add(new Move(currentKnightIndex, currentKnightIndex - 17, PieceType.KNIGHT));
            }
        }

        return knightMoves.toArray(new Move[0]);
    }

    public static Move[] generatePawnMoves(Board current){
        ArrayList<Move> pawnMoves = new ArrayList<Move>();
        long allPieces = current.whitePieces | current.blackPieces;

        if(current.getTurn() == Color.WHITE){
            long whitePawns = current.pawns & current.whitePieces;

            long leftCaptures = ((whitePawns & notAFile) << 7) & current.blackPieces;
            long rightCaptures = ((whitePawns & notHFile) << 9) & current.blackPieces;

            long oneMoveForward = (whitePawns << 8) & ~allPieces;
            long twoMovesForward = ((whitePawns & onlySecondRank) << 16) & ~allPieces & ~(allPieces << 8);

            for(int i = 0; i < 56; i++){
                long index = 1L << i;
                if((twoMovesForward & index) != 0){
                    Move twoFieldsForward = new Move(i - 16, i, PieceType.PAWN);
                    twoFieldsForward.isPawnTwoForward = true;
                    pawnMoves.add(twoFieldsForward);
                }else if((oneMoveForward & index) != 0){
                    pawnMoves.add(new Move(i - 8, i, PieceType.PAWN));
                }
                if((leftCaptures & index) != 0){
                    pawnMoves.add(new Move(i - 7, i, PieceType.PAWN));
                }
                if((rightCaptures & index) != 0){
                    pawnMoves.add(new Move(i - 9, i, PieceType.PAWN));
                }
            }
            for(int i = 56; i < 64; i++){
                long index = 1L << i;
                if((oneMoveForward & index) != 0){
                    addPawnPromotions(i - 8, i, pawnMoves);
                }
                if((leftCaptures & index) != 0){
                    addPawnPromotions(i - 7, i, pawnMoves);
                }
                if((rightCaptures & index) != 0){
                    addPawnPromotions(i - 9, i, pawnMoves);
                }
            }

            int lastMoveEnPassantPushIndex = current.lastMoveEnPassantPushIndex();
            if(lastMoveEnPassantPushIndex > -1){
                long rightIndex = 1L << (lastMoveEnPassantPushIndex + 1L);
                long leftIndex = 1L << (lastMoveEnPassantPushIndex - 1L);

                if((whitePawns & rightIndex) != 0){
                    Move enPassant = new Move(lastMoveEnPassantPushIndex + 1, lastMoveEnPassantPushIndex + 8, PieceType.PAWN);
                    enPassant.isEnPassant = true;
                    pawnMoves.add(enPassant);
                }
                if((whitePawns & leftIndex) != 0){
                    Move enPassant = new Move(lastMoveEnPassantPushIndex - 1, lastMoveEnPassantPushIndex + 8, PieceType.PAWN);
                    enPassant.isEnPassant = true;
                    pawnMoves.add(enPassant);
                }
            }
        }else{
            long blackPawns = current.pawns & current.blackPieces;

            long leftCaptures = ((blackPawns & notHFile) >>> 7) & current.whitePieces;
            long rightCaptures = ((blackPawns & notAFile) >>> 9) & current.whitePieces;

            long oneMoveForward = (blackPawns >>> 8) & ~allPieces;
            long twoMovesForward = ((blackPawns & onlySeventhRank) >>> 16) & ~allPieces & ~(allPieces >>> 8);

            for(int i = 8; i < 64; i++){
                long index = 1L << i;
                if((twoMovesForward & index) != 0){
                    Move twoFieldsForward = new Move(i + 16, i, PieceType.PAWN);
                    twoFieldsForward.isPawnTwoForward = true;
                    pawnMoves.add(twoFieldsForward);
                }else if((oneMoveForward & index) != 0){
                    pawnMoves.add(new Move(i + 8, i, PieceType.PAWN));
                }
                if((leftCaptures & index) != 0){
                    pawnMoves.add(new Move(i + 7, i, PieceType.PAWN));
                }
                if((rightCaptures & index) != 0){
                    pawnMoves.add(new Move(i + 9, i, PieceType.PAWN));
                }
            }

            for(int i = 0; i < 8; i++){
                long index = 1L << i;
                if((oneMoveForward & index) != 0){
                    addPawnPromotions(i + 8, i, pawnMoves);
                }
                if((leftCaptures & index) != 0){
                    addPawnPromotions(i + 7, i, pawnMoves);
                }
                if((rightCaptures & index) != 0){
                    addPawnPromotions(i + 9, i, pawnMoves);
                }
            }

            int lastMoveEnPassantPushIndex = current.lastMoveEnPassantPushIndex();
            if(lastMoveEnPassantPushIndex > -1){
                long rightIndex = 1L << (lastMoveEnPassantPushIndex + 1L);
                long leftIndex = 1L << (lastMoveEnPassantPushIndex - 1L);

                if((blackPawns & rightIndex) != 0){
                    Move enPassant = new Move(lastMoveEnPassantPushIndex + 1, lastMoveEnPassantPushIndex - 8, PieceType.PAWN);
                    enPassant.isEnPassant = true;
                    pawnMoves.add(enPassant);
                }
                if((blackPawns & leftIndex) != 0){
                    Move enPassant = new Move(lastMoveEnPassantPushIndex - 1, lastMoveEnPassantPushIndex - 8, PieceType.PAWN);
                    enPassant.isEnPassant = true;
                    pawnMoves.add(enPassant);
                }
            }
        }
        
        return pawnMoves.toArray(new Move[0]);
    }

}
