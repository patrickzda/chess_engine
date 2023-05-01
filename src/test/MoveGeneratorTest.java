package test;

import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.Board;
import engine.representation.Color;
import engine.representation.Move;
import engine.representation.PieceType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static engine.representation.Color.*;
import static org.junit.jupiter.api.Assertions.*;

public class MoveGeneratorTest {
    @Test
    public void generateRookMoves() {
        MoveMasks m = new MoveMasks();

        Board[] boards = {
                new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR", WHITE),
                new Board("8/8/4r3/3R4/8/8/8/8", WHITE),
                new Board("8/8/4r3/3R4/8/8/8/8", BLACK),
                new Board("8/8/3Rr3/3rR3/8/8/8/8", WHITE),
                new Board("8/8/3Rr3/3rR3/8/8/8/8", BLACK),
                new Board("8/8/2r1R3/8/2r1R3/8/8/8", WHITE),
                new Board("8/8/2r1R3/8/2r1R3/8/8/8", BLACK),
                new Board("rnbqkbnr/1pppppp1/8/8/8/8/1PPPPPP1/1NBQKBN1", WHITE),
                new Board("1nbqkbn1/1pppppp1/8/8/8/8/1PPPPPP1/RNBQKBNR", BLACK),
        };

        Move[][] results = {
                generateTestMoves(new long[] {0L, 0L}, new int[] {0, 7}, new PieceType[]{PieceType.ROOK, PieceType.ROOK}),
                generateTestMoves(new long[] {578722409201797128L}, new int[] {35}, new PieceType[]{PieceType.ROOK}),
                generateTestMoves(new long[] {1157687956502220816L}, new int[] {44}, new PieceType[]{PieceType.ROOK}),
                generateTestMoves(new long[] {578737875244285952L, 18588887945232L}, new int[] {43, 36}, new PieceType[]{PieceType.ROOK, PieceType.ROOK}),
                generateTestMoves(new long[] {8895012014088L, 1157680259651338240L}, new int[] {35, 44}, new PieceType[]{PieceType.ROOK, PieceType.ROOK}),
                generateTestMoves(new long[] {1157684657697849344L, 72679952400L}, new int[] {44, 28}, new PieceType[]{PieceType.ROOK, PieceType.ROOK}),
                generateTestMoves(new long[] {289385980052373504L, 17633117188L}, new int[] {42, 26}, new PieceType[]{PieceType.ROOK, PieceType.ROOK}),
                generateTestMoves(new long[] {0L, 0L}, new int[] {0, 0}, new PieceType[]{PieceType.ROOK, PieceType.ROOK}),
                generateTestMoves(new long[] {0L, 0L}, new int[] {0, 0}, new PieceType[]{PieceType.ROOK, PieceType.ROOK}),
        };

        for (int i = 0; i < results.length; i++) {
            assertTrue(hasSameMoves(results[i], MoveGenerator.generateRookMoves(boards[i], m)));
        }
    }

    @Test
    public void generateBishopMoves() {
        MoveMasks m = new MoveMasks();

        Board[] boards = {
                new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR", WHITE),
                new Board("8/8/3b4/8/3B4/8/8/8", WHITE),
                new Board("8/8/3b4/8/3B4/8/8/8", BLACK),
                new Board("8/8/4bb2/8/3B2B1/8/8/8", WHITE),
                new Board("8/8/4bb2/8/3B2B1/8/8/8", BLACK),
                new Board("8/1p5p/2P3p1/8/4B3/8/2p3P1/1p5p", WHITE),
                new Board("8/1p5p/2P3p1/8/4b3/8/2p3P1/1p5p", BLACK),
                new Board("rnbqkbnr/pppppppp/8/8/8/8/P1P2P1P/RN1QK1NR", WHITE),
                new Board("rn1qk1nr/p1p2p1p/8/8/8/8/PPPPPPPP/RNBQKBNR", BLACK),
        };

        Move[][] results = {
                generateTestMoves(new long[] {0L, 0L}, new int[] {2, 5}, new PieceType[]{PieceType.BISHOP, PieceType.BISHOP}),
                generateTestMoves(new long[] {-9205038694072573375L}, new int[] {27}, new PieceType[]{PieceType.BISHOP}),
                generateTestMoves(new long[] {2455587783297826816L}, new int[] {43}, new PieceType[]{PieceType.BISHOP}),
                generateTestMoves(new long[] {318944272720449L, 18279391301640L}, new int[] {27, 30}, new PieceType[]{PieceType.BISHOP, PieceType.BISHOP}),
                generateTestMoves(new long[] {-8624392940535414784L, 4911175566587199744L}, new int[] {45, 44}, new PieceType[]{PieceType.BISHOP, PieceType.BISHOP}),
                generateTestMoves(new long[] {70540545491968L}, new int[] {28}, new PieceType[]{PieceType.BISHOP}),
                generateTestMoves(new long[] {4569847840768L}, new int[] {28}, new PieceType[]{PieceType.BISHOP}),
                generateTestMoves(new long[] {0L, 0L}, new int[] {0, 0}, new PieceType[]{PieceType.BISHOP, PieceType.BISHOP}),
                generateTestMoves(new long[] {0L, 0L}, new int[] {0, 0}, new PieceType[]{PieceType.BISHOP, PieceType.BISHOP}),
        };

        for (int i = 0; i < results.length; i++) {
            assertTrue(hasSameMoves(results[i], MoveGenerator.generateBishopMoves(boards[i], m)));
        }
    }

