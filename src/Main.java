import engine.ai.AlphaBeta;
import engine.ai.DummyAi;
import engine.ai.Evaluation;
import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.*;
import performance.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        //playAgainstItself();
        performance();

        //playMove(args);
    }

    static void performance(){
        String fens[] = new String[]{"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 0" ,"4r1k1/1bqr1pbp/p2p2p1/4p1B1/2p1P3/PnP2N1P/BP2QPP1/3RR1K1 w KQkq - 0 0","8/6k1/5bP1/4p2p/3pP2P/1b1qBK2/p1r5/6R1 b KQkq - 0 0"};
        //MoveGeneratorPerformance moveGeneratorPerformance = new MoveGeneratorPerformance();
        //moveGeneratorPerformance.measureAveragePerformanceOnBoards(fens,10000);
        EvaluationPeformance evaluationPeformance = new EvaluationPeformance();
        evaluationPeformance.measureAveragePerformanceOfEvaluation(fens,10000);
        AlphaBetaPerfomance.measureAveragePerformanceOfAlphaBeta(fens,1,4);
        MiniMaxPerformance.measureAveragePerformanceOfMiniMax(fens,1,4);
        //AiPerformance.howMuchElohasMyAI(1320,5,1000);
    }
    static void playMove(String[] args){
        Board board = new Board(args[0]);
        Move m = AlphaBeta.getBestMoveTimed(board, new MoveMasks(), 1500);
        board.doMove(m);
        System.out.println(board.toFENString());
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