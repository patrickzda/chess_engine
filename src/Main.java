import engine.ai.AlphaBeta;
import engine.ai.NegaScout;
import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.*;
import performance.*;
import test.AlphaBetaTest;

public class Main {
    public static void main(String[] args) {
        //playMove(args);
        measureAverageTimeOnFENData(4);
    }

    static void measureAverageTimeOnFENData(int depth){
        String[] fens = new String[]{"2k5/6q1/3P1P2/4N3/8/1K6/8/8 w - - 0 1" ,"4r1k1/1bqr1pbp/p2p2p1/4p1B1/2p1P3/PnP2N1P/BP2QPP1/3RR1K1 w Qq - 0 1","6k1/r4ppp/r7/1b6/8/8/4QPPP/4R1K1 w - - 0 1", "r2qk2r/p1p1p1P1/1pn4b/1N1Pb3/1PB1N1nP/8/1B1PQPp1/R3K2R b Qkq - 0 1", "r1bq4/pp1p1k1p/2p2p1p/2b5/3Nr1Q1/2N1P3/PPPK1PPP/3R1B1R w - - 0 1", "3r1rk1/p1p1qp1p/1p2b1p1/6n1/R1PNp3/2QP2P1/3B1P1P/5RK1 w - - 0 1", "3r4/7p/2p2kp1/2P2p2/3P4/2K3P1/8/5R2 b - - 0 1"};
        MoveMasks masks = new MoveMasks();
        long totalTime = 0L;

        for(int i = 0; i < fens.length; i++){
            Board b = new Board(fens[i]);
            Move[] moves = MoveGenerator.generateLegalMoves(b, masks);
            long startTime = System.nanoTime();
            System.out.println(AlphaBeta.getBestMove(b, moves, depth, masks));
            totalTime = totalTime + (System.nanoTime() - startTime);
        }

        System.out.println("///");

        long totalBasicTime = 0L;
        for(int i = 0; i < fens.length; i++){
            Board b = new Board(fens[i]);
            Move[] moves = MoveGenerator.generateLegalMoves(b, masks);
            long startTime = System.nanoTime();
            System.out.println(AlphaBetaTest.bestMove(b, moves, depth, masks));
            totalBasicTime = totalBasicTime + (System.nanoTime() - startTime);
        }

        System.out.println("Aktuelle Implementation: " + (totalTime / fens.length) / 1000000);
        System.out.println("Einfaches AlphaBeta: " + (totalBasicTime / fens.length) / 1000000);

        System.out.println("Die aktuelle Implementation ist ca. " + ((double) totalBasicTime / (double) totalTime) + " Mal schneller");
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