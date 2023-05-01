package engine.move_generation;

import engine.representation.*;

import java.util.ArrayList;

public class MoveGenerator {

    static final long notAFile = -72340172838076674L, notHFile = 9187201950435737471L, onlySecondRank = 65280L, onlySeventhRank = 71776119061217280L;
    static final long whiteShortCastling = 96L, whiteLongCastling = 14L, blackShortCastling = 6917529027641081856L, blackLongCastling = 1008806316530991104L;

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

    public static Move[] generateKingMoves(Board current){
        ArrayList<Move> kingMoves = new ArrayList<Move>();
        boolean hasKingMoved, hasShortRookMoved, hasLongRookMoved;
        long currentTeam;
        long shortCastlingMask, longCastlingMask;
        Move shortCastle, longCastle;

        if(current.getTurn() == Color.WHITE){
            currentTeam = current.whitePieces;
            hasKingMoved = current.getHasWhiteKingMoved();
            hasShortRookMoved = current.getHasWhiteShortRookMoved();
            hasLongRookMoved = current.getHasWhiteLongRookMoved();

            shortCastlingMask = whiteShortCastling;
            longCastlingMask = whiteLongCastling;
            shortCastle = new Move(4, 6, PieceType.KING);
            longCastle = new Move(4, 2, PieceType.KING);

        }else{
            currentTeam = current.blackPieces;
            hasKingMoved = current.getHasBlackKingMoved();
            hasShortRookMoved = current.getHasBlackShortRookMoved();
            hasLongRookMoved = current.gettHasBlackLongRookMoved();

            shortCastlingMask = blackShortCastling;
            longCastlingMask = blackLongCastling;
            shortCastle = new Move(60, 62, PieceType.KING);
            longCastle = new Move(60, 58, PieceType.KING);
        }
        shortCastle.isCastling = true;
        longCastle.isCastling = true;

        long king = current.kings & currentTeam;
        long possibleKingMoves = (king << 1 & notAFile) | (king << 7 & notHFile) | king << 8 | (king << 9 & notAFile) | (king >>> 1 & notHFile) | (king >>> 7 & notAFile) | king >>> 8 | (king >>> 9 & notHFile);
        possibleKingMoves = possibleKingMoves & ~currentTeam;

        MoveMasks.printBitBoard(possibleKingMoves);

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
            if(!hasShortRookMoved && ((current.whitePieces | current.blackPieces) & shortCastlingMask) == 0){
                kingMoves.add(shortCastle);
            }

            if(!hasLongRookMoved && ((current.whitePieces | current.blackPieces) & longCastlingMask) == 0){
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

        int queenIndex = Long.numberOfTrailingZeros(queen);

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

        for(int i = 0; i < 64; i++){
            if((result & (1L << i)) != 0L){
                queenMoves.add(new Move(queenIndex, i, PieceType.QUEEN));
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

        //Bis zur If-Bedingung werden alle Züge für den ersten Turm generiert, für den zweiten Turm das Gleiche machen, statt firstRookIndex -> lastRookIndex
        int firstRookIndex = 63 - Long.numberOfLeadingZeros(rooks), lastRookIndex = Long.numberOfTrailingZeros(rooks);

        int maskedBlockerIndex = Long.numberOfTrailingZeros(blockers & moveMasks.rays(Direction.NORTH, firstRookIndex));
        long result = (moveMasks.rays(Direction.NORTH, firstRookIndex) & ~moveMasks.rays(Direction.NORTH, maskedBlockerIndex)) & ~currentTeam;

        maskedBlockerIndex = Long.numberOfTrailingZeros(blockers & moveMasks.rays(Direction.EAST, firstRookIndex));
        result = result | (moveMasks.rays(Direction.EAST, firstRookIndex) & ~moveMasks.rays(Direction.EAST, maskedBlockerIndex)) & ~currentTeam;

        maskedBlockerIndex = 63 - Long.numberOfLeadingZeros(blockers & moveMasks.rays(Direction.SOUTH, firstRookIndex));
        result = result | (moveMasks.rays(Direction.SOUTH, firstRookIndex) & ~moveMasks.rays(Direction.SOUTH, maskedBlockerIndex)) & ~currentTeam;

        maskedBlockerIndex = 63 - Long.numberOfLeadingZeros(blockers & moveMasks.rays(Direction.WEST, firstRookIndex));
        result = result | (moveMasks.rays(Direction.WEST, firstRookIndex) & ~moveMasks.rays(Direction.WEST, maskedBlockerIndex)) & ~currentTeam;

        for(int i = 0; i < 64; i++){
            if((result & (1L << i)) != 0L){
                rookMoves.add(new Move(firstRookIndex, i, PieceType.ROOK));
            }
        }

        if(firstRookIndex != lastRookIndex){
            //Es gibt noch einen zweiten Turm
            maskedBlockerIndex = Long.numberOfTrailingZeros(blockers & moveMasks.rays(Direction.NORTH, lastRookIndex));
            result = (moveMasks.rays(Direction.NORTH, lastRookIndex) & ~moveMasks.rays(Direction.NORTH, maskedBlockerIndex)) & ~currentTeam;

            maskedBlockerIndex = Long.numberOfTrailingZeros(blockers & moveMasks.rays(Direction.EAST, lastRookIndex));
            result = result | (moveMasks.rays(Direction.EAST, lastRookIndex) & ~moveMasks.rays(Direction.EAST, maskedBlockerIndex)) & ~currentTeam;

            maskedBlockerIndex = 63 - Long.numberOfLeadingZeros(blockers & moveMasks.rays(Direction.SOUTH, lastRookIndex));
            result = result | (moveMasks.rays(Direction.SOUTH, lastRookIndex) & ~moveMasks.rays(Direction.SOUTH, maskedBlockerIndex)) & ~currentTeam;

            maskedBlockerIndex = 63 - Long.numberOfLeadingZeros(blockers & moveMasks.rays(Direction.WEST, lastRookIndex));
            result = result | (moveMasks.rays(Direction.WEST, lastRookIndex) & ~moveMasks.rays(Direction.WEST, maskedBlockerIndex)) & ~currentTeam;

            for(int i = 0; i < 64; i++){
                if((result & (1L << i)) != 0L){
                    rookMoves.add(new Move(lastRookIndex, i, PieceType.ROOK));
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

        //Bis zur If-Bedingung werden alle Züge für den ersten Läufer generiert, für den zweiten Läufer das Gleiche machen, statt firstBishopIndex -> lastBishopIndex
        int firstBishopIndex = 63 - Long.numberOfLeadingZeros(bishops), lastBishopIndex = Long.numberOfTrailingZeros(bishops);

        int maskedBlockerIndex = Long.numberOfTrailingZeros(blockers & moveMasks.rays(Direction.NORTH_EAST, firstBishopIndex));
        long result = (moveMasks.rays(Direction.NORTH_EAST, firstBishopIndex) & ~moveMasks.rays(Direction.NORTH_EAST, maskedBlockerIndex)) & ~currentTeam;

        maskedBlockerIndex = Long.numberOfTrailingZeros(blockers & moveMasks.rays(Direction.NORTH_WEST, firstBishopIndex));
        result = result | (moveMasks.rays(Direction.NORTH_WEST, firstBishopIndex) & ~moveMasks.rays(Direction.NORTH_WEST, maskedBlockerIndex)) & ~currentTeam;

        maskedBlockerIndex = 63 - Long.numberOfLeadingZeros(blockers & moveMasks.rays(Direction.SOUTH_EAST, firstBishopIndex));
        result = result | (moveMasks.rays(Direction.SOUTH_EAST, firstBishopIndex) & ~moveMasks.rays(Direction.SOUTH_EAST, maskedBlockerIndex)) & ~currentTeam;

        maskedBlockerIndex = 63 - Long.numberOfLeadingZeros(blockers & moveMasks.rays(Direction.SOUTH_WEST, firstBishopIndex));
        result = result | (moveMasks.rays(Direction.SOUTH_WEST, firstBishopIndex) & ~moveMasks.rays(Direction.SOUTH_WEST, maskedBlockerIndex)) & ~currentTeam;

        for(int i = 0; i < 64; i++){
            if((result & (1L << i)) != 0L){
                bishopMoves.add(new Move(firstBishopIndex, i, PieceType.BISHOP));
            }
        }

        if(firstBishopIndex != lastBishopIndex){
            //Es gibt noch einen zweiten Läufer
            maskedBlockerIndex = Long.numberOfTrailingZeros(blockers & moveMasks.rays(Direction.NORTH_EAST, lastBishopIndex));
            result = (moveMasks.rays(Direction.NORTH_EAST, lastBishopIndex) & ~moveMasks.rays(Direction.NORTH_EAST, maskedBlockerIndex)) & ~currentTeam;

            maskedBlockerIndex = Long.numberOfTrailingZeros(blockers & moveMasks.rays(Direction.NORTH_WEST, lastBishopIndex));
            result = result | (moveMasks.rays(Direction.NORTH_WEST, lastBishopIndex) & ~moveMasks.rays(Direction.NORTH_WEST, maskedBlockerIndex)) & ~currentTeam;

            maskedBlockerIndex = 63 - Long.numberOfLeadingZeros(blockers & moveMasks.rays(Direction.SOUTH_EAST, lastBishopIndex));
            result = result | (moveMasks.rays(Direction.SOUTH_EAST, lastBishopIndex) & ~moveMasks.rays(Direction.SOUTH_EAST, maskedBlockerIndex)) & ~currentTeam;

            maskedBlockerIndex = 63 - Long.numberOfLeadingZeros(blockers & moveMasks.rays(Direction.SOUTH_WEST, lastBishopIndex));
            result = result | (moveMasks.rays(Direction.SOUTH_WEST, lastBishopIndex) & ~moveMasks.rays(Direction.SOUTH_WEST, maskedBlockerIndex)) & ~currentTeam;

            for(int i = 0; i < 64; i++){
                if((result & (1L << i)) != 0L){
                    bishopMoves.add(new Move(lastBishopIndex, i, PieceType.BISHOP));
                }
            }

        }

        return bishopMoves.toArray(new Move[0]);
    }

    public static Move[] generateKnightMoves(Board current){
        return new Move[0];
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
