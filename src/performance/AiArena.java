package performance;

import com.sun.jdi.InvocationException;
import engine.ai.Negamax;
import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.Board;
import engine.representation.Color;
import engine.representation.Move;
import test.AlphaBetaTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class AiArena {
    private final String[] FEN_DATA = new String[]{
        "rnbq1rk1/ppp1npbp/4p1p1/3p4/3PP3/2PB1N2/PP3PPP/RNBQ1RK1 w - - 0 7",
        "rnbqk1nr/ppppppbp/6p1/8/3PP3/2N5/PPP2PPP/R1BQKBNR b KQkq - 2 3",
        "r1bqkb1r/pppp1ppp/2n2n2/4p3/2P5/6P1/PP1PPPBP/RNBQK1NR w KQkq - 3 4",
        "r1bqkb1r/pppp1ppp/2n2n2/8/2BpP3/5N2/PPP2PPP/RNBQK2R w KQkq - 2 5",
        "rnbqkb1r/ppp1pppp/5n2/3p4/3P4/2N5/PPP1PPPP/R1BQKBNR w KQkq - 2 3",
        "rn2kb1r/1p2pppp/1pp5/3n1b2/8/2N1PN2/PP1P1PPP/R1B1KB1R w KQkq - 0 8",
        "rnbqkbnr/pp2pppp/2p5/3p4/3PP3/8/PPP2PPP/RNBQKBNR w KQkq - 0 3",
        "rnb1kbnr/ppq1pppp/2p5/8/4p3/2NP1N2/PPP2PPP/R1BQKB1R w KQkq - 0 5",
        "rnb1kb1r/ppp1pppp/5n2/q7/2B5/2NP4/PPP2PPP/R1BQK1NR b KQkq - 0 5",
        "rnbqkbnr/pp2pppp/2p5/8/2pP4/5N2/PP2PPPP/RNBQKB1R w KQkq - 0 4",
        "rnbqkb1r/pp2pppp/3p1n2/8/3NP3/8/PPP2PPP/RNBQKB1R w KQkq - 1 5",
        "rnbqkb1r/pp1ppppp/8/2pP2n1/7P/8/PPP1PPP1/RN1QKBNR w KQkq - 0 5",
        "rnbqkb1r/p1pp1ppp/1p2pn2/8/3P4/3BPN2/PPP2PPP/RNBQK2R b KQkq - 1 4",
        "rnbqkbnr/ppp1pppp/8/8/2pP4/5N2/PP2PPPP/RNBQKB1R b KQkq - 1 3",
        "rnbqk2r/pp2ppbp/3p1np1/8/3NP3/2N5/PPP1BPPP/R1BQ1RK1 b kq - 3 7",
        "rnbqkbnr/pp2pppp/8/3p4/3P4/8/PPP2PPP/RNBQKBNR w KQkq - 0 4",
        "rnbqkbnr/pp2pppp/8/2pp4/8/5NP1/PPPPPPBP/RNBQK2R b KQkq - 1 3",
        "rnbqkb1r/5ppp/p3pn2/1pP5/8/2NBPN2/PP3PPP/R1BQK2R b KQkq - 0 8",
        "r1b1kb1r/ppp2ppp/2n5/1B1np3/8/2P3N1/PP1P1PPP/R1B1K1NR b KQkq - 1 8",
        "rn2kb1r/pp3ppp/1qp1pn2/3p4/2PP2b1/1QN1PN2/PP3PPP/R1B1KB1R w KQkq - 2 7",
        "rnbqkb1r/ppp1pp1p/5np1/3p4/2PP4/2N5/PP2PPPP/R1BQKBNR w KQkq - 0 4",
        "rnbqkb1r/pp1ppppp/2p2n2/8/2P5/2N5/PP1PPPPP/R1BQKBNR w KQkq - 0 3",
        "r1bqkb1r/1p3ppp/p1np1n2/4p3/3NP3/2N1BP2/PPP3PP/R2QKB1R w KQkq - 0 8",
        "rn1qk2r/pb1p1ppp/1p2pn2/2p5/1BPP4/5NP1/PP2PPBP/RN1QK2R b KQkq - 0 7",
        "rnbqkb1r/ppp1pppp/3p4/6Bn/3P4/2N5/PPP1PPPP/R2QKBNR b KQkq - 3 4",
        "rnbqkb1r/ppp1pppp/5n2/3p4/2PP4/5N2/PP2PPPP/RNBQKB1R b KQkq - 0 3",
        "rnbqk2r/ppppppbp/5np1/8/2P5/2N3P1/PP1PPPBP/R1BQK1NR b KQkq - 3 4",
        "rnbqkb1r/ppp2ppp/4pn2/3P4/3P4/2N5/PP2PPPP/R1BQKBNR b KQkq - 0 4",
        "rn1qk2r/pbp2ppp/1p2p3/3pP3/1b1Pn3/3B4/PPPNNPPP/R1BQK2R b KQkq - 2 8",
        "rnbqkb1r/pppp2pp/4pn2/5p2/3P1B2/4PN2/PPP2PPP/RN1QKB1R b KQkq - 1 4",
        "rnbqkbnr/1p1ppppp/p7/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 0 3",
        "r1bqk2r/ppp2ppp/2np1n2/4p3/1bP1P3/2NP1N2/PP3PPP/R1BQKB1R w KQkq - 0 6",
        "rnbqkb1r/ppp2pp1/5B1p/3p4/3P4/2N5/PP2PPPP/R2QKBNR b KQkq - 0 6",
        "r1bq1rk1/1pp1bppp/p1np1n2/4p3/P1B1P3/3P1N1P/1PP2PP1/RNBQR1K1 b - - 1 8",
        "rnbqk1nr/pp2ppbp/3p2p1/2pP4/4P3/5N2/PPP2PPP/RNBQKB1R w KQkq - 0 5",
        "r2qkb1r/pppnpppp/3p1n2/5b2/3P1B2/2P1P3/PP3PPP/RN1QKBNR w KQkq - 1 5",
        "r1bqk2r/pp2npbp/2p3p1/2p1p3/4P3/3P1N2/PPP2PPP/RNBQR1K1 w kq - 1 8",
        "rnbq1rk1/ppp2pbp/3p1np1/4p3/2PPP3/2N2N2/PP2BPPP/R1BQK2R w KQ - 0 7",
        "r1bqkbnr/pppp2pp/2n5/4pp2/2P5/2N2N2/PP1PPPPP/R1BQKB1R w KQkq - 0 4",
        "rnbqkb1r/pppp1ppp/5n2/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3",
        "rnbqk1nr/ppp1ppbp/3p2p1/8/2PP4/2N2N2/PP2PPPP/R1BQKB1R b KQkq - 1 4",
        "rnbq1rk1/pp2ppbp/2p2np1/3p4/2P5/3P1NP1/PP2PPBP/RNBQ1RK1 w - - 0 7",
        "rnbqkb1r/pp2pp1p/6p1/3n4/3P4/1QN5/PP3PPP/R1B1KBNR b KQkq - 1 7",
        "rnbqkbnr/ppp2ppp/4p3/3p4/3P4/5N2/PPP1PPPP/RNBQKB1R w KQkq - 0 3",
        "rnbq1rk1/ppp1ppbp/3p1np1/8/2PPP3/2N2P2/PP4PP/R1BQKBNR w KQ - 1 6",
        "rnbqnrk1/pp2ppbp/6p1/2pp2P1/5P2/1PN1P3/PBPP3P/R2QKBNR w KQ - 0 8",
        "rnq1kb1r/ppp2ppp/4pn2/3p1b2/2PP4/1QN2N2/PP2PPPP/R1B1KB1R b KQkq - 1 6",
        "r1bqkb1r/ppp2ppp/2n2n2/3pp1N1/2B1P3/8/PPPP1PPP/RNBQK2R w KQkq - 0 5",
        "rnb1kb1r/pp2pp1p/1q1p1np1/2p5/3P1B2/2N1PN2/PPP2PPP/1R1QKB1R b Kkq - 1 6",
        "rnbq1rk1/pp3ppp/4pn2/2bp4/2P5/6P1/PPQNPPBP/R1B1K1NR w KQ - 0 8",
        "r1bqk2r/pppp1ppp/2n2n2/2b1p3/2B1P3/2NP4/PPP2PPP/R1BQK1NR w KQkq - 1 5",
        "rn1qk2r/pbp1bppp/1p3n2/3p4/3P4/P1N2NP1/1P2PPBP/R1BQK2R b KQkq - 2 8",
        "rnbqkbnr/pp2pppp/3p4/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 0 3",
        "rnbqk1nr/pp1pppbp/6p1/2pP4/2P5/5N2/PP2PPPP/RNBQKB1R b KQkq - 0 4",
        "r1bqkb1r/ppnppppp/2n5/1Bp1P3/8/2N2N2/PPPP1PPP/R1BQK2R w KQkq - 3 6",
        "rnbqkb1r/pppp3p/4pppn/8/3PP3/3B1N2/PPP2PPP/RNBQ1RK1 b kq - 1 5",
        "r1bqkb1r/1ppp1ppp/p1n2n2/4p3/B3P3/5N2/PPPP1PPP/RNBQ1RK1 b kq - 3 5",
        "r1bqk2r/pp2npbp/2n1p1p1/2p5/2B1P3/2N2N2/PPPP2PP/R1BQ1RK1 b kq - 1 8",
        "rnb1kbnr/ppp3pp/3p2q1/3P4/2N1p3/2N5/PPP2PPP/R1BQKB1R b KQkq - 0 7",
        "rn1k1b1r/ppp2ppp/4bn2/4p3/2P5/2N3P1/PP2PP1P/R1B1KBNR w KQ - 1 7",
        "rnbqkb1r/pppppppp/8/4P3/8/2n5/PPPP1PPP/R1BQKBNR w KQkq - 0 4",
        "rnbqk2r/pp1nbppp/4p3/2ppP3/3P1P2/P1N1B3/1PP1N1PP/R2QKB1R b KQkq - 1 8",
        "r2qkb1r/pp2pppp/2n2n2/3p4/3p2NP/6P1/PPP1PPB1/RNBQK2R b KQkq - 0 7",
        "rnbq1rk1/pp2ppbp/3p1np1/8/3P1B2/2PB1N2/PP3PPP/RN1Q1RK1 b - - 1 8",
        "rn1q1rk1/pp2ppbp/2pp1np1/5b2/2PP4/2N2NP1/PP2PPBP/R1BQ1RK1 w - - 2 8",
        "rnbqk2r/pp3ppp/3bpn2/3p4/3P4/2N1P1B1/PP3PPP/R2QKBNR b KQkq - 2 7",
        "rnbqkb1r/pp3ppp/3p4/2pP1p2/2P5/2N5/PP2PPPP/R2QKBNR b KQkq - 1 6",
        "rnbq1rk1/ppp1ppbp/3p2p1/8/3PnB2/4PN1P/PPP1BPP1/RN1Q1RK1 b - - 4 7",
        "rnbqkb1r/ppp2ppp/3p4/8/3Pn3/5N2/PPP2PPP/RNBQKB1R b KQkq - 0 5",
        "r1bqkbnr/pp3ppp/2npp3/1N6/4P3/8/PPP2PPP/RNBQKB1R w KQkq - 0 6",
        "rn1qkbnr/ppp2ppp/4b3/8/8/8/PPPP1PPP/RNBQKBNR w KQkq - 0 4",
        "r1bqk2r/pp2ppbp/2np1np1/2p5/3PP3/2PBBN1P/PP3PP1/RN1QK2R b KQkq - 2 7",
        "r1bqk1nr/1p1nbppp/p2p4/2pPp3/P1N1P3/8/1PP2PPP/R1BQKBNR w KQkq - 4 7",
        "rnbqk1nr/ppp1ppbp/3p2p1/8/8/5NP1/PPPPPPBP/RNBQ1RK1 b kq - 3 4",
        "rnbqkb1r/ppp1pp1p/3p1np1/8/3P4/5NP1/PPP1PPBP/RNBQK2R b KQkq - 1 4",
        "r1bqkbnr/ppp2ppp/2np4/4P3/4PP2/5N2/PPP3PP/RNBQKB1R b KQkq - 1 5",
        "rnbqkb1r/ppp2ppp/4pn2/3p4/2PP4/2N2N2/PP2PPPP/R1BQKB1R b KQkq - 2 4",
        "rnb1k2r/ppp1ppbp/6p1/3q4/8/5NP1/PP1PPP1P/R1BQKB1R w KQkq - 1 7",
        "r1bqkbnr/pppp3p/2n3p1/5p2/2PPp2N/2N3P1/PP2PP1P/R1BQKB1R b KQkq - 1 6",
        "r1bqk2r/1p1nppbp/p2p1np1/2p5/4P3/2P2N2/PPBP1PPP/RNBQR1K1 b kq - 2 8",
        "rn1qkbnr/pp2pppp/2p5/8/4N1b1/5N1P/PPPP1PP1/R1BQKB1R b KQkq - 0 5",
        "rnbqk2r/ppp2ppp/3bp3/3p4/3P1B2/2P2N2/PP1QPPPP/R3KB1R w KQkq - 1 7",
        "rnb1kb1r/ppp2ppp/5n2/q7/4p3/P1N2N2/1PPPBPPP/R1BQK2R w KQkq - 0 7",
        "rnbqkb1r/pppppp1p/5np1/8/2PP4/8/PP2PPPP/RNBQKBNR w KQkq - 0 3",
        "rnb1kb1r/pp3ppp/2pp4/q3p3/2PPn3/2N2NP1/PPQ1PPBP/R1B1K2R b KQkq - 3 7",
        "rnbqkbnr/ppp2ppp/4p3/3p4/2PP4/8/PP2PPPP/RNBQKBNR w KQkq - 0 3",
        "rnbqkb1r/ppp2p1p/5pp1/3p4/2P5/1P2P3/P2P1PPP/RN1QKBNR b KQkq - 0 5",
        "r1bqk1nr/ppp2pbp/2np2p1/8/3pP3/2PB1N1P/PP3PP1/RNBQK2R w KQkq - 0 7",
        "rnbqkb1r/ppp2ppp/5n2/4p3/4P3/2N5/PPP2PPP/R1BQKBNR w KQkq - 0 5",
        "r1bqkbnr/pppp1ppp/2n5/1B2p3/4P3/5N2/PPPP1PPP/RNBQK2R b KQkq - 3 3",
        "r1bqkb1r/ppp2pp1/5n1p/n2P4/2BNp3/3P4/PPP2PPP/RNBQK2R b KQkq - 1 8",
        "r1bqkb1r/pp3pp1/2n1pB1p/2pp4/3P1P2/2P1P3/PP1N2PP/R2QKBNR b KQkq - 0 7",
        "rnbq1rk1/ppppppbp/5np1/8/6P1/1P2P3/PBPP1P1P/RN1QKBNR w KQ - 1 5",
        "rnbqkbnr/1p3ppp/p3p3/2p5/P1pP1B2/4PN2/1P3PPP/RN1QKB1R b KQkq - 0 6",
        "rnbqkbnr/ppp2ppp/3p4/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 0 3",
        "r1bqkbnr/pp1ppppp/2n5/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3",
        "r2qkbnr/pppn1ppp/2b1p3/8/3PN3/3B1N2/PPP2PPP/R1BQK2R w KQkq - 5 7",
        "rnbqk2r/pppp1ppp/4pn2/8/1bPP4/2N5/PP2PPPP/R1BQKBNR w KQkq - 2 4",
        "rnbqkb1r/pppp1ppp/4pn2/8/2PP4/8/PP2PPPP/RNBQKBNR w KQkq - 0 3",
        "rnbqkb1r/ppp1pp2/3p3p/6pn/3P3B/5N2/PPP1PPPP/RN1QKB1R w KQkq - 0 6",
        "rnbqkbnr/ppp2ppp/4p3/3p4/4P3/3P4/PPP2PPP/RNBQKBNR w KQkq - 0 3",
        "rnbqk1nr/ppp1ppbp/3p2p1/8/3PP3/3B1N2/PPP2PPP/RNBQK2R b KQkq - 1 4",
        "r1bqk2r/pppp1ppp/2n2n2/2b1p3/2B1P3/2P2N2/PP1P1PPP/RNBQK2R w KQkq - 1 5",
        "rnbqkb1r/ppp2ppp/8/3p4/3Pn3/2N2N2/PP2PPPP/R1BQKB1R w KQkq - 2 6",
        "rnbqkb1r/pp2pp1p/3p1np1/8/3PP3/2N2N2/PP3PPP/R1BQKB1R b KQkq - 1 6",
        "rnbqkbnr/pp3ppp/4p3/2pP4/3P4/2N5/PP2PPPP/R1BQKBNR b KQkq - 0 4",
        "r1bqkbnr/pppp1ppp/2n5/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3",
        "rnbqk1nr/p4pbp/2p3p1/1p2p3/4P3/2NBB3/PPPQ1PPP/2KR2NR b kq - 1 8",
        "rnbqkb1r/pppppp1p/5np1/8/8/1P6/PBPPPPPP/RN1QKBNR w KQkq - 0 3",
        "rnbqkb1r/p1pppppp/1p3n2/8/3P4/5N2/PPP1PPPP/RNBQKB1R w KQkq - 0 3",
        "rnbqkb1r/pppppp1p/5np1/8/2P5/2N5/PP1PPPPP/R1BQKBNR w KQkq - 0 3",
        "rnbqkb1r/pp2pppp/2p5/3n4/2B5/2N5/PPPP1PPP/R1BQK1NR w KQkq - 0 5",
        "rnbqkbnr/1p2pppp/p1p5/3pP3/3P4/8/PPP2PPP/RNBQKBNR w KQkq - 0 4",
        "r1bqk2r/pppp1ppp/5n2/2b1p3/2BnP3/2N2N1P/PPPP1PP1/R1BQK2R b KQkq - 0 6",
        "rnbqk1nr/pp3pbp/2p3p1/3p4/2PPp3/6P1/PP2PPBP/RNBQNRK1 b kq - 1 7",
        "rnb1kb1r/pp3ppp/4pn2/2pq4/3P4/2P2N2/PP3PPP/RNBQKB1R w KQkq - 0 6",
        "r1b1kb1r/pp2pppp/1qn5/3p4/2pPnB2/1QP1PN2/PP3PPP/RN2KB1R w KQkq - 0 8",
        "rnbqkb1r/pppp1ppp/4pn2/8/3P1B2/5N2/PPP1PPPP/RN1QKB1R b KQkq - 3 3",
        "r1bqk2r/pp2npbp/2n1p1p1/2ppP3/3P4/2PB1N2/PP3PPP/RNBQ1RK1 w kq - 1 8",
        "rnbqkb1r/ppp2ppp/3p1n2/8/3NP3/8/PPP2PPP/RNBQKB1R w KQkq - 1 5",
        "rnbqkb1r/ppp1pppp/3p1n2/8/3PP3/2N5/PPP2PPP/R1BQKBNR b KQkq - 2 3",
        "r3kb1r/ppqnpppp/2p2n2/5b2/P1NP4/2N5/1P2PPPP/R1BQKB1R w KQkq - 1 8",
        "rnbqkb1r/ppp1pppp/3p1n2/8/4P3/2N5/PPPP1PPP/R1BQKBNR w KQkq - 2 3",
        "rnb1kb1r/pp1pp1pp/2p2n2/q5B1/3Pp3/2N2P2/PPP3PP/R2QKBNR w KQkq - 1 6",
        "rn1qkb1r/pp2nppp/2p1p3/3pPb2/3P4/5N2/PPP1BPPP/RNBQK2R w KQkq - 2 6",
        "r1bqkb1r/ppp1pppp/5n2/n2P4/2p5/2N2N2/PP2PPPP/R1BQKB1R w KQkq - 1 6",
        "rnbqk1nr/pp1p1pbp/6p1/2p1p3/2P5/5NP1/PP1PPPBP/RNBQ1RK1 b kq - 0 5",
        "r1bqk1nr/p3p1bp/2pp2p1/2p2p2/4P3/2NP2N1/PPP2PPP/R1BQ1RK1 b kq - 1 8",
        "rnbqkb1r/pp1ppp1p/6p1/2pP2B1/4n2P/8/PPP1PPP1/RN1QKBNR w KQkq - 0 5",
        "r2qkbnr/pp1b1ppp/2B1p3/2PpP3/8/4B3/PPP2PPP/RN1QK1NR b KQkq - 0 7",
        "rn1qkbnr/pp1b1ppp/8/3p4/3Q4/2N5/PP2PPPP/R1B1KBNR w KQkq - 0 7",
        "rnbqk1nr/pp2bppp/4p3/3pP3/3p4/2P5/PP1N1PPP/R1BQKBNR w KQkq - 0 6",
        "rnbqk2r/pp1p1ppp/4pn2/2p5/2PP4/5N2/PPQbPPPP/RN2KB1R w KQkq - 0 6",
        "r1bqk2r/2p1bppp/p1np1n2/1p2p3/4P3/1BP2N2/PP1P1PPP/RNBQR1K1 b kq - 0 8",
        "rnbqk1nr/pppp1ppp/8/4p3/1bP5/1QN5/PP1PPPPP/R1B1KBNR b KQkq - 3 3",
        "rnbqkb1r/pp2pppp/2p2n2/3p4/3P1B2/5N2/PPP1PPPP/RN1QKB1R w KQkq - 2 4",
        "r1bqkbnr/pp2pppp/2n5/3p4/3P4/3B4/PPP2PPP/RNBQK1NR w KQkq - 2 5",
        "r1bqkbnr/1ppp1ppp/p1n5/1B2p3/4P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 0 4",
        "rnbq1rk1/1p2ppbp/p2p1np1/2pP4/P3P3/2N2N2/1PP1BPPP/R1BQK2R w KQ - 3 8",
        "rnbqk1nr/ppp2ppp/4p3/3p4/1b1PP3/P1N5/1PP2PPP/R1BQKBNR b KQkq - 0 4",
        "rnbqkbnr/pp2pppp/8/3pN3/8/8/PPPP1PPP/RNBQKB1R b KQkq - 1 4",
        "rnbqk2r/pp2ppbp/3p1np1/8/3NP3/2N1B3/PPP2PPP/R2QKB1R w KQkq - 2 7",
        "r1bqk2r/1pp2ppp/p1np1n2/2b1p3/4P3/2N2NPP/PPPP1PB1/R1BQK2R w KQkq - 0 7",
        "rnbqk2r/ppp2ppp/8/3P4/1b1QnP2/2N5/PPP3PP/R1B1KBNR b KQkq - 1 7",
        "rnbqkb1r/pp1p1ppp/5n2/4p3/2PN4/8/PP2PPPP/RNBQKB1R w KQkq - 0 5",
        "rnbq1rk1/1pp1ppbp/3p1np1/p7/P2P4/2N2NP1/1PP1PPBP/R1BQ1RK1 b - - 1 7",
        "rnbqkbnr/ppp1pppp/8/8/2pP4/8/PP2PPPP/RNBQKBNR w KQkq - 0 3",
        "rnb1kbnr/pp3ppp/4p3/2pq4/3P4/5N2/PPPN1PPP/R1BQKB1R b KQkq - 1 5",
        "rnbqkbnr/ppp2ppp/4p3/3p4/2P5/5NP1/PP1PPP1P/RNBQKB1R b KQkq - 0 3",
        "rnbqk2r/ppppppbp/5np1/8/2P5/2N3P1/PP1PPP1P/R1BQKBNR w KQkq - 1 4",
        "r1bqk1nr/pp1p1pbp/2n1p1p1/2p5/2P5/2N2NP1/PP1PPPBP/R1BQ1RK1 b kq - 1 6",
        "r1bqkb1r/pp2pp1p/2np1np1/2p5/2P5/2N2NP1/PP1PPPBP/R1BQK2R w KQkq - 0 6",
        "rn1qkb1r/4pppp/b2p1n2/2pP4/4P3/2N5/PP3PPP/R1BQKBNR b KQkq - 0 8",
        "rn1qkb1r/pbp2ppp/1p2pn2/3p4/2PP4/P1N2N2/1P2PPPP/R1BQKB1R w KQkq - 0 6",
        "rnbqk1nr/ppppppbp/6p1/8/3P1B2/5N2/PPP1PPPP/RN1QKB1R b KQkq - 3 3",
        "rnbqkb1r/ppp1pp1p/5np1/3p4/3P4/2N2N2/PPP1PPPP/R1BQKB1R w KQkq - 0 4",
        "rnbqkbnr/pp2pppp/2p5/3P4/3P4/8/PPP2PPP/RNBQKBNR b KQkq - 0 3",
        "r1bqkbnr/2p2ppp/p2p4/1p2P3/3p4/1B6/PPP2PPP/RNBQK2R b KQkq - 0 8",
        "r1bq1rk1/ppp1nppp/2n5/3p4/Qb1P1B2/2N1PN2/PP3PPP/R3KB1R b KQ - 0 8",
        "rnbqkb1r/pp1npppp/2p5/3pP3/3P1P2/8/PPP3PP/RNBQKBNR b KQkq - 0 5",
        "rnbqkb1r/pppppp1p/5np1/8/2P5/1P3N2/P2PPPPP/RNBQKB1R b KQkq - 0 3",
        "r1b1kbnr/pp2pppp/1qn5/2pp4/3P1B2/2P1P3/PP3PPP/RN1QKBNR w KQkq - 1 5",
        "r1bqk2r/2ppbppp/p1n2n2/1p2p3/3PP3/1B3N2/PPP2PPP/RNBQ1RK1 b kq - 0 7",
        "r1bq1rk1/pp2ppbp/n2p1np1/2pP4/2P1P3/2NB1N1P/PP3PP1/R1BQK2R b KQ - 2 8",
        "rn1qkbnr/ppp2ppp/4p3/3p1b2/2PP4/2N5/PP2PPPP/R1BQKBNR w KQkq - 0 4",
        "rnbqk2r/pp1nbppp/4p3/2ppP3/3P1P2/P1N5/1PP1N1PP/R1BQKB1R w KQkq - 0 8",
        "rnbqkbnr/pp2pppp/2p5/8/3Pp3/2N5/PPP2PPP/R1BQKBNR w KQkq - 0 4",
        "rnbq1rk1/p1p1bppp/1p2pn2/3p4/2P5/1P3NP1/PB1PPPBP/RN1Q1RK1 b - - 1 7",
        "r3kbnr/pp1b1ppp/1qn1p3/2ppP3/5P2/2P2N2/PPBP2PP/RNBQK2R b KQkq - 4 7",
        "rnbqkbnr/pp2pppp/3p4/8/3pP3/5N2/PPP2PPP/RNBQKB1R w KQkq - 0 4",
        "r3kb1r/pp1n1ppp/1qp1pn2/3p1b2/2PP1B2/1Q2PN2/PP3PPP/RN2KB1R w KQkq - 0 8",
        "r1bqkb1r/pp1n1ppp/2p2n2/4p1B1/3PP3/2N5/PPP3PP/R2QKBNR w KQkq - 0 7",
        "rnbqkbnr/pp2pppp/3p4/2p5/3PP3/5N2/PPP2PPP/RNBQKB1R b KQkq - 0 3",
        "r3kb1r/pp2pppp/2n2n2/2P2b2/8/2P2N2/PP3PPP/RNBK1B1R w kq - 1 8",
        "r1bqk1nr/pp1pppbp/2n3p1/8/3NP3/2N5/PPP2PPP/R1BQKB1R w KQkq - 1 6",
        "rnbqkb1r/pp2pp1p/5np1/2pp4/3P1B2/2P1PN2/PP3PPP/RN1QKB1R b KQkq - 1 5",
        "rnbqkb1r/pppppp1p/5np1/8/3P1B2/5N2/PPP1PPPP/RN1QKB1R b KQkq - 1 3",
        "rnbqk1nr/ppppppbp/6p1/8/3P4/5N2/PPP1PPPP/RNBQKB1R w KQkq - 2 3",
        "rnbqk1nr/ppp2ppp/3p4/2bPp3/2P1P3/8/PP1N1PPP/R1BQKBNR w KQkq - 0 6",
        "r1bqkb1r/pp3ppp/2n2n2/1Bpp4/3P4/2N1PN2/PP3PPP/R1BQK2R b KQkq - 4 7",
        "rnbqkbnr/pp2pp1p/2p3p1/3p4/4P3/2NP1N2/PPP2PPP/R1BQKB1R b KQkq - 0 4",
        "rnbqkb1r/pp3ppp/4pn2/2pp4/3P1B2/4PN2/PPPN1PPP/R2QKB1R b KQkq - 1 5",
        "rnbqkbnr/ppp2ppp/8/3p4/3P4/8/PPP2PPP/RNBQKBNR w KQkq - 0 4",
        "r1bqkbnr/pp1npppp/2p5/8/3PN3/3B4/PPP2PPP/R1BQK1NR b KQkq - 2 5",
        "rnb1k2r/pp1ppp1p/5np1/2q5/4P3/2P2P2/P1PB2PP/R2QKBNR w KQkq - 0 8",
        "rnbqkb1r/pppn1ppp/4p3/3pP3/3P4/8/PPPN1PPP/R1BQKBNR w KQkq - 1 5",
        "r1bqkb1r/pp3ppp/2n2n2/2p1p3/2PpP3/N2P1NP1/PP3PBP/R1BQ1RK1 b kq - 1 8",
        "rnbqkb1r/pp3ppp/5n2/2p5/3pN3/3P2P1/PPP2PBP/R1BQK1NR b KQkq - 3 7",
        "rnb1kb1r/1pqp1ppp/p3pn2/8/2PNP3/2N5/PP3PPP/R1BQKB1R w KQkq - 3 7",
        "rnbqkbnr/pp2pppp/2p5/8/3PN3/8/PPP2PPP/R1BQKBNR b KQkq - 0 4",
        "rnbqkb1r/ppp3pp/4pn2/3p1pB1/3P4/2N3P1/PPP1PPBP/R2QK1NR b KQkq - 1 5",
        "rnbqkb1r/ppp2ppp/4pn2/3P4/3P4/5N2/PP2PPPP/RNBQKB1R b KQkq - 0 4",
        "rnbqkbnr/pp2pppp/2p5/3P4/3P4/8/PP2PPPP/RNBQKBNR b KQkq - 0 3",
        "r1bqkb1r/ppp1pppp/1nn5/4P3/2PP4/8/PP4PP/RNBQKBNR w KQkq - 1 7",
        "rnbq1rk1/pp2bppp/4pn2/3p4/2PP4/2N1BN2/PP2BPPP/R2QK2R b KQ - 5 8",
        "rn1qkbnr/pp2pppp/2p5/3p4/4P1b1/2N2N2/PPPP1PPP/R1BQKB1R w KQkq - 2 4",
        "rnbqk2r/ppp1bppp/3p1n2/8/3NP3/2N5/PPP2PPP/R1BQKB1R w KQkq - 3 6",
        "rnbq1rk1/ppp1ppbp/3p1np1/6B1/3PP3/2N2N2/PPP2PPP/R2QKB1R w KQ - 4 6",
        "r1bq1rk1/ppppnppp/2n5/4p3/1b2P3/2N2NP1/PPPP1PBP/R1BQ1RK1 b - - 4 6",
        "rn1qkbnr/1p2pppp/p1p5/3p3b/3PP3/5N1P/PPPN1PP1/R1BQKB1R w KQkq - 1 6",
        "rnbqkb1r/ppp1pppp/3p1n2/8/3PP3/8/PPP2PPP/RNBQKBNR w KQkq - 1 3",
        "rn1qkb1r/ppp2pp1/4pn1p/3p1b2/3P4/5NP1/PPPNPPBP/R1BQK2R w KQkq - 0 6",
        "r1bqkb1r/pp2pppp/2np1n2/8/3NP3/2N5/PPP2PPP/R1BQKB1R w KQkq - 3 6",
        "r1bqk1nr/pp1p1ppp/1bp5/1B2p3/3PP3/2P5/PP3PPP/RNBQ1RK1 w kq - 0 8",
        "r1bqkbnr/pppp2pp/2n5/1B2pp2/4P3/2N2N2/PPPP1PPP/R1BQK2R b KQkq - 1 4",
        "r1bqkbnr/ppp3pp/2n2p2/3pp3/8/3P1NP1/PPP1PPBP/RNBQK2R w KQkq - 0 5",
        "rnbqkb1r/1p2pppp/p2p1n2/6B1/3NP3/2N5/PPP2PPP/R2QKB1R b KQkq - 1 6",
        "rnbqk2r/pp3ppp/3bpn2/2pp4/5P2/2N1PN2/PPPPB1PP/R1BQ1RK1 b kq - 1 6",
        "rnbqkbnr/pp2pppp/8/2pp4/3P1B2/5N2/PPP1PPPP/RN1QKB1R b KQkq - 1 3",
        "rnbqkb1r/ppp1pppp/1n1p4/4P3/2PP4/8/PP3PPP/RNBQKBNR w KQkq - 0 5",
        "rnb1kb1r/pp3pp1/2p1pq1p/3p4/2PP4/2N2N2/PP2PPPP/R2QKB1R w KQkq - 0 7",
        "rnb1kb1r/pp3ppp/2pp1n2/q3p3/3PPP2/2NB1N2/PPP3PP/R1BQK2R b KQkq - 1 6",
        "rnbq1rk1/pp2ppbp/2pp1np1/8/3PP3/2N2N1P/PPP1BPP1/R1BQK2R w KQ - 0 7",
        "r1bqkb1r/2p2ppp/p1n5/1p1pP3/4n3/1B3N2/PPP2PPP/RNBQ1RK1 b kq - 0 8",
        "rn1qkbnr/ppp1pppp/6b1/6N1/4p1P1/8/PPPP1P1P/RNBQKB1R w KQkq - 1 5",
        "r1bqkbnr/pp1ppppp/2n5/1Bp5/4P3/5N2/PPPP1PPP/RNBQK2R b KQkq - 3 3",
        "rn1qkbnr/pp2pppp/2p5/3pPb2/3P4/8/PPP2PPP/RNBQKBNR w KQkq - 1 4",
        "rnbqkb1r/pppppppp/1n6/4P3/2P5/5N2/PP1P1PPP/RNBQKB1R b KQkq - 2 4",
        "rnbqk1nr/ppppppbp/6p1/8/3PP3/5N2/PPP2PPP/RNBQKB1R b KQkq - 0 3",
        "r1bqkbnr/pp1p1ppp/2n1p3/8/3pPP2/2P2N2/PP4PP/RNBQKB1R b KQkq - 0 5",
        "r1b1kb1r/pp1n1ppp/1qn1p3/2ppP3/3P4/2PB4/PP1NNPPP/R1BQK2R w KQkq - 3 8",
        "rnbqkbnr/pp3ppp/8/2pp4/3P4/5N2/PPPN1PPP/R1BQKB1R b KQkq - 1 5",
        "rnbq1rk1/ppp1ppbp/3p1np1/8/3P1B2/4PN2/PPP1BPPP/RN1Q1RK1 b - - 4 6",
        "r1bqkb1r/pppp1ppp/2n2n2/4p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4",
        "rn1qkb1r/pp2pppp/2p2n2/3p1b2/2PP4/2N1PN2/PP3PPP/R1BQKB1R b KQkq - 2 5",
        "r1bq1rk1/ppp1n1pp/2np4/4pp2/1b2P3/2NP1NPP/PPP2PB1/R1BQ1RK1 b - - 0 8",
        "r1bqk2r/ppppb1pp/2n2B2/4p3/4P3/5N2/PPP1BPPP/RN1QK2R b KQkq - 0 7",
        "rnbq1rk1/pp3ppp/3bpn2/2ppN3/3P1B2/4P3/PPPN1PPP/R2QKB1R w KQ - 0 7",
        "rnbqkbnr/pppp1p1p/8/6p1/4Pp1P/2N5/PPPP2P1/R1BQKBNR b KQkq - 0 4",
        "r1bqk2r/pppp1ppp/5n2/4p1B1/2BnP3/8/PPP2PPP/RN1Q1RK1 b kq - 1 7",
        "rn1qkb1r/pp3ppp/2p1pn2/3p4/2PP2b1/2N1PN2/PP3PPP/R1BQKB1R w KQkq - 0 6",
        "rnbqk2r/1pp1ppbp/p2p1np1/8/P2PPP2/2NB4/1PP3PP/R1BQK1NR b KQkq - 2 6",
        "r1bqkb1r/p1p2ppp/2pp1n2/8/4P3/2N5/PPP2PPP/R1BQKB1R w KQkq - 0 7",
        "r1bqkbnr/pp1p1ppp/2n1p3/2p5/4PP2/3P4/PPP3PP/RNBQKBNR w KQkq - 0 4",
        "rnbqk1nr/ppp2ppp/4p3/3pP3/1b1P4/2N5/PPP2PPP/R1BQKBNR b KQkq - 0 4",
        "r2qkb1r/pppnpppp/5n2/3p4/3P1Pb1/3BPN2/PPP3PP/RNBQK2R b KQkq - 2 5",
        "r1b1kbnr/ppp1pppp/2n5/q7/3P4/2N2N2/PPP2PPP/R1BQKB1R b KQkq - 0 5",
        "rnbqk2r/ppp1nppp/3bp3/3p4/3P1B2/4PN2/PPP2PPP/RN1QKB1R w KQkq - 1 5",
        "r1bqk2r/pp1n1ppp/2pb1n2/3p4/3P4/2NBPN2/PP3PPP/R1BQK2R w KQkq - 2 8",
        "rn1qkb1r/p1pp1ppp/bp2pn2/8/2PP4/5NP1/PP2PP1P/RNBQKB1R w KQkq - 1 5",
        "rnbqk2r/ppp1ppbp/1n1p2p1/4P3/2PP1P2/2N5/PP4PP/R1BQKBNR w KQkq - 2 7",
        "r2qkb1r/pppn1ppp/4pn2/3p1b2/3P4/1P2PN2/PBP2PPP/RN1QKB1R w KQkq - 1 6",
        "rnbqkbnr/pp2pppp/2p5/3p4/3P4/5N2/PPP1PPPP/RNBQKB1R w KQkq - 0 3",
        "r1bqkb1r/pppn1pp1/4pn1p/3p4/3P3B/2N2P2/PPP1P1PP/R2QKBNR w KQkq - 0 6",
        "rnbqk1nr/ppp2pp1/3p3p/2b1p3/2B1P3/2NP1N2/PPP2PPP/R1BQK2R b KQkq - 0 5",
        "r1bqk1nr/pp2bp1p/2np4/2p1p3/4P1p1/2PP1N2/PP1NBPPP/R1BQ1RK1 w kq - 0 8",
        "rnbqkbnr/pp3ppp/4p3/3p4/2PP4/8/PP3PPP/RNBQKBNR w KQkq - 0 5",
        "r1bq1rk1/pp3ppp/2nbpn2/2pp4/3P4/2PBPNB1/PP1N1PPP/R2QK2R b KQ - 4 8",
        "rnbqkb1r/ppp1pppp/5n2/3p4/3P4/5N2/PPP1PPPP/RNBQKB1R w KQkq - 2 3",
        "rnbqkb1r/pppppp1p/5np1/8/8/1P3NP1/P1PPPP1P/RNBQKB1R b KQkq - 0 3",
        "r1bqk2r/pppp1ppp/2n2n2/2b1p3/2BPP3/5N2/PPP2PPP/RNBQ1RK1 b kq - 0 5",
        "rnbqkb1r/pp2pp1p/2p2np1/3p4/2PP4/2N2N2/PP2PPPP/R1BQKB1R w KQkq - 0 5",
        "rnbqkb1r/ppp1pp1p/5np1/3p2B1/3P4/2N5/PPP1PPPP/R2QKBNR w KQkq - 0 4",
        "rnbqkbnr/3p1ppp/p3p3/1p6/3NP3/2N5/PPP2PPP/R1BQKB1R w KQkq - 0 6",
        "rnbqkbnr/1p3ppp/p7/3p4/2pP4/2P2N2/PPB2PPP/RNBQK2R b KQkq - 1 7",
        "rnbqkb1r/pp2pppp/2p2n2/3p4/3P4/1P3N2/P1P1PPPP/RNBQKB1R w KQkq - 0 4",
        "rnbqkb1r/ppp1pp1p/5np1/3p2B1/3P4/2N2N2/PPP1PPPP/R2QKB1R b KQkq - 1 4",
        "2kr1bnr/pppq2pp/2n1pp2/3pPb2/1P1P1P2/2P2N2/P5PP/RNBQKB1R w KQ - 0 8",
        "r2qkb1r/pp1npppp/2p2n2/3p1b2/3P1B2/2P2NP1/PP2PPBP/RN1QK2R b KQkq - 2 6",
        "rnbq1rk1/ppp1bppp/3p1n2/8/3NP3/2N1B3/PPP2PPP/R2QKB1R w KQ - 5 7",
        "r1bq1rk1/pp1pppbp/2n2np1/1Bp5/4P3/2P2N1P/PP1P1PP1/RNBQR1K1 b - - 0 7",
        "rnbqk2r/pp1pppbp/5np1/8/3BP3/2N2N2/PPP2PPP/R2QKB1R b KQkq - 2 6",
        "r1bnkbnr/ppp2ppp/8/4p3/2P5/2N2N2/PP2PPPP/R1B1KB1R b KQkq - 1 6",
        "rn1qk2r/ppp1bppp/8/3p1b2/3Pn3/3B1N2/PPP2PPP/RNBQR1K1 b kq - 3 8",
        "r2qkbnr/pp3ppp/2n1p3/1B1p4/Q2P2b1/2P2N2/PP3PPP/RNB1K2R b KQkq - 1 7",
        "rnbqkb1r/pppppppp/8/3nP3/8/8/PPPP1PPP/RNBQKBNR w KQkq - 1 3",
        "rn1qk2r/pp1bppbp/3p1np1/1BpPP3/5P2/2N5/PPP3PP/R1BQK1NR b KQkq - 0 7",
        "rn1qk1nr/pp3ppp/3Bp3/3p4/3P4/P4N2/1P2PPPP/1R1QKB1R b Kkq - 0 8",
        "r1bqkb1r/p2p1ppp/2n1pn2/8/2Q1P3/5N2/PP3PPP/RNB1KB1R w KQkq - 1 8",
        "rn1qkbnr/pp3ppp/2p1p3/5b2/2BP4/2N2N2/PPP2PPP/R1BQK2R w KQkq - 0 7",
        "rn1qkbnr/pbpppppp/1p6/8/3P4/5N2/PPP1PPPP/RNBQKB1R w KQkq - 2 3",
        "rn1qk2r/pbp2ppp/1p1ppb2/8/2PPP3/P1N5/1P3PPP/R2QKBNR w KQkq - 0 8",
        "rnbqkb1r/ppp2ppp/4pn2/3p4/2PP4/5N2/PP2PPPP/RNBQKB1R w KQkq - 0 4",
        "rn1qkbnr/pp2ppp1/2p3bp/8/3P3P/5NN1/PPP2PP1/R1BQKB1R b KQkq - 1 7",
        "rnbq1rk1/pp2p1bp/2p2Ppn/3p4/3P4/2N3P1/PPP1NPBP/R1BQK2R b KQ - 0 8",
        "rnbqkb1r/1p2ppp1/p2p1n2/7p/3NP3/2N5/PPP2PPP/R1BQKBR1 w Qkq - 0 7",
        "r2qkb1r/pp1n1pp1/2p1pn1p/3p1b2/2PP4/2N2NP1/PP2PPBP/R1BQ1RK1 w kq - 2 8",
        "rnbq1rk1/pp2bppp/5n2/2Pp4/8/2N1PN2/PP2BPPP/R1BQK2R b KQ - 0 8",
        "rnbqkb1r/ppp1pppp/5n2/3p4/5P2/5NP1/PPPPP2P/RNBQKB1R b KQkq - 0 3",
        "r2qkb1r/pppnpppp/5n2/8/2PP4/2N5/PP2QPPP/R1B1K1NR b KQkq - 2 8",
        "r1bq1rk1/ppp1nppp/2nb4/3p4/3P4/2P2N1P/PP2BPP1/RNBQ1RK1 b - - 0 8",
        "rnbqkb1r/p1pppppp/5n2/1p6/8/5NP1/PPPPPP1P/RNBQKB1R w KQkq - 0 3",
        "rnbq1rk1/pp2bppp/3p1n2/2pP4/2P5/5N1P/PP3PP1/RNBQKB1R w KQ - 1 8",
        "rnbq1rk1/pp2ppbp/3p1np1/2pP2B1/2P1P3/2N5/PP3PPP/R2QKBNR w KQ - 3 7",
        "r1bqkbnr/1p3ppp/p1n1p3/3pP3/3P4/3B1N2/PP3PPP/RNBQK2R b KQkq - 1 7",
        "rnbq1rk1/pp2ppbp/5np1/2pp4/3P1B2/4PN1P/PPP1BPP1/RN1Q1RK1 b - - 1 7",
        "r1bqkb1r/pp2pppp/2n2n2/2pp4/3P1B2/4PN2/PPP2PPP/RN1QKB1R w KQkq - 1 5",
        "r1bqk2r/2pp1ppp/p1n2n2/1pb1p3/4P3/1B3N2/PPPP1PPP/RNBQ1RK1 w kq - 2 7",
        "rnbqkbnr/pp2pppp/8/3p4/3P1B2/8/PP2PPPP/RN1QKBNR b KQkq - 1 4",
        "rnb1kb1r/pp2pppp/1q6/2pP4/4p2B/8/PPP1PPPP/1R1QKBNR b Kkq - 2 7",
        "rnbqk2r/ppp1ppbp/3p1np1/8/2PPPP2/2N5/PP4PP/R1BQKBNR b KQkq - 0 5",
        "rn1q1rk1/pbp1bppp/1p1ppn2/8/3P1B2/3BPN1P/PPPN1PP1/R2QK2R w KQ - 0 8",
        "rnbqkbnr/ppp2ppp/4p3/3p4/3PP3/8/PPP2PPP/RNBQKBNR w KQkq - 0 3",
        "r1bqk1nr/pp1pppbp/2n3p1/2p5/4P3/2N3P1/PPPPNP1P/R1BQKB1R w KQkq - 1 5",
        "rnb1kbnr/pp2pppp/2p5/4q3/8/8/PPPN1PPP/R1BQKBNR w KQkq - 0 6",
        "r2qk2r/pppb1ppp/2nb1n2/1B2p3/3pP3/P2P1N2/1PP1NPPP/R1BQ1RK1 b kq - 2 8",
        "r1bqk2r/pp2bpp1/2np1n1p/2p1p3/2B1P3/2NP4/PPP2PPP/R1BQNRK1 w kq - 0 8",
        "rn1qkb1r/1pp2pp1/p3pn2/3pNb1p/3P1B2/2NBP3/PPP2PPP/R2QK2R b KQkq - 1 7",
        "r1b1kb1r/1pp2ppp/p1n1pn2/3q4/2QP4/5NP1/PP1NPPBP/R1B1K2R b KQkq - 3 8",
        "r1bqkb1r/2p2ppp/p1np1n2/1p2p3/P3P3/1B3N2/1PPP1PPP/RNBQ1RK1 b kq - 0 7",
        "r1bqkb1r/pp1ppppp/2n2n2/6B1/3Q4/2N5/PPP1PPPP/R3KBNR w KQkq - 1 5",
        "r3kb1r/pp1n1ppp/1qp1pn2/3p1b2/2PP1B2/1QN1PN2/PP3PPP/R3KB1R w KQkq - 0 8",
        "rnbqkb1r/pp1ppppp/2p2n2/8/2PP4/8/PP2PPPP/RNBQKBNR w KQkq - 0 3",
        "rn1qk1nr/pp3ppp/2p1p3/2bp4/4P3/2NP1Q1P/PPP2PP1/R1B1KB1R w KQkq - 1 7",
        "r1bqk1nr/pppp1pbp/2n3p1/1B6/3PP3/5N2/PP3PPP/RNBQK2R b KQkq - 0 6",
        "rn1qkb1r/pp2pppp/2p2n2/3P1b2/3P4/2N1P3/PP3PPP/R1BQKBNR b KQkq - 0 5",
        "rnbqkb1r/1p3ppp/p2p1n2/4p3/4P3/1NN5/PPP1BPPP/R1BQK2R b KQkq - 1 7",
        "rnbqkb1r/ppp2ppp/3p1n2/8/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 4",
        "r1bqkb1r/pp3ppp/2np1n2/1N2p1B1/4P3/2N5/PPP2PPP/R2QKB1R b KQkq - 1 7",
        "rnbqk1nr/ppp2ppp/8/3p4/1b1P4/2NB4/PPP2PPP/R1BQK1NR b KQkq - 1 5",
        "rnbqkb1r/ppp1pp1p/5np1/3p4/5P2/5NP1/PPPPP2P/RNBQKB1R w KQkq - 0 4",
        "rnbqkb1r/1p3ppp/p2ppn2/8/2BNP3/2N5/PPP2PPP/R1BQK2R w KQkq - 0 7",
        "rnbqkb1r/pp2pppp/2p2n2/8/3PN3/8/PPP2PPP/R1BQKBNR w KQkq - 1 5",
        "rnb2rk1/pp2ppbp/1qpp1np1/8/3PP3/2N1BN1P/PPP2PP1/R1Q1KB1R w KQ - 1 8",
        "rnbqk2r/pp2bppp/4pn2/3P4/3P4/2N2N2/PP3PPP/R1BQKB1R b KQkq - 0 7",
        "rnbqkb1r/1p2pppp/p2p1n2/8/3NP3/2N5/PPP2PPP/R1BQKB1R w KQkq - 0 6",
        "rnbqk2r/pp1p1ppp/4p3/4P3/1b1N2Q1/P1n5/1PP2PPP/R1B1KB1R b KQkq - 0 8",
        "rnbqkb1r/ppp1pppp/5n2/3p4/3P3B/5P2/PPP1P1PP/RN1QKBNR w KQkq - 1 5",
        "rnbq1rk1/ppp2ppp/3ppn2/6B1/2PP4/P1Q5/1P2PPPP/R3KBNR b KQ - 1 7",
        "r1bqk1nr/pppp1ppp/2n5/2b1p3/2P5/6P1/PP1PPPBP/RNBQK1NR w KQkq - 3 4",
        "r1bqkbnr/ppppppp1/6n1/3P3p/4P2P/8/PPP2PP1/RNBQKBNR w KQkq - 0 5",
        "rn1qkb1r/pp3ppp/2n1p3/2ppPb2/3P1P2/2P1BN2/PP4PP/RN1QKB1R w KQkq - 2 8",
        "r1bqkb1r/pp3ppp/n1p1pn2/3p4/3P1B2/2N1P3/PPP2PPP/R2QKBNR b KQkq - 1 6",
        "rnbqkbnr/ppp2ppp/3p4/4p3/3P4/2N2N2/PPP1PPPP/R1BQKB1R b KQkq - 1 3",
        "r1b1k1nr/ppp2pbp/2p3p1/8/3NP3/8/PPP2PPP/RNB1K2R w KQkq - 1 8",
        "rnbqk1nr/ppppppbp/6p1/8/8/2P2N2/PP1PPPPP/RNBQKB1R w KQkq - 1 3",
        "rnbqkb1r/pp3ppp/4p3/8/3pP3/2P2N2/P4PPP/R1BQKB1R w KQkq - 0 8",
        "r1bq1rk1/pp2bppp/2n1pn2/2pp4/3P4/1P2PN2/PBPNBPPP/R2Q1RK1 b - - 2 8",
        "rn1qkb1r/pbp1pppp/1p1p1n2/6B1/3P4/2P2N2/PP2PPPP/RN1QKB1R w KQkq - 0 5",
        "r1bqk2r/pp1nbppp/2pp1n2/4p3/3PP3/2PB4/PP2NPPP/RNBQ1RK1 w kq - 1 7",
        "rnbqk2r/pp3ppp/2pb1p2/8/3P4/5N2/PPP2PPP/R1BQKB1R w KQkq - 2 7",
        "r1bq1rk1/pp2ppbp/2n2np1/2Pp4/2P2B2/4PN2/PP1N1PPP/R2QKB1R w KQ - 1 8",
        "r1bqkb1r/ppp2ppp/2pn4/4p3/3P4/5N2/PPP2PPP/RNBQ1RK1 w kq - 0 7",
        "rnbqkbnr/ppp3pp/8/3p1p2/3P4/3B1N2/PPP2PPP/RNBQK2R b KQkq - 1 5",
        "rnbq1rk1/pp2bppp/2pp1n2/4p3/2B1P1P1/3P1N1P/PPP2P2/RNBQK2R w KQ - 0 7",
        "r1bq1rk1/ppp1bppp/2n1pn2/4N3/2pP4/6P1/PP2PPBP/RNBQ1RK1 w - - 2 8",
        "rnbq1rk1/ppp1ppbp/3p1np1/8/2PPP3/2N2N2/PP3PPP/R1BQKB1R w KQ - 2 6",
        "rn1qk2r/pbppbppp/1p2pn2/8/2PP4/5NP1/PP2PPBP/RNBQ1RK1 b kq - 4 6",
        "1rbqkb1r/pp1p1ppp/4pn2/2p5/2PP4/4PN2/PP3PPP/RN1QKB1R w KQk - 1 7",
        "rn2kbnr/pp4pp/1qpp1p2/4p3/3P2b1/4PNB1/PPPN1PPP/R1Q1KB1R b KQkq - 1 7",
        "rnbqkb1r/pppp1ppp/4pn2/8/2P5/2N5/PP1PPPPP/R1BQKBNR w KQkq - 0 3",
        "r1b1kb1r/pppp2pp/2nq1n2/3Np3/2P5/5NB1/PP2PPPP/R2QKB1R b KQkq - 0 8",
        "1rbqk1nr/1p1pppbp/p1n3p1/2p5/P3P3/2N3P1/1PPPNPBP/R1BQK2R w KQk - 0 7",
        "r1bqk1nr/ppp1ppbp/2np2p1/1B6/3PP3/2N2N2/PPP2PPP/R1BQK2R b KQkq - 4 5",
        "rnbqkb1r/pppppp1p/5np1/8/2PP4/2N5/PP2PPPP/R1BQKBNR b KQkq - 1 3",
        "rn1qkbnr/pp2pppp/2p5/5b2/3P4/6N1/PPP2PPP/R1BQKBNR b KQkq - 2 5",
        "rnbqkb1r/pp2pppp/5n2/2pp4/3P1B2/4P3/PPP2PPP/RN1QKBNR w KQkq - 0 4",
        "rnbq1rk1/ppp1bppp/4pn2/3p2B1/2PP4/2N1PN2/PP3PPP/R2QKB1R b KQ - 0 6",
        "rnbqkbnr/pp2pppp/3p4/8/3NP3/8/PPP2PPP/RNBQKB1R b KQkq - 0 4",
        "r1bqkb1r/pppp1ppp/2n2n2/4p3/8/1P2P3/PBPP1PPP/RN1QKBNR w KQkq - 1 4",
        "r1bqkb1r/pppn1pp1/3p1n1p/4p3/2PPP3/2N2N2/PP3PPP/R1BQKB1R w KQkq - 0 6",
        "r2qkb1r/pp1n1ppp/5p2/3p1b2/3P4/2N1PN2/PP3PPP/R2QKB1R b KQkq - 1 8",
        "r1bqkbnr/pp1npppp/2pp4/8/3PPB2/5N2/PPP2PPP/RN1QKB1R b KQkq - 3 4",
        "r1bqk2r/pp3ppp/2nppn2/2p5/2PP4/2PBPN2/P4PPP/R1BQK2R w KQkq - 0 8",
        "rn1qkbnr/pp2pppp/2p5/5b2/2BP4/2N2N2/PPP2PPP/R1BQK2R b KQkq - 2 6",
        "rnbqkb1r/ppp2ppp/4pn2/3p4/2PP4/5NP1/PP2PP1P/RNBQKB1R b KQkq - 0 4",
        "r1bqk2r/pppp1ppp/2n2n2/1Bb1p3/4P3/2N2N2/PPPP1PPP/R1BQK2R w KQkq - 6 5",
        "rnbqkbnr/pp1ppp1p/6p1/2p5/3PP3/5N2/PPP2PPP/RNBQKB1R b KQkq - 0 3",
        "rnbqkb1r/ppp1pp1p/5np1/3p4/5P2/5NP1/PPPPP1BP/RNBQK2R b KQkq - 1 4",
        "rnbqk2r/pp4pp/2p1pn2/3p1p2/2PP1P2/5N2/PP2PPBP/RN1Q1RK1 b kq - 0 8",
        "rnbqk2r/pp3ppp/2pbpn2/3p4/2PP4/5NP1/PP1BPPBP/RN1Q1RK1 b kq - 1 7",
        "r1bqkb1r/pp2pppp/2n2n2/2pp2B1/3P4/4P3/PPPN1PPP/R2QKBNR w KQkq - 1 5",
        "rnbq1rk1/ppppppbp/5np1/8/2PP4/4PN2/PP3PPP/RNBQKB1R w KQ - 1 5",
        "rnbqkb1r/pppn1ppp/4p3/3pP3/3P1P2/2N5/PPP3PP/R1BQKBNR b KQkq - 0 5",
        "r1bqk1nr/pppp1ppp/2n5/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4",
        "r2qk2r/pppn1ppp/4pn2/2bp1b2/8/3P1NP1/PPPNPPBP/R1BQ1RK1 w kq - 3 7",
        "r3kb1r/1pp1nppp/p1np1q2/4p3/B3P1b1/2PP1N2/PP3PPP/RNBQ1RK1 w kq - 1 8",
        "rnbqkbnr/ppp1pppp/8/8/4N3/8/PPPP1PPP/R1BQKBNR b KQkq - 0 3",
        "rn1qkb1r/pbp2ppp/1p2pn2/3pN3/2PP4/2N1P3/PP3PPP/R1BQKB1R b KQkq - 3 6",
        "rnbqkbnr/pp2pppp/2pp4/8/3P1B2/5N2/PPP1PPPP/RN1QKB1R b KQkq - 1 3",
        "rnb1kbnr/pp3pp1/1q2p2p/3p4/3P3B/2N5/PP2PPPP/R2QKBNR w KQkq - 0 7",
        "r1bqkbnr/pp1ppp1p/2n3p1/2p5/2PPP3/5N2/PP3PPP/RNBQKB1R b KQkq - 0 4",
        "r2qkb1r/pp2pp1p/2bp1np1/2p5/4PP2/2NP1N2/PPP3PP/R1BQ1RK1 b kq - 1 8",
        "r1bqkb1r/ppp2ppp/2n1pn2/3pP3/3P4/5N2/PPPN1PPP/R1BQKB1R b KQkq - 0 5",
        "rnbqkbnr/pp1ppp1p/6p1/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 0 3",
        "rnbqk2r/pp2ppbp/1n1p2p1/8/2PP4/2N1B3/PP3PPP/2RQKBNR b Kkq - 3 8",
        "r1bqkbnr/pp1ppppp/2n5/2p5/4P3/2P2N2/PP1P1PPP/RNBQKB1R b KQkq - 0 3",
        "rn1qk1nr/pbpp2pp/1p2p3/3P4/1bP1B3/8/PP1N1PPP/R1BQK1NR b KQkq - 0 7",
        "rn1qkb1r/ppp1pppp/5n2/3pN2b/8/1P4P1/PBPPPP1P/RN1QKB1R b KQkq - 0 5",
        "rnbqkb1r/pp2pppp/2p2n2/3p4/8/2PP1N2/PP2PPPP/RNBQKB1R w KQkq - 1 4",
        "r1bq1rk1/ppppnppp/2n5/4p3/1bB1P3/2N2N2/PPPP1PPP/R1BQ1RK1 w - - 8 6",
        "r1bqkb1r/ppp2ppp/2n2n2/3P4/8/4Q3/PPPB1PPP/RN2KBNR b KQkq - 0 6",
        "r1bqk2r/pp2bppp/2n1pn2/3p2B1/3P4/2N1PN2/PP3PPP/R2QKB1R w KQkq - 1 8",
        "rnbq1rk1/ppp1ppbp/1n4p1/8/3PP3/2N1BP2/PP1Q2PP/R3KBNR b KQ - 6 8",
        "rnbqk1nr/pp3ppp/4p3/2bpP3/8/3B4/PPP2PPP/RNBQK1NR w KQkq - 0 6",
        "rnbqk1nr/pp4pp/2pbp3/3p1p2/2PP4/5NP1/PPQ1PP1P/RNB1KB1R w KQkq - 0 6",
        "rnbq1rk1/pp3ppp/4pn2/2Pp2B1/2P5/P1Q5/1P2PPPP/R3KBNR b KQ - 0 8",
        "r1bqk1nr/pp1nppbp/2p3p1/3p4/4P3/3P1NP1/PPPN1P1P/R1BQKB1R w KQkq - 1 6",
        "rnbqk2r/ppp1bpp1/4pn1p/3p4/2PP4/2N1PN2/PP3PPP/R1BQKB1R w KQkq - 1 6",
        "r1bqkb1r/2p2ppp/p1n5/1p1pp3/3Pn3/1B3N2/PPP2PPP/RNBQ1RK1 w kq - 0 8",
        "1r1qkbnr/pppn1ppp/3p4/4p2b/3PP3/1QP2N1P/PP1N1PP1/R1B1KB1R b KQk - 0 7",
        "rnbqkb1r/ppp1pppp/5n2/3p4/3P1B2/5N2/PPP1PPPP/RN1QKB1R b KQkq - 3 3",
        "r1bq1rk1/pppnppbp/5np1/3p2B1/3P4/4PN2/PPPNBPPP/R2QK2R w KQ - 3 7",
        "rnbqkb1r/ppp2ppp/3ppn2/8/2P1P3/2N3P1/PP1P1P1P/R1BQKBNR b KQkq - 0 4",
        "rnb1kb1r/pp3ppp/1q2pn2/8/2pP1B2/5N2/PP1N1PPP/R2QKB1R w KQkq - 0 8",
        "r1bqk2r/pppp1ppp/5n2/2b1p3/2P1P3/2N4P/PPP1NPP1/R1BQK2R b KQkq - 0 7",
        "r1bqkb1r/pp1p1ppp/2n1p3/2pnP3/3P4/2P2N2/PP3PPP/RNBQKB1R w KQkq - 0 6",
        "r1bqkb1r/ppp2ppp/2n2n2/3pp3/3P4/5NP1/PPP1PPBP/RNBQ1RK1 b kq - 0 5",
        "r1bqkbnr/pp1npppp/3p4/1Bp5/4P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 2 4",
        "rnbqkbnr/pp2pppp/2pp4/8/3P4/5N2/PPP1PPPP/RNBQKB1R w KQkq - 0 3",
        "rnbqk2r/1pp2ppp/p3p3/3nN3/1bpP4/2N3P1/PP2PPBP/R1BQK2R w KQkq - 4 8",
        "rnbqk1nr/pp1pppbp/6p1/8/3pP3/2N5/PPP1NPPP/R1BQKB1R w KQkq - 0 5",
        "r2qkb1r/ppp2ppp/2n1b3/3np3/8/2N2NP1/PPPP1PBP/R1BQ1RK1 b kq - 5 7",
        "rnbqkb1r/ppp1pppp/5n2/3p4/8/2P2N2/PP1PPPPP/RNBQKB1R w KQkq - 1 3",
        "rnbqkbnr/pp3ppp/8/2pp4/3P4/2N2N2/PP2PPPP/R1BQKB1R b KQkq - 1 5",
        "rnbqk2r/ppppppbp/5np1/8/8/5NP1/PPPPPPBP/RNBQ1RK1 b kq - 3 4",
        "rnb1kbnr/ppp1pppp/8/3q4/8/2N5/PPPP1PPP/R1BQKBNR b KQkq - 1 3",
        "r1bqkb1r/pp3ppp/2n1pn2/3p2B1/3P4/2N2N2/PP2PPPP/R2QKB1R w KQkq - 0 7",
        "r1bqkbnr/ppp2ppp/2n5/4P3/2Pp4/5NP1/PP2PP1P/RNBQKB1R b KQkq - 0 5",
        "rnbqk2r/ppp2pp1/5n2/2b1p3/2B4p/2N4P/PPPP1PP1/R1BQK1NR b KQkq - 3 8",
        "r1bqkb1r/pppn1ppp/4pn2/8/2pP1B2/2N2N2/PP2PPPP/R2QKB1R w KQkq - 0 6",
        "rnbqkb1r/pp3ppp/2p2n2/3pp3/4P3/3P3P/PPPN1PP1/R1BQKBNR w KQkq - 1 5",
        "rnbqkb1r/pp2pppp/3p1n2/8/3NP3/2N5/PPP2PPP/R1BQKB1R b KQkq - 2 5",
        "rnbqkbnr/ppp2ppp/4p3/3p4/2P5/2N5/PP1PPPPP/R1BQKBNR w KQkq - 0 3",
        "r1bq1rk1/ppppbppp/2B2n2/4p3/8/2P2N2/PP1P1PPP/RNBQR1K1 b - - 0 8",
        "r1bqkbnr/pppn1ppp/4p3/8/3PN3/8/PPP2PPP/R1BQKBNR w KQkq - 1 5",
        "rnbqk1nr/ppppppbp/6p1/8/4P2P/5N2/PPPP1PP1/RNBQKB1R b KQkq - 0 3",
        "rnbq1rk1/pppp1ppp/4pn2/8/2PP4/P1P5/4PPPP/R1BQKBNR w KQ - 1 6",
        "r1bqk2r/pp3pbp/2n2np1/2ppp3/4P3/2PP1N2/PPQNBPPP/R1B2RK1 b kq - 3 8",
        "rnbqkb1r/pp1p1ppp/5n2/2pP4/8/2N5/PP2PPPP/R1BQKBNR b KQkq - 0 5",
        "r1bqkb1r/pp1npppp/2p2n2/6B1/3PP3/2N5/PPP3PP/R2QKBNR b KQkq - 0 6",
        "rnbqkbnr/pp1p1ppp/4p3/8/3P4/5N2/PP2PPPP/RNBQKB1R b KQkq - 0 4",
        "rnbq1rk1/ppp2pb1/3p1npp/4p3/2PPP3/2N1B3/PP2BPPP/R2QK1NR w KQ - 0 8",
        "r1bqkbnr/pp1npp1p/3p2p1/1Bp1P3/P7/5N2/1PPP1PPP/RNBQK2R b KQkq - 0 5",
        "rn1qkb1r/pp2pppp/2p2n2/3p1b2/2PP4/4PN2/PP3PPP/RNBQKB1R w KQkq - 1 5",
        "rnbq1rk1/ppp1bppp/4pn2/3p4/2PP4/5NP1/PP2PPBP/RNBQ1RK1 b - - 4 6",
        "r1bqkbnr/pp1ppppp/2n5/2p5/4P3/2N5/PPPP1PPP/R1BQKBNR w KQkq - 2 3",
        "r2qkbnr/ppp1pppp/2n5/3pPb2/3P4/2P5/PP3PPP/RNBQKBNR b KQkq - 0 4",
        "rnbqkb1r/pp2pppp/2p2n2/3p4/2PP4/2N5/PP2PPPP/R1BQKBNR w KQkq - 0 4",
        "rn1qkbnr/pp2pppp/2p5/3p1b2/8/3PP1P1/PPP2P1P/RNBQKBNR w KQkq - 1 4",
        "rnbqkb1r/pp2pppp/2p2n2/8/P1pP4/2N2N2/1P2PPPP/R1BQKB1R b KQkq - 0 5",
        "rn2kbnr/pp3pp1/2p1p3/q2pP2p/3P3P/3Q4/PPPN1PP1/R1B1K1NR w KQkq - 0 8",
        "r1b1kb1r/pp3ppp/1qn1pn2/3p4/5B2/2P1PN2/PP3PPP/RNQ1KB1R b KQkq - 0 8",
        "rnbqkb1r/pp3ppp/2p1pn2/3p2B1/2PP4/2N2N2/PP2PPPP/R2QKB1R b KQkq - 1 5",
        "r2qkb1r/1p1nnppp/p1p1p3/3pPb2/3P4/5N2/PPPNBPPP/R1BQ1RK1 w kq - 0 8",
        "rnb1k2r/pp1p1ppp/4p3/q3P3/1b1NQ3/2N5/PPP2PPP/R1B1KB1R b KQkq - 0 8",
        "rnbqkb1r/pp2pppp/8/3n4/3NP3/8/PP3PPP/RNBQKB1R b KQkq - 0 6",
        "rnbqkb1r/ppppnppp/8/8/4Pp2/5N2/PPPP2PP/RNBQKB1R w KQkq - 2 4",
        "rnbqkb1r/pppp1ppp/8/4P3/2P3n1/8/PP2PPPP/RNBQKBNR w KQkq - 1 4",
        "r1bq1rk1/pppnppbp/3p1np1/8/3P4/1P2PN2/P1P1BPPP/RNBQ1RK1 w - - 1 7",
        "rn1qk1nr/pb2ppbp/1p1p2p1/2p5/2P5/2N2NP1/PP1PPPBP/R1BQ1RK1 w kq - 0 7",
        "rn1qkbnr/pp3pp1/2p1p2p/3p4/2P3b1/1P2PN2/P2PBPPP/RNBQ1RK1 b kq - 1 6",
        "r1bqkbnr/pppn1ppp/3p4/4p3/3P4/2P2N2/PP2PPPP/RNBQKB1R w KQkq - 0 4",
        "rn1qkb1r/pbpp1p1p/1p2pnp1/8/2PP4/P3PN2/1P3PPP/RNBQKB1R w KQkq - 0 6",
        "rnbqkb1r/ppp2ppp/4pn2/3p4/2PP4/2N2N2/PP2PPPP/R1BQKB1R b KQkq - 1 4",
        "rnbqk2r/ppp1bppp/4pn2/3p4/2PP4/2N2N2/PP2PPPP/R1BQKB1R w KQkq - 2 5",
        "r1bqk2r/ppppbppp/2n3n1/1B2p3/4P2P/3P1N2/PPP2PP1/RNBQK2R w KQkq - 1 6",
        "rnbk1bnr/ppp2ppp/8/4p3/8/2N5/PPP1PPPP/R1B1KBNR w KQ - 0 5",
        "rnbq1rk1/pp2bppp/2p1pn2/3p2B1/2PP4/2N1PN2/PP3PPP/R2QKB1R w KQ - 1 7",
        "r1bqk2r/pppp1ppp/2n5/2b5/2BPn3/5N2/PP3PPP/RNBQ1RK1 b kq - 0 7",
        "rn1qkb1r/ppp1pppp/5n2/3p1b2/3P1B2/2N2P2/PPP1P1PP/R2QKBNR b KQkq - 0 4",
        "r1bqkb1r/pp2pppp/2n2n2/3p4/3P4/2N2N2/PP2PPPP/R1BQKB1R w KQkq - 2 6",
        "r1bqkbnr/pp2pp1p/2np2p1/8/3NP3/2N1B3/PPP2PPP/R2QKB1R b KQkq - 1 6",
        "r3kbnr/ppp1pppp/2nq2b1/1B6/6P1/2N2N1P/PPPP1P2/R1BQK2R w KQkq - 1 8",
        "r1bqkbnr/1ppp1ppp/p1n5/4p3/B3P3/5N2/PPPP1PPP/RNBQK2R b KQkq - 1 4",
        "r1bq1rk1/pppn1ppp/3b1n2/1B2p3/3P4/5N2/PPP2PPP/RNBQ1RK1 w - - 0 8",
        "rnbq1rk1/1pp2ppp/3ppn2/p7/2PPP3/P2B1N2/1P1N1PPP/R2QK2R b KQ - 1 8",
        "r1bqkb1r/ppp2ppp/2n2n2/3Pp1N1/2B5/8/PPPP1PPP/RNBQK2R b KQkq - 0 5",
        "rnbqkb1r/pp1ppppp/5n2/2p5/2PP4/8/PP2PPPP/RNBQKBNR w KQkq - 0 3",
        "rn1qkbnr/pbppp1pp/1p6/5p2/8/3P1NP1/PPP1PPBP/RNBQK2R b KQkq - 0 4",
        "rnb1kbnr/ppq1pppp/2pp4/8/3PP3/2N3P1/PPP2P1P/R1BQKBNR b KQkq - 0 4",
        "rnbqk2r/ppp1ppbp/5np1/3p4/3P1B2/4PN2/PPP2PPP/RN1QKB1R w KQkq - 1 5",
        "rnbqkb1r/pp2pppp/2p2n2/3p4/2PP4/2N5/PP2PPPP/R1BQKBNR w KQkq - 2 4",
        "rnbqkb1r/pppppppp/1n6/4P3/2PP4/8/PP3PPP/RNBQKBNR b KQkq - 0 4",
        "r1bqkbnr/ppp2ppp/2n5/4P3/2Pp4/P4N2/1P2PPPP/RNBQKB1R b KQkq - 0 5",
        "r2qkb1r/pp2pppp/1np2n2/5b2/P2P4/2N1N3/1P2PPPP/R1BQKB1R b KQkq - 2 8",
        "r1bqkb1r/pppp1pp1/2n2n1p/4p3/2B1P3/3P1N2/PPP2PPP/RNBQK2R w KQkq - 0 5",
        "rnbq1rk1/pp3ppp/2pb1n2/3pp3/2P5/1P4P1/PBQPPPBP/RN2K1NR w KQ - 3 7",
        "rnbqkb1r/pppp1ppp/4pn2/8/2PP4/6P1/PP2PP1P/RNBQKBNR b KQkq - 0 3",
        "rnbq1rk1/ppp1ppbp/3p1np1/8/3PPP2/2PB1N2/PP4PP/RNBQK2R b KQ - 4 6",
        "rnbqk2r/pp2npp1/4p2p/2ppP2P/3P4/P1P5/2P2PP1/R1BQKBNR b KQkq - 0 8",
        "r1bq1rk1/ppp1ppbp/2np1np1/8/3PP3/2N1BN1P/PPP2PP1/R2QKB1R w KQ - 1 7",
        "r2qk2r/pp3ppp/2nbpn2/2pp1b2/3P1P2/2PBPN2/PP4PP/RNBQ1RK1 w kq - 2 8",
        "r1bqk2r/ppp2ppp/3p1n2/n1b1p3/2B1PP2/2NP1N2/PPP3PP/R1BQK2R w KQkq - 3 7",
        "rn1qkb1r/pbpp1ppp/1p2pn2/6B1/3P4/2P2N2/PP2PPPP/RN1QKB1R w KQkq - 0 5",
        "rn1qkb1r/pp3ppp/2p1pn2/3p1b2/3P4/5NP1/PPP1PPBP/RNBQ1RK1 w kq - 0 6",
        "rn1qkb1r/ppp1pppp/3p1nb1/8/2PP4/2N2N2/PP2PPPP/R1BQKB1R w KQkq - 4 5",
        "r1bqkb1r/pp1p1ppp/2n1pn2/2p3B1/3P4/4PN2/PPPN1PPP/R2QKB1R b KQkq - 0 5",
        "rnbqkb1r/pp1ppppp/5n2/8/8/4BN2/PPP1BPPP/RN1QK2R b KQkq - 0 5",
        "rnbqkb1r/ppp1pppp/3p1n2/8/2PP4/8/PP2PPPP/RNBQKBNR w KQkq - 1 3",
        "rnbqkbnr/ppp2ppp/4p3/3p4/8/5NP1/PPPPPP1P/RNBQKB1R w KQkq - 0 3",
        "rn1qkb1r/pp2pppp/2p2n2/3p1b2/3P1B2/2N1P3/PPP2PPP/R2QKBNR w KQkq - 1 5",
        "r1bqkbnr/ppp2ppp/2n5/3p4/3P4/3B1N2/PPP2PPP/RNBQK2R b KQkq - 2 5",
        "r1bq1rk1/p1pnppbp/1p1p1np1/8/2BP1B2/2P1PN2/PP1N1PPP/R2QK2R w KQ - 0 8",
        "rnbqk1nr/p3ppbp/2pp2p1/1p6/3PPP2/2N2N2/PPP3PP/R1BQKB1R w KQkq - 0 6",
        "r1bqkbnr/pp2pp1p/2p3p1/2p5/4P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 0 5",
        "rnbqr1k1/ppp1ppbp/3p1np1/8/3P4/1P2PN2/P1P1BPPP/RNBQ1RK1 w - - 1 7",
        "r1bqk2r/pp2ppbp/2np1np1/8/2P1P3/2N1BP2/PP4PP/R2QKBNR b KQkq - 2 8",
        "r1bqk2r/pppp1ppp/2n2n2/2b1p3/2B1P3/3P1N2/PPP2PPP/RNBQ1RK1 b kq - 2 5",
        "rnbqk2r/ppppb1pp/4pn2/5p2/2PP4/6P1/PP2PPBP/RNBQK1NR w KQkq - 2 5",
        "r1bqk1nr/ppp1n1bp/3p2p1/3Ppp2/2P5/1PN1PN2/PB3PPP/R2QKB1R b KQkq - 1 8",
        "rn2kbnr/pp2ppp1/1qp4p/3p1b2/3P3B/4P3/PPPN1PPP/R2QKBNR w KQkq - 2 6",
        "r1bqkb1r/pp1n1ppp/2p2n2/4p3/8/1P3NP1/P1P1PPBP/RNBQ1RK1 b kq - 0 7",
        "rnbqkb1r/pp1ppppp/5n2/2pP4/2P5/8/PP2PPPP/RNBQKBNR b KQkq - 0 3",
        "rnbqkb1r/pp2pppp/2p2n2/3p4/8/5NP1/PPPPPPBP/RNBQ1RK1 b kq - 3 4",
        "rnbqkb1r/pppppppp/8/3nP3/3P4/8/PPP2PPP/RNBQKBNR b KQkq - 0 3",
        "rnbqkb1r/1p2pppp/p2p1n2/8/3NPP2/2N5/PPP3PP/R1BQKB1R b KQkq - 0 6",
        "r1bqkbnr/pppp1p1p/2n3p1/4p3/2P5/2N3P1/PP1PPPBP/R1BQK1NR b KQkq - 1 4",
        "rnbqk1nr/pp2ppbp/2p3p1/3p4/4P3/3P1N2/PPPN1PPP/R1BQKB1R w KQkq - 2 5",
        "r2qk2r/1pp2ppp/p1npbn2/2bNp1B1/2B1P3/3P1N1P/PPP2PP1/R2QK2R b KQkq - 3 8",
    };

    private final int millisPerMove, movesToDraw, totalPositions;

    public AiArena(int millisPerMove){
        this.millisPerMove = millisPerMove;
        this.movesToDraw = 150;
        this.totalPositions = FEN_DATA.length;
    }

    public AiArena(int millisPerMove, int movesToDraw, int totalPositions){
        this.millisPerMove = millisPerMove;
        this.movesToDraw = movesToDraw;
        this.totalPositions = totalPositions;
    }

    public void playAgainstBasicAlphaBeta(){
        MoveMasks masks = new MoveMasks();
        Negamax negamax = new Negamax();
        int wonByNewAi = 0, draws = 0;

        for(int i = 0; i < totalPositions; i++){
            Board board = new Board(FEN_DATA[i]);
            while(!board.isGameLost(masks, MoveGenerator.generateLegalMoves(board, masks).length) && board.moves.size() < movesToDraw){
                Move next;
                if(board.getTurn() == Color.WHITE){
                    next = negamax.getBestMoveTimed(board, millisPerMove, masks);
                }else{
                    next = AlphaBetaTest.getBestMoveTimed(board, millisPerMove, masks);
                }
                board.doMove(next);
            }

            if(board.isGameLost(masks, MoveGenerator.generateLegalMoves(board, masks).length) && board.getTurn() == Color.BLACK){
                wonByNewAi++;
            }else if(board.moves.size() == movesToDraw){
                draws++;
            }
            negamax.clearTable();
            System.out.println("NEXT GAME");
        }

        for(int i = 0; i < totalPositions; i++){
            Board board = new Board(FEN_DATA[i]);
            while(!board.isGameLost(masks, MoveGenerator.generateLegalMoves(board, masks).length) && board.moves.size() < movesToDraw){
                Move next;
                if(board.getTurn() == Color.BLACK){
                    next = negamax.getBestMoveTimed(board, millisPerMove, masks);
                }else{
                    next = AlphaBetaTest.getBestMoveTimed(board, millisPerMove, masks);
                }
                board.doMove(next);
            }

            if(board.isGameLost(masks, MoveGenerator.generateLegalMoves(board, masks).length) && board.getTurn() == Color.WHITE){
                wonByNewAi++;
            }else if(board.moves.size() == movesToDraw){
                draws++;
            }
            negamax.clearTable();
            System.out.println("NEXT GAME");
        }

        double newAiWonPercentage = (((double) wonByNewAi) / (totalPositions * 2)) * 10;
        double drawPercentage = (((double) draws) / (totalPositions * 2)) * 10;
        double oldAiWonPercentage = 10 - (newAiWonPercentage + drawPercentage);

        System.out.println("Neue KI: " + wonByNewAi + ", Unentschieden: " + draws + ", Alte KI: " + (totalPositions * 2 - (wonByNewAi + draws)));
        for(int i = 0; i < (int) newAiWonPercentage; i++){
            System.out.print("🟩");
        }
        for(int i = 0; i < (int) drawPercentage; i++){
            System.out.print("🟨");
        }
        for(int i = 0; i < (int) oldAiWonPercentage; i++){
            System.out.print("🟥");
        }
        System.out.printf("\n");
    }

    public void playAgainstBasicAlphaBeta(int threadCount){
        MoveMasks masks = new MoveMasks();
        int wonByNewAi = 0, draws = 0;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        ArrayList<Callable<int[]>> tasks = new ArrayList<Callable<int[]>>();
        int fensPerThread = totalPositions / threadCount;

        for(int i = 0; i < threadCount; i++){
            final String[] currentFens = Arrays.copyOfRange(FEN_DATA, i * fensPerThread, i * fensPerThread + fensPerThread);
            Callable<int[]> c = new Callable<int[]>() {
                @Override
                public int[] call() throws Exception {
                    return playOnThread(currentFens);
                }
            };
            tasks.add(c);
        }

        try {
            List<Future<int[]>> results = executorService.invokeAll(tasks);
            for(int i = 0; i < results.size(); i++){
                int[] values = results.get(i).get();
                wonByNewAi += values[0];
                draws += values[1];
            }

            double newAiWonPercentage = (((double) wonByNewAi) / (totalPositions * 2)) * 10;
            double drawPercentage = (((double) draws) / (totalPositions * 2)) * 10;
            double oldAiWonPercentage = 10 - (newAiWonPercentage + drawPercentage);

            System.out.println("Neue KI: " + wonByNewAi + ", Unentschieden: " + draws + ", Alte KI: " + (totalPositions * 2 - (wonByNewAi + draws)));
            for(int i = 0; i < (int) newAiWonPercentage; i++){
                System.out.print("🟩");
            }
            for(int i = 0; i < (int) drawPercentage; i++){
                System.out.print("🟨");
            }
            for(int i = 0; i < (int) oldAiWonPercentage; i++){
                System.out.print("🟥");
            }
            System.out.printf("\n");
        }catch (InterruptedException | ExecutionException e){
            System.out.println(e.toString());
        }

        executorService.shutdown();
    }

    private int[] playOnThread(String[] fens){
        MoveMasks masks = new MoveMasks();
        Negamax negamax = new Negamax();
        int wonByNewAi = 0, draws = 0;

        for(int i = 0; i < fens.length; i++){
            Board board = new Board(fens[i]);
            while(!board.isGameLost(masks, MoveGenerator.generateLegalMoves(board, masks).length) && board.moves.size() < movesToDraw){
                Move next;
                if(board.getTurn() == Color.WHITE){
                    next = negamax.getBestMoveTimed(board, millisPerMove, masks);
                }else{
                    next = AlphaBetaTest.getBestMoveTimed(board, millisPerMove, masks);
                }
                if(next != null){
                    board.doMove(next);
                }else{
                    break;
                }
            }

            if(board.isGameLost(masks, MoveGenerator.generateLegalMoves(board, masks).length) && board.getTurn() == Color.BLACK){
                wonByNewAi++;
            }else if(board.moves.size() == movesToDraw){
                draws++;
            }
            System.out.println("NEXT GAME");
            negamax.clearTable();
        }

        for(int i = 0; i < fens.length; i++){
            Board board = new Board(fens[i]);
            while(!board.isGameLost(masks, MoveGenerator.generateLegalMoves(board, masks).length) && board.moves.size() < movesToDraw){
                Move next;
                if(board.getTurn() == Color.BLACK){
                    next = negamax.getBestMoveTimed(board, millisPerMove, masks);
                }else{
                    next = AlphaBetaTest.getBestMoveTimed(board, millisPerMove, masks);
                }
                if(next != null){
                    board.doMove(next);
                }
            }

            if(board.isGameLost(masks, MoveGenerator.generateLegalMoves(board, masks).length) && board.getTurn() == Color.WHITE){
                wonByNewAi++;
            }else if(board.moves.size() == movesToDraw){
                draws++;
            }
            System.out.println("NEXT GAME");
        }

        return new int[]{wonByNewAi, draws};
    }

}
