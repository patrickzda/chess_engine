package engine.representation;

import java.math.BigInteger;

public class Board {
    public long whitePieces, blackPieces, kings, queens, rooks, bishops, knights, pawns;

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

    private String fillLeadingZeros(Long l){
        return String.format("%064d", new BigInteger(Long.toBinaryString(l)));
    }

    public String toFENString(){
        StringBuilder stringBuilder = new StringBuilder();
        String[] pieceIdentifiers = new String[]{"K", "Q", "R", "B", "N", "P"};
        String[] whiteBitboardStrings = new String[]{fillLeadingZeros(whitePieces & kings), fillLeadingZeros(whitePieces & queens), fillLeadingZeros(whitePieces & rooks), fillLeadingZeros(whitePieces & bishops), fillLeadingZeros(whitePieces & knights), fillLeadingZeros(whitePieces & pawns)};
        String[] blackBitboardStrings = new String[]{fillLeadingZeros(blackPieces & kings), fillLeadingZeros(blackPieces & queens), fillLeadingZeros(blackPieces & rooks), fillLeadingZeros(blackPieces & bishops), fillLeadingZeros(blackPieces & knights), fillLeadingZeros(blackPieces & pawns)};
        int freeSpaceCounter = 0;
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
                    freeSpaceCounter++;
                }else if(freeSpaceCounter > 0){
                    stringBuilder.insert(stringBuilder.toString().length() - 2, freeSpaceCounter);
                    freeSpaceCounter = 0;
                }

                if((7 - y) == 0){
                    if(freeSpaceCounter > 0){
                        stringBuilder.append(freeSpaceCounter);
                        freeSpaceCounter = 0;
                    }
                    stringBuilder.append("/");
                }
            }
        }

        return stringBuilder.substring(0, stringBuilder.toString().length() - 1);
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        String[] pieceIdentifiers = new String[]{"K", "Q", "R", "B", "N", "P"};
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
