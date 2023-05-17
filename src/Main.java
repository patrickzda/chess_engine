import engine.ai.AlphaBeta;
import engine.ai.DummyAi;
import engine.ai.Evaluation;
import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.*;
import performance.MoveGeneratorPerformance;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        playAgainstItself();
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