    @Test
    public void generateQueenMoves() {
        MoveMasks m = new MoveMasks();

        Board[] boards = {
                new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR", WHITE),
                new Board("8/8/8/4q3/2Q5/8/8/8", WHITE),
                new Board("8/8/8/4q3/2Q5/8/8/8", BLACK),
                new Board("8/8/8/P1p1p3/8/P1Q1P3/8/p1p1P3", WHITE),
                new Board("8/8/8/P1p1p3/8/P1q1P3/8/p1p1P3", BLACK),
                new Board("rn1qkbnr/pp3ppp/3p4/8/8/8/PP3PPP/RN1QKBNR", WHITE),
                new Board("rn1qkbnr/pp3ppp/3p4/8/8/8/PP3PPP/RN1QKBNR", BLACK),
                new Board("rnbqkbnr/pppppppp/8/8/8/8/PP3PPP/RN2KBNR", WHITE),
                new Board("rn2kbnr/pp3ppp/8/8/8/8/PPPPPPPP/RNBQKBNR", BLACK),
        };

        Move[][] results = {
                generateTestMoves(new long[] {0L}, new int[] {3}, new PieceType[]{PieceType.QUEEN}),
                generateTestMoves(new long[] {4910072647826412836L}, new int[] {26}, new PieceType[]{PieceType.QUEEN}),
                generateTestMoves(new long[] {-7902628846034972143L}, new int[] {36}, new PieceType[]{PieceType.QUEEN}),
                generateTestMoves(new long[] {86134885893L}, new int[] {18}, new PieceType[]{PieceType.QUEEN}),
                generateTestMoves(new long[] {4531621392L}, new int[] {18}, new PieceType[]{PieceType.QUEEN}),
                generateTestMoves(new long[] {9381436070916L}, new int[] {3}, new PieceType[]{PieceType.QUEEN}),
                generateTestMoves(new long[] {296149340215312384L}, new int[] {59}, new PieceType[]{PieceType.QUEEN}),
                generateTestMoves(new long[] {0L}, new int[] {0}, new PieceType[]{PieceType.QUEEN}),
                generateTestMoves(new long[] {0L}, new int[] {0}, new PieceType[]{PieceType.QUEEN}),
        };

        for (int i = 0; i < results.length; i++) {
            boolean res = hasSameMoves(results[i], MoveGenerator.generateQueenMoves(boards[i], m));
            if (!res) {
                System.out.println("FEHLER! Index: " + i);
            }
            assertTrue(res);
        }
    }

    // generiert ein Array von Move für Testzwecke ACHTUNG! alle 3 Argumente müssen die gleiche Anzahl an Elementen enthalten!
    // params:  bitMasks: Bitmasken für alle Felder, auf die gezogen werden soll
    //          indizes: indizes von denen jeder Move starten soll
    //          pieces: die Spielfiguren, die diese Züge machen sollen
    public static Move[] generateTestMoves(long[] bitMasks, int[] indizes, PieceType[] pieces) {
        ArrayList<Move> testMoves = new ArrayList<Move>();

        for (int i = 0; i < bitMasks.length; i++) {
            for (int j = 0; j < 64; j++) {
                long bit = (1L << j);
                if ((bitMasks[i] & bit) != 0) {
                    testMoves.add(new Move(indizes[i], Long.numberOfTrailingZeros(bitMasks[i] & bit), pieces[i]));
                }
            }
        }

        return testMoves.toArray(new Move[0]);
    }

    // Testet, ob in zwei Move-Arrays die gleichen Moves enthalten sind unabhängig von der Reihenfolge
    // ACHTUNG! Es wird nicht überprüft, ob auch alle Flags gleich gesetzt sind! Es wird nur nach start und Endfeld und PieceType getestet
    public static boolean hasSameMoves(Move[] moves1, Move[] moves2) {
        boolean found;
        for (Move move1: moves1) {
            found = false;
            for (Move move2: moves2) {
                if ((move1.getStartFieldIndex() == move2.getStartFieldIndex()) && (move1.getEndFieldIndex() == move2.getEndFieldIndex()) && (move1.getPieceType() == move2.getPieceType())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

}
