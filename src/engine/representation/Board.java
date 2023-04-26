package engine.representation;

import java.math.BigInteger;

public class Board {
    /*
    Repräsentiert den aktuellen Spielzustand (Brett + welcher Spieler ist am Zug).
    Enthält Methoden zum Ausgeben und zum Konvertieren zwischen FEN-String und Bitboard-Repräsentation.
     */

    private final String[] pieceIdentifiers = new String[]{"K", "Q", "R", "B", "N", "P"};
    public long whitePieces, blackPieces, kings, queens, rooks, bishops, knights, pawns;
    private boolean isWhitesTurn = true;

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
    }

    private String fillLeadingZeros(long l){
        return String.format("%064d", new BigInteger(Long.toBinaryString(l)));
    }

    private long setBit(long l, long index){
        return l | (1L << index);
    }

    public boolean isWhitesTurn(){
        return isWhitesTurn;
    }

    public void changeTurn(){
        isWhitesTurn = !isWhitesTurn;
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
        return result.substring(0, result.length() - 1);
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
