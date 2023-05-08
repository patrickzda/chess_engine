package test;

import engine.ai.Evaluation;
import engine.representation.Board;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EvaluationTest {
    @Test
    public void Evaluation() {
        Board[] boards = {
                new Board("r7/2p1Qp2/p1kp2p1/8/4P3/8/PPPP1PPP/RNB1K2R w KQ - 1 13"),
                new Board("r7/2p1Qp2/p1kp2p1/8/4P3/8/PPPP1PPP/RNB1K2R b KQ - 1 13"),
                new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"),
                new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1"),
        };

        int[] results = {23, -23, 0, 0};

        for (int i = 0; i < results.length; i++) {
            assertEquals(results[i], Evaluation.evaluate(boards[i]));
        }
    }
}
