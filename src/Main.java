import engine.ai.AlphaBeta;
import engine.ai.DummyAi;
import engine.ai.Evaluation;
import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.*;
import performance.AlphaBetaPerfomance;
import performance.EvaluationPeformance;
import performance.MiniMaxPerformance;
import performance.MoveGeneratorPerformance;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        String fens[] = new String[]{"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1","4r1k1/1bqr1pbp/p2p2p1/4p1B1/2p1P3/PnP2N1P/BP2QPP1/3RR1K1 w Qq - 0 1","8/6k1/5bP1/4p2p/2qpP2P/1b2BK2/p1r5/6R1 w - - 1 2"};
        MoveGeneratorPerformance moveGeneratorPerformance = new MoveGeneratorPerformance();
        moveGeneratorPerformance.measureAveragePerformanceOnBoards(fens,10000);
        EvaluationPeformance evaluationPeformance = new EvaluationPeformance();
        evaluationPeformance.measureAveragePerformanceOfEvaluation(fens,10000);
        AlphaBetaPerfomance.measureAveragePerformanceOfMiniMax(fens,2);
        MiniMaxPerformance.measureAveragePerformanceOfMiniMax(fens,2);


//        Board b = new Board("4k3/Pp5P/1p1p1r2/2pp1P1P/3N1PpP/4P1K1/1p1P2P1/1Q6 w - - 0 1");
//        System.out.println(Evaluation.getBlockedPawnCount(b, Color.WHITE));

        //playAgainstItself();
    }

    static void playAgainstItself(){
        Board board = new Board();
        MoveMasks masks = new MoveMasks();
        int counter = 0;

        GameState gameState;

        while(counter < 400){
            Move m = AlphaBeta.getBestMove(board, 4, masks);
            board.doMove(m);

            gameState = board.gameState(masks);
            System.out.println("Zug " + (counter + 1) + ": " + gameState);

            if(board.getTurn() == Color.WHITE){
                System.out.println("Black played: " + m + ": " + board.toFENString());
            }else{
                System.out.println("White played: " + m + ": " + board.toFENString());
            }
            System.out.println(board);

            counter++;

            if (gameState == GameState.DRAW || gameState == GameState.BLACK_WON || gameState == GameState.WHITE_WON) {
                break;
            }
        }
    }

}