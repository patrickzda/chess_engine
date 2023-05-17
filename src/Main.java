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
        //String fens[] = new String[]{"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1","4r1k1/1bqr1pbp/p2p2p1/4p1B1/2p1P3/PnP2N1P/BP2QPP1/3RR1K1 w Qq - 0 1","8/6k1/5bP1/4p2p/2qpP2P/1b2BK2/p1r5/6R1 w - - 1 2","6k1/r4ppp/r7/1b6/8/8/4QPPP/4R1K1 w - - 0 1"};
        //MoveGeneratorPerformance moveGeneratorPerformance = new MoveGeneratorPerformance();
        //moveGeneratorPerformance.measureAveragePerformanceOnBoards(fens,10000);
        //EvaluationPeformance evaluationPeformance = new EvaluationPeformance();
        //evaluationPeformance.measureAveragePerformanceOfEvaluation(fens,10000);
        //AlphaBetaPerfomance.measureAveragePerformanceOfMiniMax(fens,100,5);
        //MiniMaxPerformance.measureAveragePerformanceOfMiniMax(fens,100,5);

        playAgainstItself();
    }

    static void playAgainstItself(){
        Board board = new Board();
        MoveMasks masks = new MoveMasks();
        int counter = 0;

        GameState gameState;

        while(counter < 400){
            Move m = AlphaBeta.getBestMove(board, 5, masks);
            board.doMove(m);

            gameState = board.getGameState(masks);
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