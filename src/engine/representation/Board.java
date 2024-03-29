package engine.representation;

import engine.ai.Evaluation;
import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;

import java.math.BigInteger;
import java.util.ArrayList;


public class Board {
    private final String[] pieceIdentifiers = new String[]{"K", "Q", "R", "B", "N", "P"};
    public long whitePieces, blackPieces, kings, queens, rooks, bishops, knights, pawns;
    public final ArrayList<Move> moves = new ArrayList<Move>();
    private Color turn = Color.WHITE;
    private boolean hasWhiteKingMoved = false, hasBlackKingMoved = false, hasWhiteLongRookMoved = false, hasWhiteShortRookMoved = false, hasBlackLongRookMoved = false, hasBlackShortRookMoved = false;
    private int movesSinceLastPawnMoveOrCapture = 0, totalMoves = 1;

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
        if(!castlingRights.contains("K") | (rooks & whitePieces & 128L) == 0){
            hasWhiteShortRookMoved = true;
        }
        if(!castlingRights.contains("Q") | (rooks & whitePieces & 1L) == 0){
            hasWhiteLongRookMoved = true;
        }
        if(!castlingRights.contains("k") | (rooks & blackPieces & -9223372036854775808L) == 0){
            hasBlackShortRookMoved = true;
        }
        if(!castlingRights.contains("q") | (rooks & blackPieces & 72057594037927936L) == 0){
            hasBlackLongRookMoved = true;
        }
        if(castlingRights.equals("-")){
            hasWhiteKingMoved = true;
            hasBlackKingMoved = true;
        }else{
            if((kings & whitePieces & 16L) == 0L){
                hasWhiteKingMoved = true;
            }
            if((kings & blackPieces & 1152921504606846976L) == 0L){
                hasBlackKingMoved = true;
            }
        }

        movesSinceLastPawnMoveOrCapture = Integer.parseInt(sections[4]);
        totalMoves = Integer.parseInt(sections[5]);
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

    public boolean getHasBlackLongRookMoved() {
        return hasBlackLongRookMoved;
    }

    public boolean getHasBlackShortRookMoved() {
        return hasBlackShortRookMoved;
    }

    public int getMovesSinceLastPawnMoveOrCapture() {
        return movesSinceLastPawnMoveOrCapture;
    }

