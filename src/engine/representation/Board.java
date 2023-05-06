package engine.representation;

import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;


public class Board {
    private final String[] pieceIdentifiers = new String[]{"K", "Q", "R", "B", "N", "P"};
    public long whitePieces, blackPieces, kings, queens, rooks, bishops, knights, pawns;
    private final ArrayList<Move> moves = new ArrayList<Move>();
    private Color turn = Color.WHITE;
    private boolean hasWhiteKingMoved = false, hasBlackKingMoved = false, hasWhiteLongRookMoved = false, hasWhiteShortRookMoved = false, hasBlackLongRookMoved = false, hasBlackShortRookMoved = false;
    private final long HILL_TOP = 103481868288L;

    public Board(){
        whitePieces = 65535L;
        blackPieces = -281474976710656L;
        kings = 1152921504606846992L;
        queens = 576460752303423496L;
        rooks = -9151314442816847743L;
        bishops = 2594073385365405732L;
        knights = 4755801206503243842L;
        pawns = 71776119061282560L;
    }

    public Board(String fen){
        String[] sections = fen.split(" ");

        fen = fen.replace("/", "");
        fen = fen.replace("8", "        ");
        fen = fen.replace("7", "       ");
        fen = fen.replace("6", "      ");
        fen = fen.replace("5", "     ");
        fen = fen.replace("4", "    ");
        fen = fen.replace("3", "   ");
        fen = fen.replace("2", "  ");
        fen = fen.replace("1", " ");
        for(int x = 0; x < 8; x++){
            for(int y = 0; y < 8; y++){
                int charIndex = x * 8 + y;
                int i = (7 - x) * 8 + y;
                if(fen.charAt(charIndex) != ' '){
                    if(Character.toUpperCase(fen.charAt(charIndex)) == fen.charAt(charIndex)){
                        whitePieces = setBit(whitePieces, i);
                    }else{
                        blackPieces = setBit(blackPieces, i);
                    }
                    switch(Character.toUpperCase(fen.charAt(charIndex))){
                        case 'K':
                            kings = setBit(kings, i);
                            break;
                        case 'Q':
                            queens = setBit(queens, i);
                            break;
                        case 'R':
                            rooks = setBit(rooks, i);
                            break;
                        case 'B':
                            bishops = setBit(bishops, i);
                            break;
                        case 'N':
                            knights = setBit(knights, i);
                            break;
                        case 'P':
                            pawns = setBit(pawns, i);
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        if(sections[1].equals("w")){
            turn = Color.WHITE;
        }else{
            turn = Color.BLACK;
        }

        String castlingRights = sections[2];
        if(!castlingRights.contains("K")){
            hasWhiteShortRookMoved = true;
        }
        if(!castlingRights.contains("Q")){
            hasWhiteLongRookMoved = true;
        }
        if(!castlingRights.contains("k")){
            hasBlackShortRookMoved = true;
        }
        if(!castlingRights.contains("q")){
            hasBlackLongRookMoved = true;
        }
        if(castlingRights.equals("-")){
            hasWhiteKingMoved = true;
            hasBlackKingMoved = true;
        }
    }

    private String fillLeadingZeros(long l){
        return String.format("%064d", new BigInteger(Long.toBinaryString(l)));
    }

    private long setBit(long l, long index){
        return l | (1L << index);
    }

    public Color getTurn(){
        return turn;
    }

    public boolean getHasWhiteKingMoved() {
        return hasWhiteKingMoved;
    }

    public boolean getHasBlackKingMoved() {
        return hasBlackKingMoved;
    }

    public boolean getHasWhiteLongRookMoved() {
        return hasWhiteLongRookMoved;
    }

    public boolean getHasWhiteShortRookMoved() {
        return hasWhiteShortRookMoved;
    }

    public boolean gettHasBlackLongRookMoved() {
        return hasBlackLongRookMoved;
    }

    public boolean getHasBlackShortRookMoved() {
        return hasBlackShortRookMoved;
    }

    public void doMove(Move move){
        long startPositionMask = (1L << move.getStartFieldIndex());
        long endPositionMask = (1L << move.getEndFieldIndex());

        move.whitePieces = whitePieces;
        move.blackPieces = blackPieces;
        move.kings = kings;
        move.queens = queens;
        move.rooks = rooks;
        move.bishops = bishops;
        move.knights = knights;
        move.pawns = pawns;

        move.hasWhiteKingMoved = hasWhiteKingMoved;
        move.hasWhiteLongRookMoved = hasWhiteLongRookMoved;
        move.hasWhiteShortRookMoved = hasWhiteShortRookMoved;

        move.hasBlackKingMoved = hasBlackKingMoved;
        move.hasBlackLongRookMoved = hasBlackLongRookMoved;
        move.hasBlackShortRookMoved = hasBlackShortRookMoved;

        queens = queens & ~endPositionMask;
        rooks = rooks & ~endPositionMask;
        bishops = bishops & ~endPositionMask;
        knights = knights & ~endPositionMask;
        pawns = pawns & ~endPositionMask;

        switch (move.getPieceType()){
            case KING:
                kings = kings ^ startPositionMask;
                kings = kings | endPositionMask;

                if(turn == Color.WHITE){
                    hasWhiteKingMoved = true;
                }else{
                    hasBlackKingMoved = true;
                }

                if(move.isCastling){
                    if(getTurn() == Color.WHITE){
                        if(move.getEndFieldIndex() == 6){
                            long shortWhiteCastleMask = 160L;
                            rooks = rooks ^ shortWhiteCastleMask;
                            whitePieces = whitePieces ^ shortWhiteCastleMask;
                        }else{
                            long longWhiteCastleMask = 9L;
                            rooks = rooks ^ longWhiteCastleMask;
                            whitePieces = whitePieces ^ longWhiteCastleMask;
                        }
                    }else{
                        if(move.getEndFieldIndex() == 62){
                            long shortBlackCastleMask = -6917529027641081856L;
                            rooks = rooks ^ shortBlackCastleMask;
                            blackPieces = blackPieces ^ shortBlackCastleMask;
                        }else{
                            long longBlackCastleMask = 648518346341351424L;
                            rooks = rooks ^ longBlackCastleMask;
                            blackPieces = blackPieces ^ longBlackCastleMask;
                        }
                    }
                }
                break;
            case QUEEN:
                queens = queens ^ startPositionMask;
                queens = queens | endPositionMask;
                break;
            case ROOK:
                rooks = rooks ^ startPositionMask;
                rooks = rooks | endPositionMask;

                if(turn == Color.WHITE){
                    if(move.getStartFieldIndex() == 0){
                        hasWhiteLongRookMoved = true;
                    }else if(move.getStartFieldIndex() == 7){
                        hasWhiteShortRookMoved = true;
                    }
                }else{
                    if(move.getStartFieldIndex() == 56){
                        hasBlackLongRookMoved = true;
                    }else if(move.getStartFieldIndex() == 63){
                        hasBlackShortRookMoved = true;
                    }
                }

                break;
            case BISHOP:
                bishops = bishops ^ startPositionMask;
                bishops = bishops | endPositionMask;
                break;
            case KNIGHT:
                knights = knights ^ startPositionMask;
                knights = knights | endPositionMask;
                break;
            case PAWN:
                pawns = pawns ^ startPositionMask;
                if(move.isPromotionToQueen){
                    queens = queens | endPositionMask;
                }else if(move.isPromotionToRook){
                    rooks = rooks | endPositionMask;
                }else if(move.isPromotionToBishop){
                    bishops = bishops | endPositionMask;
                }else if(move.isPromotionToKnight){
                    knights = knights | endPositionMask;
                }else{
                    pawns = pawns | endPositionMask;
                    if(move.isEnPassant){
                        if(getTurn() == Color.WHITE){
                            blackPieces = blackPieces & ~(endPositionMask >>> 8);
                        }else{
                            whitePieces = whitePieces & ~(endPositionMask << 8);
                        }
                    }
                }
                break;
            default:
                break;
        }

        if(getTurn() == Color.WHITE){
            whitePieces = whitePieces ^ startPositionMask;
            whitePieces = whitePieces | endPositionMask;
            blackPieces = blackPieces & ~endPositionMask;
            turn = Color.BLACK;
        }else{
            blackPieces = blackPieces ^ startPositionMask;
            blackPieces = blackPieces | endPositionMask;
            whitePieces = whitePieces & ~endPositionMask;
            turn = Color.WHITE;
        }

        moves.add(move);
    }

    public void undoLastMove(){
        Move lastMove = moves.get(moves.size() - 1);
        whitePieces = lastMove.whitePieces;
        blackPieces = lastMove.blackPieces;
        kings = lastMove.kings;
        queens = lastMove.queens;
        rooks = lastMove.rooks;
        bishops = lastMove.bishops;
        knights = lastMove.knights;
        pawns = lastMove.pawns;
        hasWhiteKingMoved = lastMove.hasWhiteKingMoved;
        hasWhiteLongRookMoved = lastMove.hasWhiteLongRookMoved;
        hasWhiteShortRookMoved = lastMove.hasWhiteShortRookMoved;
        hasBlackKingMoved = lastMove.hasBlackKingMoved;
        hasBlackLongRookMoved = lastMove.hasBlackLongRookMoved;
        hasBlackShortRookMoved = lastMove.hasBlackShortRookMoved;
        if(turn == Color.WHITE){
            turn = Color.BLACK;
        }else{
            turn = Color.WHITE;
        }
        moves.remove(moves.size() - 1);
    }

    public int lastMoveEnPassantPushIndex(){
        if(moves.size() > 0 && moves.get(moves.size() - 1).isPawnTwoForward){
            return moves.get(moves.size() - 1).getEndFieldIndex();
        }
        return -1;
    }

    public String toFENString(){
        String result = toString();
        result = result.replace("\n", "/");
        result = result.replace("        ", "8");
        result = result.replace("       ", "7");
        result = result.replace("      ", "6");
        result = result.replace("     ", "5");
        result = result.replace("    ", "4");
        result = result.replace("   ", "3");
        result = result.replace("  ", "2");
        result = result.replace(" ", "1");
        result = result.substring(0, result.length() - 1);

        if(turn == Color.WHITE){
            result = result + " w ";
        }else{
            result = result + " b ";
        }

        if(hasWhiteKingMoved && hasBlackKingMoved){
            result = result + "-";
        }else{
            if(!hasWhiteKingMoved && !hasWhiteShortRookMoved){
                result = result + "K";
            }
            if(!hasWhiteKingMoved && !hasWhiteLongRookMoved){
                result = result + "Q";
            }
            if(!hasBlackKingMoved && !hasBlackShortRookMoved){
                result = result + "k";
            }
            if(!hasBlackKingMoved && !hasBlackLongRookMoved){
                result = result + "q";
            }
        }

        return result;
    }

    public boolean isKingOfTheHill() {
        return ((kings & HILL_TOP) != 0);
    }
    public boolean isGameWon(MoveMasks moveMasks) {
        boolean checkMate = false;
        Color currentColor = getTurn();
        long currentTeam, otherTeam;

        Color otherColor;
        if (currentColor == Color.WHITE) {
            otherColor = Color.BLACK;
            currentTeam = whitePieces;
            otherTeam = blackPieces;
        }
        else {
            otherColor = Color.WHITE;
            currentTeam = blackPieces;
            otherTeam = whitePieces;
        }


        Move[] moves = MoveGenerator.generateLegalMoves(this, moveMasks);
        if (moves.length == 0 && MoveGenerator.isAttacked(this, moveMasks, Long.numberOfTrailingZeros(kings & currentTeam), otherColor)) {
            checkMate = true;
        }

//        turn = otherColor;
//        moves = MoveGenerator.generateLegalMoves(this, moveMasks);
//        if (moves.length == 0 && MoveGenerator.isAttacked(this, moveMasks, Long.numberOfTrailingZeros(kings & enemyTeam), currentColor)) {
//            checkMate = true;
//        }
//
//        turn = currentColor;

        return isKingOfTheHill() | checkMate;
    }

    public static String indexToChessField(int index) {
        int rank = index % 8;
        int file = index / 8;
        String[] rows = {"1", "2", "3", "4", "5", "6", "7", "8"};
        String[] cols = {"a", "b", "c", "d", "e", "f", "g", "h"};

        return cols[rank] + rows[file];
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        String[] whiteBitboardStrings = new String[]{fillLeadingZeros(whitePieces & kings), fillLeadingZeros(whitePieces & queens), fillLeadingZeros(whitePieces & rooks), fillLeadingZeros(whitePieces & bishops), fillLeadingZeros(whitePieces & knights), fillLeadingZeros(whitePieces & pawns)};
        String[] blackBitboardStrings = new String[]{fillLeadingZeros(blackPieces & kings), fillLeadingZeros(blackPieces & queens), fillLeadingZeros(blackPieces & rooks), fillLeadingZeros(blackPieces & bishops), fillLeadingZeros(blackPieces & knights), fillLeadingZeros(blackPieces & pawns)};
        for(int x = 0; x < 8; x++){
            for(int y = 0; y < 8; y++){
                int i = x * 8 + (7 - y);
                boolean appended = false;
                for(int j = 0; j < 6; j++){
                    if(whiteBitboardStrings[j].charAt(i) == '1'){
                        stringBuilder.append(pieceIdentifiers[j]);
                        appended = true;
                    }else if(blackBitboardStrings[j].charAt(i) == '1'){
                        stringBuilder.append(pieceIdentifiers[j].toLowerCase());
                        appended = true;
                    }
                }
                if(!appended){
                    stringBuilder.append(" ");
                }
                if((7 - y) == 0){
                    stringBuilder.append("\n");
                }
            }
        }
        return stringBuilder.toString();
    }
}

//Alternative Symbole zum ausgeben des Schachbrettes
//String[] whitePieceIdentifiers = new String[]{"♚", "♛", "♜", "♝", "♞", "♟"};
//String[] blackPieceIdentifiers = new String[]{"♔", "♕", "♖", "♗", "♘", "♙"};
