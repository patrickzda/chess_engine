package test;

import engine.representation.Board;
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
            testBoard = new Board(fenStrings[i]);
            assertEquals(testBoard.toFENString(), fenStrings[i]);
        }
    }
}