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
            new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w -"),
            new Board("8/8/4r3/3R4/8/8/8/8 w -"),
            new Board("8/8/4r3/3R4/8/8/8/8 b -"),
            new Board("8/8/3Rr3/3rR3/8/8/8/8 w -"),
            new Board("8/8/3Rr3/3rR3/8/8/8/8 b -"),
            new Board("8/8/2r1R3/8/2r1R3/8/8/8 w -"),
            new Board("8/8/2r1R3/8/2r1R3/8/8/8 b -"),
            new Board("rnbqkbnr/1pppppp1/8/8/8/8/1PPPPPP1/1NBQKBN1 w -"),
            new Board("1nbqkbn1/1pppppp1/8/8/8/8/1PPPPPP1/RNBQKBNR b -"),
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
            new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w -"),
            new Board("8/8/3b4/8/3B4/8/8/8 w -"),
            new Board("8/8/3b4/8/3B4/8/8/8 b -"),
            new Board("8/8/4bb2/8/3B2B1/8/8/8 w -"),
            new Board("8/8/4bb2/8/3B2B1/8/8/8 b -"),
            new Board("8/1p5p/2P3p1/8/4B3/8/2p3P1/1p5p w -"),
            new Board("8/1p5p/2P3p1/8/4b3/8/2p3P1/1p5p b -"),
            new Board("rnbqkbnr/pppppppp/8/8/8/8/P1P2P1P/RN1QK1NR w -"),
            new Board("rn1qk1nr/p1p2p1p/8/8/8/8/PPPPPPPP/RNBQKBNR b -"),
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
            new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w -"),
            new Board("8/8/8/4q3/2Q5/8/8/8 w -"),
            new Board("8/8/8/4q3/2Q5/8/8/8 b -"),
            new Board("8/8/8/P1p1p3/8/P1Q1P3/8/p1p1P3 w -"),
            new Board("8/8/8/P1p1p3/8/P1q1P3/8/p1p1P3 b -"),
            new Board("rn1qkbnr/pp3ppp/3p4/8/8/8/PP3PPP/RN1QKBNR w -"),
            new Board("rn1qkbnr/pp3ppp/3p4/8/8/8/PP3PPP/RN1QKBNR b -"),
            new Board("rnbqkbnr/pppppppp/8/8/8/8/PP3PPP/RN2KBNR w -"),
            new Board("rn2kbnr/pp3ppp/8/8/8/8/PPPPPPPP/RNBQKBNR b -"),
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

        if (moves1.length != moves2.length) {
            System.out.println("FEHLER! nicht die gleiche Anzahl an Moves!");
            return false;
        }
        boolean found;
        boolean res = true;
        for (Move move1: moves1) {
            found = false;
            for (Move move2: moves2) {
                if ((move1.getStartFieldIndex() == move2.getStartFieldIndex()) && (move1.getEndFieldIndex() == move2.getEndFieldIndex()) && (move1.getPieceType() == move2.getPieceType())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("no match found for move: " + move1.toString());
                res = false;
            }
        }
        return res;
    }

    @Test
    void isAttacked() {
        Board b = new Board("rnbqkbnr/pppp1ppp/8/8/8/8/PPPP1PPP/RNBQKBNR w - - 0 1");
        assertFalse(MoveGenerator.isAttacked(b, new MoveMasks(), 4, BLACK));

        b = new Board("rnb1kbnr/ppppqppp/8/8/8/8/PPPP1PPP/RNBQKBNR w - - 0 1");
        assertTrue(MoveGenerator.isAttacked(b, new MoveMasks(), 4, BLACK));

        b = new Board("rnb1kbnr/ppppqppp/8/8/8/4N3/PPPP1PPP/RNBQKB1R w - - 0 1");
        assertFalse(MoveGenerator.isAttacked(b, new MoveMasks(), 4, BLACK));

        b = new Board("4k3/8/8/8/8/3qn3/2r3p1/4K3 w - - 0 1");
        assertFalse(MoveGenerator.isAttacked(b, new MoveMasks(), 4, BLACK));

        b = new Board("7k/8/8/8/8/2B5/8/4K3 b - - 0 1");
        assertTrue(MoveGenerator.isAttacked(b, new MoveMasks(), 63, WHITE));

        b = new Board("7k/8/8/4P3/8/2B5/8/4K3 b - - 0 1");
        assertFalse(MoveGenerator.isAttacked(b, new MoveMasks(), 63, WHITE));

        b = new Board("7k/8/5n2/4P3/8/2B5/8/4K1R1 b - - 0 1");
        assertFalse(MoveGenerator.isAttacked(b, new MoveMasks(), 63, WHITE));

        b = new Board("6bk/5Nb1/5n2/4P3/8/2B5/8/4K1R1 b - - 0 1");
        assertTrue(MoveGenerator.isAttacked(b, new MoveMasks(), 63, WHITE));

        b = new Board("7k/6P1/8/8/8/2B5/8/4K3 b - - 0 1");
        assertTrue(MoveGenerator.isAttacked(b, new MoveMasks(), 63, WHITE));
    }

    @Test
    void generateKingMoves() {
        Board[] boards = {
            new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"),
            new Board("rnbqk2r/pppp1ppp/5n2/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 0 1"),
            new Board("r1bqkbnr/pppp1ppp/8/4p3/2B1P3/3n1N2/PPPP1PPP/RNBQK2R w KQkq - 0 1"),
            new Board("r1bqkbnr/pppp1ppp/8/4p3/2B1P3/3n1N2/PPPP1PPP/RNBQ1RK1 w Qkq - 0 1"),
            new Board("4k3/8/8/8/8/6n1/3r4/4K3 w - - 0 1"),
            new Board("7k/8/8/3n4/8/8/3r4/7K b - - 0 1"),
            new Board("r3k2r/ppp2ppp/2n2n2/1qbpp3/4P1b1/1BN2N2/PPPP1PPP/R1BQK2R b KQkq - 0 1"),
            new Board("r3k2r/ppp2ppp/2n2n2/1qbpp3/4P1b1/1BN2N2/PPPP1PPP/R1BQK2R b - - 0 1"),
            new Board("r3k2r/ppp2ppp/2n2n2/1qbpp3/4P1b1/1BN2N2/PPPP1PPP/R1BQK2R b q - 0 1"),
        };

        Move[][] results = {
            generateTestMoves(new long[] {0L}, new int[] {4}, new PieceType[] {PieceType.KING}),
            generateTestMoves(new long[] {4192L}, new int[] {4}, new PieceType[] {PieceType.KING}),
            generateTestMoves(new long[] {4128L}, new int[] {4}, new PieceType[] {PieceType.KING}),
            generateTestMoves(new long[] {128L}, new int[] {6}, new PieceType[] {PieceType.KING}),
            generateTestMoves(new long[] {14376L}, new int[] {4}, new PieceType[] {PieceType.KING}),
            generateTestMoves(new long[] {4665729213955833856L}, new int[] {63}, new PieceType[] {PieceType.KING}),
            generateTestMoves(new long[] {7788975555537272832L}, new int[] {60}, new PieceType[] {PieceType.KING}),
            generateTestMoves(new long[] {2889059160958173184L}, new int[] {60}, new PieceType[] {PieceType.KING}),
            generateTestMoves(new long[] {3177289537109884928L}, new int[] {60}, new PieceType[] {PieceType.KING}),
        };

        for (int i = 0; i < results.length; i++) {
            boolean res = hasSameMoves(results[i], MoveGenerator.generateKingMoves(boards[i], new MoveMasks()));
            if (!res) {
                System.out.println("FEHLER! Index: " + i);
            }
            assertTrue(res);
        }

    }

    @Test
    void generateKnightMoves() {
        Board[] boards = {
            new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"),
            new Board("8/8/2n2N2/8/8/2N2n2/8/8 w - - 0 1"),
            new Board("8/8/2n2N2/8/8/2N2n2/8/8 b - - 0 1"),
            new Board("1p1PP1p1/P2pp2P/2n2N2/pp1PP1pp/PP1pp1PP/2N2n2/p2PP2p/1P1pp1P1 w - - 0 1"),
            new Board("1p1PP1p1/P2pp2P/2n2N2/pp1PP1pp/PP1pp1PP/2N2n2/p2PP2p/1P1pp1P1 b - - 0 1"),
            new Board("8/8/1rrrrr2/1rRRRr2/1rRNRr2/1rRRRr2/1rrrrr2/8 w - - 0 1"),
            new Board("8/8/1RRRRR2/1RrrrR2/1RrnrR2/1RrrrR2/1RRRRR2/8 b - - 0 1"),
            new Board("6bQ/PP1NbpP1/Pp2kpRK/2P3rB/R2PpB1p/p1P3nP/r1pn1q1p/1N6 w - - 0 1"),
            new Board("3BB2K/N1PpbpP1/pPp5/1Pq1Pbp1/3nPPrP/1RN1pnRQ/p2r3p/k7 b - - 0 1"),
        };

        Move[][] results = {
            generateTestMoves(new long[] {327680L, 10485760L}, new int[] {1, 6}, new PieceType[] {PieceType.KNIGHT, PieceType.KNIGHT}),
            generateTestMoves(new long[] {43234889994L, 5802888705324613632L}, new int[] {18, 45}, new PieceType[] {PieceType.KNIGHT, PieceType.KNIGHT}),
            generateTestMoves(new long[] {725361088165576704L, 345879119952L}, new int[] {42, 21}, new PieceType[] {PieceType.KNIGHT, PieceType.KNIGHT}),
            generateTestMoves(new long[] {8858370312L, 4613938368265322496L}, new int[] {18, 45}, new PieceType[] {PieceType.KNIGHT, PieceType.KNIGHT}),
            generateTestMoves(new long[] {576742296033165312L, 70866962496L}, new int[] {42, 21}, new PieceType[] {PieceType.KNIGHT, PieceType.KNIGHT}),
            generateTestMoves(new long[] {22136263676928L}, new int[] {27}, new PieceType[] {PieceType.KNIGHT}),
            generateTestMoves(new long[] {22136263676928L}, new int[] {27}, new PieceType[] {PieceType.KNIGHT}),
            generateTestMoves(new long[] {67584L, 2449995649404370944L}, new int[] {1, 51}, new PieceType[] {PieceType.KNIGHT, PieceType.KNIGHT}),
            generateTestMoves(new long[] {17600776115200L, 70866960464L}, new int[] {27, 21}, new PieceType[] {PieceType.KNIGHT, PieceType.KNIGHT})
        };

        for (int i = 0; i < results.length; i++) {
            boolean res = hasSameMoves(results[i], MoveGenerator.generateKnightMoves(boards[i]));
            if (!res) {
                System.out.println("FEHLER! Index: " + i);
            }
            assertTrue(res);
        }
    }

    @Test
    void generatePawnMoves() {
        Board[] boards = {
            new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"),
            new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1"),
            new Board("8/6P1/5P2/4P3/3P4/2P5/1P6/8 w - - 0 1"),
            new Board("8/8/8/pppppppp/PPPPPPPP/8/8/8 w - - 0 1"),
            new Board("8/8/8/pppppppp/PPPPPPPP/8/8/8 b - - 0 1"),
            new Board("8/pppppppp/1P1P1P1P/P1P1P1P1/8/8/8/8 b - - 0 1"),
            new Board("8/8/8/1p1p1p1p/p1p1p1p1/8/PPPPPPPP/8 w - - 0 1"),
            new Board("rnbq1rk1/pp1pppbp/5np1/6B1/2Pp4/2N1PN2/PP3PPP/R2QKB1R w KQ - 0 7")
        };

        Move[][] results = {
            generateTestMoves(new long[] {16842752L, 33685504L, 67371008L, 134742016L, 269484032L, 538968064L, 1077936128L, 2155872256L}, new int[] {8, 9, 10, 11, 12, 13, 14, 15}, new PieceType[]{PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN}),
            generateTestMoves(new long[] {1103806595072L, 2207613190144L, 4415226380288L, 8830452760576L, 17660905521152L, 35321811042304L, 70643622084608L, 141287244169216L}, new int[] {48, 49, 50, 51, 52, 53, 54, 55}, new PieceType[]{PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN}),
            generateTestMoves(new long[] {33685504L, 67108864L, 34359738368L, 17592186044416L, 9007199254740992L, 4611686018427387904L, 4611686018427387904L, 4611686018427387904L, 4611686018427387904L}, new int[] {9, 18, 27, 36, 45, 54, 54, 54, 54}, new PieceType[]{PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN}),
            generateTestMoves(new long[] {8589934592L, 21474836480L, 42949672960L, 85899345920L, 171798691840L, 343597383680L, 687194767360L, 274877906944L}, new int[] {24, 25, 26, 27, 28, 29, 30, 31}, new PieceType[]{PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN}),
            generateTestMoves(new long[] {33554432L, 83886080L, 167772160L, 335544320L, 671088640L, 1342177280L, 2684354560L, 1073741824L}, new int[] {32, 33, 34, 35, 36, 37, 38, 39}, new PieceType[]{PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN}),
            generateTestMoves(new long[] {3298534883328L, 0L, 15393162788864L, 0L, 61572651155456L, 0L, 246290604621824L, 0L}, new int[] {48, 49, 50, 51, 52, 53, 54, 55}, new PieceType[]{PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN}),
            generateTestMoves(new long[] {65536L, 33685504L, 262144L, 134742016L, 1048576L, 538968064L, 4194304L, 2155872256L}, new int[] {8, 9, 10, 11, 12, 13, 14, 15}, new PieceType[]{PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN}),
            generateTestMoves(new long[] {16842752L, 33685504L, 17179869184L, 402653184L, 1077936128L, 2155872256L}, new int[] {8, 9, 26, 20, 14, 15}, new PieceType[]{PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN}),
        };

        for (int i = 0; i < results.length; i++) {
            boolean res = hasSameMoves(results[i], MoveGenerator.generatePawnMoves(boards[i]));
            if (!res) {
                System.out.println("FEHLER! Index: " + i);
            }
            assertTrue(res);
        }
    }

    @Test
    void generateLegalMoves() {
        Board[] boards = {
            new Board(),
            new Board("rnbqkbnr/1pppppp1/p6p/8/2B1P3/5N2/PPPP1PPP/RNBQK2R b KQkq - 1 3"),
            new Board("rnbqkbnr/ppppp1pp/8/5p2/3P4/8/PPP1PPPP/RNBQKBNR w KQkq - 0 2"),
            new Board("4kr2/4b3/8/2p1R2p/4n1p1/3q2P1/7P/4K3 w - - 4"),
            new Board("rnb1kbnr/pp1ppppp/2p5/q7/8/3P1P2/PPP1P1PP/RNBQKBNR w KQkq - 0 1"),
            new Board("7k/7q/5pqp/6p1/NP3P1P/1KP3P1/2B5/8 b - - 0 1"),
            new Board("r2kq3/3r4/8/8/7b/p7/P3BN2/R3K2R w KQq - 0 1"),
        };
        int[] moveCounts = {20, 18, 28, 10, 7, 24, 10};

        MoveMasks masks = new MoveMasks();
        for(int i = 0; i < boards.length; i++){
            int moveCount = MoveGenerator.generateLegalMoves(boards[i], masks).length;
            assertEquals(moveCounts[i], moveCount);
        }
    }
}
