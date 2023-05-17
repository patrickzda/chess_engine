package test;

import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.Board;
import engine.representation.Color;
import engine.representation.Move;
import engine.representation.PieceType;

import java.util.ArrayList;

import static java.lang.String.valueOf;

public class GameLoader {

    public static Board loadPGN(String pgn) {
        Board b = new Board();
        MoveMasks m = new MoveMasks();

        String[] moveStrings = isolateMoves(pgn);

        for (String moveString : moveStrings) {
            ArrayList<Move> matches = new ArrayList<Move>();
            // alle möglichen Moves berechnen
            Move[] moves = MoveGenerator.generateLegalMoves(b, m);

            // Zielindex aus dem String berechnen
            int endIndex = getFieldIndex(moveString, b.getTurn());

            // finde alle Moves, die auf dem gleichen Feld landen
            for (Move move : moves) {
                if (move.getEndFieldIndex() == endIndex) {
                    matches.add(move);
                }
            }

            // nur ein Move gefunden? dann kann dieser ausgeführt werden
            if (matches.size() == 1) {
                b.doMove(matches.get(0));
                continue;
            }

            // mögliche Moves auf die vorherigen matches setzen
            moves = matches.toArray(new Move[0]);

            // finde alle Moves, die den gleichen PieceType verwenden
            for (Move move : moves) {
                if (move.getPieceType() == getPieceType(moveString)) {
                    matches.add(move);
                }
            }

            // nur ein Move gefunden? dann kann dieser ausgeführt werden
            if (matches.size() == 1) {
                b.doMove(matches.get(0));
                continue;
            }

            // mögliche Moves auf die vorherigen matches setzen
            moves = matches.toArray(new Move[0]);

        }

        return b;
    }

    private static PieceType getPawnConversionType (String moveString) {
        int index = moveString.length() - 1;
        if (moveString.charAt(moveString.length() - 1) == '+') {
            index--;
        }

        return switch(moveString.charAt(index)) {
            case 'Q' -> PieceType.QUEEN;
            case 'R' -> PieceType.ROOK;
            case 'B' -> PieceType.BISHOP;
            default -> PieceType.KNIGHT;
        };
    }

    private static PieceType getPieceType(String moveString) {
        if (moveString.equals("O-O") || moveString.equals("O-O-O")) {
            return PieceType.KING;
        }

        return switch(moveString.charAt(0)) {
            case 'N' -> PieceType.KNIGHT;
            case 'R' -> PieceType.ROOK;
            case 'B' -> PieceType.BISHOP;
            case 'K' -> PieceType.KING;
            case 'Q' -> PieceType.QUEEN;
            default -> PieceType.PAWN;
        };

    }

    private static boolean isNumber(Character c) {
        return switch (c) {
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> true;
            default -> false;
        };
    }
    private static String[] isolateMoves(String pgn) {
        ArrayList<String> moveStrings = new ArrayList<>();

        String[] parts = pgn.split(" ");

        for (int i = 0; i < parts.length; i++) {
            if (!isNumber(parts[i].charAt(0))) {
                moveStrings.add(parts[i]);
            }
        }

        return moveStrings.toArray(new String[0]);
    }

    private static int getFieldIndex(String moveString, Color player) {
        if (moveString.equals("O-O")) {
            if (player == Color.WHITE) {
                return 6;
            }

            else {
                return 62;
            }
        }

        if (moveString.equals("O-O-O")) {
            if (player == Color.WHITE) {
                return 2;
            }

            else {
                return 58;
            }
        }
        int letterIndex = moveString.length() - 2;
        if (moveString.chars().filter(ch -> ch == '+').count() > 0) {
            letterIndex--;
        }
        if (moveString.chars().filter(ch -> ch == '.').count() > 0) {
            letterIndex = letterIndex - 4;
        }

        String columnIdentifiers = "abcdefgh";

        int fileIndex = columnIdentifiers.indexOf(moveString.charAt(letterIndex));
        int rankIndex = Integer.parseInt(valueOf(moveString.charAt(letterIndex + 1))) - 1;

        return rankIndex * 8 + fileIndex;
    }
     private static Move getMove(String moveString) {
        String[] columnNames = {"a", "b", "c", "d", "e", "f", "g", "h"};

        int startIndex = 0;
        int endIndex = 0;


        return new Move(startIndex, endIndex, PieceType.PAWN);
    }


}
