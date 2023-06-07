import engine.ai.AlphaBeta;
import engine.ai.Evaluation;
import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.*;
import performance.*;
import test.FENData;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        //playMove(args);
        measureAverageTimeOnFENData(4, 0, 25);
    }

    static void measureAverageTimeOnFENData(int depth, int startIndex, int endIndex){
        long averageTime = 0;
        MoveMasks masks = new MoveMasks();
        for(int i = startIndex; i < endIndex; i++){
            String currentFEN = FENData.FEN_DATA[i] + " 0 1";
            averageTime = averageTime + FENData.getTimeOfDepth(new Board(currentFEN), masks, depth);
        }
        System.out.println((averageTime / (endIndex - startIndex)) / 1000000 + " ms");
    }

    static void measureAverageDepthOnFENData(int millisPerSearch, int startIndex, int endIndex){
        int averageDepth = 0;
        MoveMasks masks = new MoveMasks();
        for(int i = startIndex; i < endIndex; i++){
            String currentFEN = FENData.FEN_DATA[i] + " 0 1";
            averageDepth = averageDepth + FENData.getDepthOfBestMoveTimed(new Board(currentFEN), masks, millisPerSearch);
        }
        System.out.println(averageDepth / (endIndex - startIndex));
    }

    static void performance(){
        String fens[] = new String[]{"2k5/6q1/3P1P2/4N3/8/1K6/8/8 w - - 0 1" ,"4r1k1/1bqr1pbp/p2p2p1/4p1B1/2p1P3/PnP2N1P/BP2QPP1/3RR1K1 w Qq - 0 1","6k1/r4ppp/r7/1b6/8/8/4QPPP/4R1K1 w - - 0 1"};
        //MoveGeneratorPerformance moveGeneratorPerformance = new MoveGeneratorPerformance();
        //moveGeneratorPerformance.measureAveragePerformanceOnBoards(fens,10000);
        EvaluationPeformance evaluationPeformance = new EvaluationPeformance();
        evaluationPeformance.measureAveragePerformanceOfEvaluation(fens,10000);
        AlphaBetaPerfomance.measureAveragePerformanceOfAlphaBeta(fens,1,4);
        //MiniMaxPerformance.measureAveragePerformanceOfMiniMax(fens,1,4);
        //AiPerformance.howMuchElohasMyAI(1320,5,1000);
    }
    static void playMove(String[] args){
        Board board = new Board(args[0]);
        Move m = AlphaBeta.getBestMoveTimed(board, new MoveMasks(), 500);
        board.doMove(m);
        System.out.println(board.toFENString());
    }
    static void playAgainstItself(){
        Board board = new Board();
        MoveMasks masks = new MoveMasks();
        int counter = 0;

        GameState gameState;

        while(counter < 400){
            Move[] moves = MoveGenerator.generateLegalMoves(board, masks);
            Move m = AlphaBeta.getBestMove(board, moves, 5, masks);
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

    static void playTimed(String[] fens, int millis){
        MoveMasks m = new MoveMasks();
        for (int i = 0; i < fens.length; i++) {
            Board b = new Board(fens[i]);
            Move move = AlphaBeta.getBestMoveTimed(b, m, millis);
            System.out.println(move);
            System.out.println();
        }
    }

}