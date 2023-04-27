package test;

import engine.representation.Board;
import engine.representation.Color;
import engine.representation.Move;
import engine.representation.PieceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    @Test
    void toFENString() {
        Board testBoard = new Board();
        assertEquals(testBoard.toFENString(), "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");

        final String[] fenStrings = new String[]{
            "2n5/4P3/1R2NP1R/2p5/3p1b1P/1P2k3/r2p4/1b5K",
            "8/3P2Pr/2p4p/2p4k/2P4b/P1P5/P1P3B1/R6K",
            "q3R3/p1k2P2/8/p3n1KP/8/1nr3p1/2Pp1p1p/8",
            "7n/1p4P1/1PP2PP1/7b/2P4K/p2N1q2/2kp4/5B2",
            "nQ1R4/N3npBp/2p2PPb/1q3pP1/1pPPrpr1/P1pb2N1/1pPkP2K/2R2B2",
            "1Nr1n1k1/Npn2p2/1KRP1pBp/2P2qrP/1R1Q3p/PP1bp1B1/PPP3pp/6b1",
            "8/8/1p3k2/1K6/8/R7/r7/3b3B",
            "8/p7/1p6/1k6/4b3/8/1K3P1P/8",
            "8/7k/8/8/K7/8/8/8",
            "8/4k3/p7/8/8/8/4K3/7N"
        };

        for(int i = 0; i < fenStrings.length; i++){
            testBoard = new Board(fenStrings[i], Color.WHITE);
            assertEquals(testBoard.toFENString(), fenStrings[i]);
        }
    }

    @Test
    void getTurn() {
        Board testBoard = new Board();
        assertEquals(testBoard.getTurn(), Color.WHITE);
        for(int i = 0; i < 10; i++){
            testBoard.doMove(new Move(1, 18, PieceType.KNIGHT));
            assertEquals(testBoard.getTurn(), Color.BLACK);
            testBoard.doMove(new Move(62, 45, PieceType.KNIGHT));
            assertEquals(testBoard.getTurn(), Color.WHITE);
            testBoard.doMove(new Move(18, 1, PieceType.KNIGHT));
            assertEquals(testBoard.getTurn(), Color.BLACK);
            testBoard.doMove(new Move(45, 62, PieceType.KNIGHT));
            assertEquals(testBoard.getTurn(), Color.WHITE);
        }
    }

    @Test
    void doMove() {
        Board testBoard = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR", Color.WHITE);
        testBoard.doMove(new Move(1, 18, PieceType.KNIGHT));
        assertEquals(testBoard.toFENString(), "rnbqkbnr/pppppppp/8/8/8/2N5/PPPPPPPP/R1BQKBNR");
        testBoard.doMove(new Move(62, 45, PieceType.KNIGHT));
        assertEquals(testBoard.toFENString(), "rnbqkb1r/pppppppp/5n2/8/8/2N5/PPPPPPPP/R1BQKBNR");

        testBoard = new Board("4kbr1/r6R/b1n2n2/8/3q2Q1/B7/R7/1N2KBN1", Color.WHITE);
        testBoard.doMove(new Move(30, 27, PieceType.QUEEN));
        testBoard.doMove(new Move(42, 27, PieceType.KNIGHT));
        testBoard.doMove(new Move(55, 48, PieceType.ROOK));
        testBoard.doMove(new Move(60, 59, PieceType.KING));
        testBoard.doMove(new Move(48, 50, PieceType.ROOK));
        testBoard.doMove(new Move(61, 16, PieceType.BISHOP));
        assertEquals(testBoard.toFENString(), "3k2r1/2R5/b4n2/8/3n4/b7/R7/1N2KBN1");

        testBoard = new Board("7r/8/8/8/8/8/8/3K1k2", Color.BLACK);
        testBoard.doMove(new Move(63, 7, PieceType.ROOK));
        assertEquals(testBoard.toFENString(), "8/8/8/8/8/8/8/3K1k1r");

        testBoard = new Board("8/8/8/8/8/8/8/3K1k1r", Color.BLACK);
        testBoard.doMove(new Move(7, 63, PieceType.ROOK));
        assertEquals(testBoard.toFENString(), "7r/8/8/8/8/8/8/3K1k2");

        testBoard = new Board("8/1P6/8/8/8/8/8/3K1k1r", Color.WHITE);
        Move nextMove = new Move(49, 57, PieceType.PAWN);
        nextMove.isPromotionToQueen = true;
        testBoard.doMove(nextMove);
        assertEquals(testBoard.toFENString(), "1Q6/8/8/8/8/8/8/3K1k1r");

        testBoard = new Board("2r5/1P6/8/8/8/8/8/3K1k1r", Color.WHITE);
        nextMove = new Move(49, 58, PieceType.PAWN);
        nextMove.isPromotionToKnight = true;
        testBoard.doMove(nextMove);
        assertEquals(testBoard.toFENString(), "2N5/8/8/8/8/8/8/3K1k1r");

        testBoard = new Board("2r5/1P6/8/8/8/8/8/3K1k1r", Color.WHITE);
        nextMove = new Move(49, 58, PieceType.PAWN);
        nextMove.isPromotionToBishop = true;
        testBoard.doMove(nextMove);
        assertEquals(testBoard.toFENString(), "2B5/8/8/8/8/8/8/3K1k1r");

        testBoard = new Board("8/4P3/8/8/8/8/8/3K1k1r", Color.WHITE);
        nextMove = new Move(52, 60, PieceType.PAWN);
        nextMove.isPromotionToRook = true;
        testBoard.doMove(nextMove);
        assertEquals(testBoard.toFENString(), "4R3/8/8/8/8/8/8/3K1k1r");
    }

    @Test
    void undoLastMove() {

    }

    @Test
    void testToString() {
        assert(true);
    }
}