    public int getTotalMoves() {
        return totalMoves;
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

        move.movesSinceLastPawnMoveOrCapture = movesSinceLastPawnMoveOrCapture;

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

            if(move.blackPieces != blackPieces | move.getPieceType() == PieceType.PAWN){
                movesSinceLastPawnMoveOrCapture = 0;
            }else{
                movesSinceLastPawnMoveOrCapture++;
            }
        }else{
            totalMoves++;
            blackPieces = blackPieces ^ startPositionMask;
            blackPieces = blackPieces | endPositionMask;
            whitePieces = whitePieces & ~endPositionMask;
            turn = Color.WHITE;

            if(move.whitePieces != whitePieces | move.getPieceType() == PieceType.PAWN){
                movesSinceLastPawnMoveOrCapture = 0;
            }else{
                movesSinceLastPawnMoveOrCapture++;
            }
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
        movesSinceLastPawnMoveOrCapture = lastMove.movesSinceLastPawnMoveOrCapture;
        if(turn == Color.WHITE){
            totalMoves--;
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
        result = result.replace("········", "8");
        result = result.replace("·······", "7");
        result = result.replace("······", "6");
        result = result.replace("·····", "5");
        result = result.replace("····", "4");
        result = result.replace("···", "3");
        result = result.replace("··", "2");
        result = result.replace("·", "1");
        result = result.substring(0, result.length() - 1);

        if(turn == Color.WHITE){
            result = result + " w ";
        }else{
            result = result + " b ";
        }

        if(hasWhiteKingMoved && hasBlackKingMoved){
            result = result + "-";
        }else if((!hasWhiteKingMoved && !hasWhiteShortRookMoved) || (!hasWhiteKingMoved && !hasWhiteLongRookMoved) || (!hasBlackKingMoved && !hasBlackShortRookMoved) || (!hasBlackKingMoved && !hasBlackLongRookMoved)){
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
        }else{
            result = result + "-";
        }

        result = result + " - " + movesSinceLastPawnMoveOrCapture + " " + totalMoves;

        return result;
    }

    public GameState getGameState(MoveMasks moveMasks) {
        Move[] legalMoves = MoveGenerator.generateLegalMoves(this, moveMasks);
        if (isGameLost(moveMasks, legalMoves.length)) {
            if (getTurn() == Color.WHITE) {
                return GameState.BLACK_WON;
            }
            return GameState.WHITE_WON;
        }

        if (movesSinceLastPawnMoveOrCapture >= 50) {
            return GameState.DRAW;
        }

        if (MoveGenerator.generateLegalMoves(this, moveMasks).length == 0) {
            return GameState.DRAW;
        }

        int gameStateOccurance = 1;

        for (int i = 0; i < moves.size(); i++) {
            if (moves.get(i).pawns == pawns &&
                moves.get(i).rooks == rooks &&
                moves.get(i).bishops == bishops &&
                moves.get(i).knights == knights &&
                moves.get(i).queens == queens &&
                moves.get(i).kings == kings &&
                moves.get(i).whitePieces == whitePieces &&
                moves.get(i).blackPieces == blackPieces
               ) {
                gameStateOccurance++;
            }
            if (gameStateOccurance == 3) {
                return GameState.DRAW;
            }
        }

        if (totalMoves < 15) {
            return GameState.START_GAME;
        }

        if (countPieces() >= 8) {
            return GameState.MID_GAME;
        }

        return GameState.END_GAME;
    }

    private int countPieces() {
        return Evaluation.getSetBits(blackPieces | whitePieces);
    }

    private boolean isKingOfTheHill() {
        long HILL_TOP = 103481868288L;
        return ((kings & HILL_TOP) != 0);
    }

    //Prüft, ob der Spieler, der aktuell dran ist, verloren hat
    public boolean isGameLost(MoveMasks moveMasks, int legalMovesLength) {
        boolean checkMate = false;
        Color currentColor = getTurn();
        long currentTeam;

        Color otherColor;
        if (currentColor == Color.WHITE) {
            otherColor = Color.BLACK;
            currentTeam = whitePieces;
        }
        else {
            otherColor = Color.WHITE;
            currentTeam = blackPieces;
        }

        if (legalMovesLength == 0 && MoveGenerator.isAttacked(this, moveMasks, Long.numberOfTrailingZeros(kings & currentTeam), otherColor)) {
            checkMate = true;
        }

        return isKingOfTheHill() || checkMate;
    }

    public boolean isInCheck(MoveMasks masks){
        if(turn == Color.WHITE) {
            return MoveGenerator.isAttacked(this, masks, Long.numberOfTrailingZeros(kings & whitePieces), Color.BLACK);
        }else {
            return MoveGenerator.isAttacked(this, masks, Long.numberOfTrailingZeros(kings & blackPieces), Color.WHITE);
        }
    }

    public PieceType getCapturedPieceType(Move move){
        long enemyPieces;
        long beforePawns, beforeKnights, beforeBishops, beforeRooks, beforeQueens;

        if(turn == Color.WHITE){
            beforePawns = blackPieces & pawns;
            beforeKnights = blackPieces & knights;
            beforeBishops = blackPieces & bishops;
            beforeRooks = blackPieces & rooks;
            beforeQueens = blackPieces & queens;
        }else{
            beforePawns = whitePieces & pawns;
            beforeKnights = whitePieces & knights;
            beforeBishops = whitePieces & bishops;
            beforeRooks = whitePieces & rooks;
            beforeQueens = whitePieces & queens;
        }

        PieceType type = null;
        doMove(move);

        if(turn == Color.WHITE){
            enemyPieces = whitePieces;
        }else{
            enemyPieces = blackPieces;
        }

        if(beforePawns != (enemyPieces & pawns)){
            type = PieceType.PAWN;
        }else if(beforeKnights != (enemyPieces & knights)){
            type = PieceType.KNIGHT;
        }else if(beforeBishops != (enemyPieces & bishops)){
            type = PieceType.BISHOP;
        }else if(beforeRooks != (enemyPieces & rooks)){
            type = PieceType.ROOK;
        }else if(beforeQueens != (enemyPieces & queens)){
            type = PieceType.QUEEN;
        }

        undoLastMove();
        return type;
    }

    public void doNullMove(){
        if(turn == Color.WHITE){
            turn = Color.BLACK;
        }else{
            turn = Color.WHITE;
        }
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
                    stringBuilder.append("·");
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
