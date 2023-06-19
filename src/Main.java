import engine.ai.AlphaBeta;
import engine.ai.Evaluation;
import engine.ai.Negamax;
import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.*;
import engine.tools.EvaluationParams;
import performance.*;
import test.AlphaBetaTest;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        //playMove(args);
        //measureAverageTimeOnFENData(4);
        performance();

        //WICHTIGER TEST
        //Board b = new Board("4r1k1/1bqr1pbp/p2p2p1/4p1B1/2p1P3/PnP2N1P/BP2QPP1/3RR1K1 w Qq - 0 1");
        //long nanoStart = System.nanoTime();
        //System.out.println(Negamax.getBestMove(b, 5, new MoveMasks()));
        //long nanoEnd = System.nanoTime();
        //long nanoElapsed = nanoEnd-nanoStart;
        //System.out.println(nanoElapsed/1000000d);

    }

    static void measureAverageTimeOnFENData(int depth){
        String[] fens = new String[]{"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", "2k5/6q1/3P1P2/4N3/8/1K6/8/8 w - - 0 1", "4r1k1/1bqr1pbp/p2p2p1/4p1B1/2p1P3/PnP2N1P/BP2QPP1/3RR1K1 w Qq - 0 1","6k1/r4ppp/r7/1b6/8/8/4QPPP/4R1K1 w - - 0 1", "r2qk2r/p1p1p1P1/1pn4b/1N1Pb3/1PB1N1nP/8/1B1PQPp1/R3K2R b Qkq - 0 1", "r1bq4/pp1p1k1p/2p2p1p/2b5/3Nr1Q1/2N1P3/PPPK1PPP/3R1B1R w - - 0 1", "3r1rk1/p1p1qp1p/1p2b1p1/6n1/R1PNp3/2QP2P1/3B1P1P/5RK1 w - - 0 1", "3r4/7p/2p2kp1/2P2p2/3P4/2K3P1/8/5R2 b - - 0 1", "5rk1/1p4pp/2R1p3/p5Q1/P4P2/6qr/2n3PP/5RK1 w - - 0 1", "r1b2rk1/4qpp1/4p2R/p2pP3/2pP2QP/4P1P1/PqB4K/8 w - - 0 1"};
        MoveMasks masks = new MoveMasks();

        long totalTime = 0L;
        AlphaBeta alphaBeta = new AlphaBeta();
        for(int i = 0; i < fens.length; i++){
            Board b = new Board(fens[i]);
            Move[] moves = MoveGenerator.generateLegalMoves(b, masks);
            long startTime = System.nanoTime();
            Negamax.getBestMove(b, depth, new MoveMasks());
            totalTime = totalTime + (System.nanoTime() - startTime);
            alphaBeta.clearTable();
        }

        long totalBasicTime = 0L;
        for(int i = 0; i < fens.length; i++){
            Board b = new Board(fens[i]);
            Move[] moves = MoveGenerator.generateLegalMoves(b, masks);
            long startTime = System.nanoTime();
            AlphaBetaTest.bestMove(b, moves, depth, masks);
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
        //EvaluationPeformance evaluationPeformance = new EvaluationPeformance();
        //evaluationPeformance.measureAveragePerformanceOfEvaluation(fens,10000);
        //AlphaBetaPerfomance.measureAveragePerformanceOfAlphaBeta(fens,1,5);
        //MiniMaxPerformance.measureAveragePerformanceOfMiniMax(fens,1,4);
        //AiPerformance.howMuchElohasMyAI(1320,5,1000);
        //AiPerformance.calcElo(1000,2000);
        NegamaxPerformance.measureAveragePerformanceOfNegamax(fens,1,5);
    }

    static void aiArena(int depth){
        String[] fens = new String[]{"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", "r1bqkb1r/pppp1ppp/2n2n2/4p3/4P3/2N2N2/PPPP1PPP/R1BQKB1R w KQkq - 0 1", "rnbqkb1r/pp2pppp/5n2/3p4/2PP4/2N5/PP3PPP/R1BQKBNR b KQkq - 0 1", "rnbqkbnr/ppp2ppp/4p3/3pP3/3P4/8/PPP2PPP/RNBQKBNR b KQkq - 0 1", "r1bq1rk1/2p1bppp/p1n2n2/1p1pp3/4P3/1BP2N2/PP1P1PPP/RNBQR1K1 w - - 0 1"};
        int winCountAdvancedAI = 0, winCountClassicAlphaBeta = 0;

        for(int i = 0; i < fens.length; i++){
            Negamax.clearTable();
            Board b = new Board(fens[i]);
            Move[] moves = MoveGenerator.generateLegalMoves(b, new MoveMasks());
            AlphaBeta alphaBeta = new AlphaBeta();
            int moveCount = 0;
            while(!b.isGameLost(new MoveMasks(), moves.length) && moveCount < 200){
                Move m;
                if(b.getTurn() == Color.WHITE){
                    m = Negamax.getBestMove(b, depth, new MoveMasks());
                }else{
                    m = AlphaBetaTest.bestMove(b, MoveGenerator.generateLegalMoves(b, new MoveMasks()), depth, new MoveMasks());
                }
                b.doMove(m);
                moveCount++;
                System.out.println(b.toFENString());
                moves = MoveGenerator.generateLegalMoves(b, new MoveMasks());
            }
            if(moveCount == 200){
                System.out.println("Unentschieden");
                System.out.println(b.toFENString());
            }else if(b.getTurn() == Color.BLACK){
                winCountAdvancedAI++;
                System.out.println("Die neue Implementation hat gewonnen!");
            }else{
                winCountClassicAlphaBeta++;
                System.out.println("Das klassische AlphaBeta hat gewonnen!");
            }
        }

        System.out.println("FARBENTAUSCH");

        for(int i = 0; i < fens.length; i++){
            Negamax.clearTable();
            Board b = new Board(fens[i]);
            Move[] moves = MoveGenerator.generateLegalMoves(b, new MoveMasks());
            AlphaBeta alphaBeta = new AlphaBeta();
            int moveCount = 0;
            while(!b.isGameLost(new MoveMasks(), moves.length) && moveCount < 200){
                Move m;
                if(b.getTurn() == Color.BLACK){
                    m = Negamax.getBestMove(b, depth, new MoveMasks());
                }else{
                    m = AlphaBetaTest.bestMove(b, MoveGenerator.generateLegalMoves(b, new MoveMasks()), depth, new MoveMasks());
                }
                b.doMove(m);
                moveCount++;
                System.out.println(b.toFENString());
                moves = MoveGenerator.generateLegalMoves(b, new MoveMasks());
            }
            if(moveCount == 200){
                System.out.println("Unentschieden");
                System.out.println(b.toFENString());
            }else if(b.getTurn() == Color.WHITE){
                winCountAdvancedAI++;
                System.out.println("Die neue Implementation hat gewonnen!");
            }else{
                winCountClassicAlphaBeta++;
                System.out.println("Das klassische AlphaBeta hat gewonnen!");
            }
        }

        System.out.println("Die neue KI hat " + winCountAdvancedAI + " Mal gewonnen");
        System.out.println("Die standard KI hat " + winCountClassicAlphaBeta + " Mal gewonnen");
    }

    static void playMove(String[] args){
        AlphaBeta alphaBeta = new AlphaBeta();
        MoveMasks masks = new MoveMasks();
        Board board = new Board(args[0]);
        //Move m = alphaBeta.getBestMoveTimed(board, new MoveMasks(), 2000);
        //Move m = Negamax.getBestMove(board, Integer.parseInt(args[1]), masks);
        Move m;
        if(board.getTurn() == Color.BLACK){
            //m = Negamax.getBestMove(board, Integer.parseInt(args[1]), masks);
            m = Negamax.getBestMoveTimed(board, 5000, masks);
        }else{
            //m = AlphaBetaTest.bestMove(board, MoveGenerator.generateLegalMoves(board, masks), Integer.parseInt(args[1]), masks);
            m = AlphaBetaTest.getBestMoveTimed(board, 5000, masks);
        }
        board.doMove(m);
        System.out.println(board.toFENString());
    }

    static void playAgainstItself(){
        Board board = new Board();
        MoveMasks masks = new MoveMasks();
        int counter = 0;

        GameState gameState;

        while(counter < 400){
            AlphaBeta alphaBeta = new AlphaBeta();
            Move[] moves = MoveGenerator.generateLegalMoves(board, masks);
            Move m = alphaBeta.getBestMove(board, moves, 5, Integer.MIN_VALUE, Integer.MAX_VALUE, masks);
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
        AlphaBeta alphaBeta = new AlphaBeta();
        for (int i = 0; i < fens.length; i++) {
            Board b = new Board(fens[i]);
            Move move = alphaBeta.getBestMoveTimed(b, m, millis);
            System.out.println(move);
            System.out.println();
        }
    }

}