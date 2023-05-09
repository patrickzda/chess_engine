import engine.ai.DummyAi;
import engine.ai.Evaluation;
import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.Board;
import engine.representation.Color;
import engine.representation.Move;
import engine.representation.PieceType;
import performance.MoveGeneratorPerformance;

import java.util.ArrayList;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        String fens[] = new String[]{"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq","4r1k1/1bqr1pbp/p2p2p1/4p1B1/2p1P3/PnP2N1P/BP2QPP1/3RR1K1 w KQkq","8/6k1/5bP1/4p2p/3pP2P/1b1qBK2/p1r5/6R1 b KQkq"};
        MoveGeneratorPerformance moveGeneratorPerformance = new MoveGeneratorPerformance();
        moveGeneratorPerformance.measureAveragePerformanceOnBoards(fens,1000);
    }

    static void playAgainstItself(){
        Board board = new Board();
        MoveMasks masks = new MoveMasks();
        int counter = 0;

        while(!board.isGameWon(masks) && counter < 40){
            Move m = DummyAi.selectMove(board);
            board.doMove(m);
            if(board.getTurn() == Color.WHITE){
                System.out.println("Black played: " + m + ": " + board.toFENString());
            }else{
                System.out.println("White played: " + m + ": " + board.toFENString());
            }
            counter++;
        }
    }

}