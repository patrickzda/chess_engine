package test;

import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.Board;
import engine.representation.Color;
import engine.representation.Move;
import engine.representation.PieceType;
import org.junit.jupiter.api.Test;

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
                MoveGenerator.generateTestMoves(new long[] {0L, 0L}, new int[] {0, 7}, new PieceType[]{PieceType.ROOK, PieceType.ROOK}),
                MoveGenerator.generateTestMoves(new long[] {578722409201797128L}, new int[] {35}, new PieceType[]{PieceType.ROOK}),
                MoveGenerator.generateTestMoves(new long[] {1157687956502220816L}, new int[] {44}, new PieceType[]{PieceType.ROOK}),
                MoveGenerator.generateTestMoves(new long[] {578737875244285952L, 18588887945232L}, new int[] {43, 36}, new PieceType[]{PieceType.ROOK, PieceType.ROOK}),
                MoveGenerator.generateTestMoves(new long[] {8895012014088L, 1157680259651338240L}, new int[] {35, 44}, new PieceType[]{PieceType.ROOK, PieceType.ROOK}),
                MoveGenerator.generateTestMoves(new long[] {1157684657697849344L, 72679952400L}, new int[] {44, 28}, new PieceType[]{PieceType.ROOK, PieceType.ROOK}),
                MoveGenerator.generateTestMoves(new long[] {289385980052373504L, 17633117188L}, new int[] {42, 26}, new PieceType[]{PieceType.ROOK, PieceType.ROOK}),
                MoveGenerator.generateTestMoves(new long[] {0L, 0L}, new int[] {0, 0}, new PieceType[]{PieceType.ROOK, PieceType.ROOK}),
                MoveGenerator.generateTestMoves(new long[] {0L, 0L}, new int[] {0, 0}, new PieceType[]{PieceType.ROOK, PieceType.ROOK}),
        };

        for (int i = 0; i < results.length; i++) {
            assertTrue(MoveGenerator.hasSameMoves(results[i], MoveGenerator.generateRookMoves(boards[i], m)));
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
                MoveGenerator.generateTestMoves(new long[] {0L, 0L}, new int[] {2, 5}, new PieceType[]{PieceType.BISHOP, PieceType.BISHOP}),
                MoveGenerator.generateTestMoves(new long[] {-9205038694072573375L}, new int[] {27}, new PieceType[]{PieceType.BISHOP}),
                MoveGenerator.generateTestMoves(new long[] {2455587783297826816L}, new int[] {43}, new PieceType[]{PieceType.BISHOP}),
                MoveGenerator.generateTestMoves(new long[] {318944272720449L, 18279391301640L}, new int[] {27, 30}, new PieceType[]{PieceType.BISHOP, PieceType.BISHOP}),
                MoveGenerator.generateTestMoves(new long[] {-8624392940535414784L, 4911175566587199744L}, new int[] {45, 44}, new PieceType[]{PieceType.BISHOP, PieceType.BISHOP}),
                MoveGenerator.generateTestMoves(new long[] {70540545491968L}, new int[] {28}, new PieceType[]{PieceType.BISHOP}),
                MoveGenerator.generateTestMoves(new long[] {4569847840768L}, new int[] {28}, new PieceType[]{PieceType.BISHOP}),
                MoveGenerator.generateTestMoves(new long[] {0L, 0L}, new int[] {0, 0}, new PieceType[]{PieceType.BISHOP, PieceType.BISHOP}),
                MoveGenerator.generateTestMoves(new long[] {0L, 0L}, new int[] {0, 0}, new PieceType[]{PieceType.BISHOP, PieceType.BISHOP}),
        };

        for (int i = 0; i < results.length; i++) {
            assertTrue(MoveGenerator.hasSameMoves(results[i], MoveGenerator.generateBishopMoves(boards[i], m)));
        }
    }

}
