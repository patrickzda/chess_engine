package engine.move_generation;

import engine.representation.*;

import java.util.ArrayList;

public class MoveGenerator {

    static final long notAFile = -72340172838076674L, notHFile = 9187201950435737471L, onlySecondRank = 65280L, onlySeventhRank = 71776119061217280L;

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
        return new Move[0];
    }

    public static Move[] generateQueenMoves(Board current){
        return new Move[0];
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

        //MoveMasks.printBitBoard(result);

        for(int i = 0; i < 64; i++){
            if((result & (1L << i)) != 0L){
                rookMoves.add(new Move(firstRookIndex, i, PieceType.ROOK));
            }
        }

        if(firstRookIndex != lastRookIndex){
            //Es gibt noch einen zweiten Turm

        }

        return rookMoves.toArray(new Move[0]);
    }

    public static Move[] generateBishopMoves(Board current){
        return new Move[0];
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
