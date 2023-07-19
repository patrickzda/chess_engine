import engine.ai.Evaluation;
import engine.tools.EvaluationParams;
import engine.tools.MonteCarlo;
import engine.tools.OpeningBookReader;
import engine.tools.genetic_algorithm.Chromosome;
import engine.tools.genetic_algorithm.EvolutionData;
import engine.ai.AlphaBeta;
import engine.ai.Negamax;
import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.*;
import performance.*;
import test.AlphaBetaTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class Main {

    public static void main(String[] args) throws IOException {
        //playMove(args);
        //measureAverageTimeOnFENData(4);
        //performance();
        //Negamax negamax = new Negamax();
        //System.out.println(negamax.getBestMoveTimed(new Board(), 1000, new MoveMasks()));

        //AiArena arena = new AiArena(100, 150, 50);
        //arena.playAgainstBasicAlphaBeta();

        //monteCarloOpeningLibraryBuilder(1000, 10000, 5);
    }

    static void measureAverageDepthOnMidGameData(int timeInMilliseconds){
        Negamax negamax = new Negamax();
        String[] fens = new String[]{
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
            "rnbqkbnr/pp2pppp/2pp4/8/3PPP2/8/PPP3PP/RNBQKBNR b KQkq - 0 3",
            "rnbqkbnr/pp1ppppp/2p5/8/1P6/5N2/P1PPPPPP/RNBQKB1R b KQkq - 1 2",
            "rnbqkb1r/ppp1pppp/5n2/3p4/8/1P3N2/P1PPPPPP/RNBQKB1R w KQkq - 2 3",
            "rn1qkb1r/ppp2pp1/4pn1p/3p1b2/8/1P3NP1/PBPPPPBP/RN1QK2R w KQkq - 0 6",
            "8/1p5p/p2k2p1/2p1p3/r5b1/2PB2P1/2P2P2/2BR2K1 w - - 2 26",
            "r7/p1p3pk/P2p2q1/1p1P1p2/3QpP1p/2PbP2P/6P1/2R1R1K1 w - - 5 36",
            "1r2k2r/p3ppbp/B1p1b1p1/8/5B2/1P2P3/P4PPP/3R1RK1 b k - 4 14",
            "r2q1rk1/pp1n2pp/3bpnp1/3p4/3P1P2/1P1BP2P/P2N2P1/R1BQK2R b KQ - 0 12",
            "1r1q1rk1/Q4pbp/2p3p1/p2p4/8/1PnP2P1/P1P2PBP/2K1R2R w - - 0 21"
        };

        for(int i = 0; i < fens.length; i++){
            negamax.getBestMoveTimed(new Board(fens[i]), timeInMilliseconds, new MoveMasks());
        }
    }

    static void measureAverageDepthOnEndGameData(int timeInMilliseconds){
        Negamax negamax = new Negamax();
        String[] fens = new String[]{
            "8/8/8/1p5p/8/4k3/7r/3RK3 w - - 8 46",
            "8/8/8/2P4k/3pP3/1n6/Q6P/7K b - - 0 61",
            "3b4/8/5p2/2B5/2kP1N2/1p4Pp/1p3K1P/8 w - - 2 57",
            "8/8/8/8/8/5Pp1/5k2/7K w - - 1 63",
            "8/2r2pk1/6p1/7p/7P/4R1P1/5PK1/8 b - - 3 45",
            "8/8/8/3p4/2kP1K2/3b1Q2/5B2/8 b - - 0 84",
            "8/8/5bk1/5p1p/8/5K1P/3B4/8 b - - 21 75",
            "1R6/P7/6p1/kBp4p/2P2K1P/8/8/8 b - - 0 54",
            "1k6/7P/3r2B1/4r3/1p3K2/p4p2/R7/8 b - - 1 55",
            "6R1/8/8/8/3p4/2k5/r7/3K4 w - - 4 58"
        };

        for(int i = 0; i < fens.length; i++){
            negamax.getBestMoveTimed(new Board(fens[i]), timeInMilliseconds, new MoveMasks());
        }
    }

    static void monteCarloOpeningLibraryBuilder(int totalStates, int iterationsPerState, int movesPerIteration) throws IOException {
        OpeningBookReader openingBookReader = new OpeningBookReader("opening_book.txt", true);

        Queue<String> queue = new LinkedList<>();
        queue.add("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

        String FEN;
        Board b;
        MonteCarlo MC;
        LinkedList<Move> moves;

        for (int i = 0; i < totalStates; i++) {
            FEN = queue.remove();
            b = new Board(FEN);
            MC = new MonteCarlo(b);

            System.out.println(b.toFENString());
            moves = MC.getBestMoves(iterationsPerState, movesPerIteration);
            b = new Board(FEN);
            System.out.println(b.toFENString());
            System.out.println();

            openingBookReader.writeBestMove(b, moves.getFirst());

            for (Move move: moves) {
                b.doMove(move);
                queue.add(b.toFENString());
                b.undoLastMove();
            }
        }

    }

    static void measureAverageTimeOnFENData(int depth){
        String[] fens = new String[]{"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", "2k5/6q1/3P1P2/4N3/8/1K6/8/8 w - - 0 1", "4r1k1/1bqr1pbp/p2p2p1/4p1B1/2p1P3/PnP2N1P/BP2QPP1/3RR1K1 w Qq - 0 1","6k1/r4ppp/r7/1b6/8/8/4QPPP/4R1K1 w - - 0 1", "r2qk2r/p1p1p1P1/1pn4b/1N1Pb3/1PB1N1nP/8/1B1PQPp1/R3K2R b Qkq - 0 1", "r1bq4/pp1p1k1p/2p2p1p/2b5/3Nr1Q1/2N1P3/PPPK1PPP/3R1B1R w - - 0 1", "3r1rk1/p1p1qp1p/1p2b1p1/6n1/R1PNp3/2QP2P1/3B1P1P/5RK1 w - - 0 1", "3r4/7p/2p2kp1/2P2p2/3P4/2K3P1/8/5R2 b - - 0 1", "5rk1/1p4pp/2R1p3/p5Q1/P4P2/6qr/2n3PP/5RK1 w - - 0 1", "r1b2rk1/4qpp1/4p2R/p2pP3/2pP2QP/4P1P1/PqB4K/8 w - - 0 1"};
        MoveMasks masks = new MoveMasks();
        Negamax negamax = new Negamax();

        long totalTime = 0L;
        AlphaBeta alphaBeta = new AlphaBeta();
        for(int i = 0; i < fens.length; i++){
            Board b = new Board(fens[i]);
            Move[] moves = MoveGenerator.generateLegalMoves(b, masks);
            long startTime = System.nanoTime();
            negamax.getBestMove(b, depth, new MoveMasks());
            totalTime = totalTime + (System.nanoTime() - startTime);
            negamax.clearTable();
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
        Negamax negamax = new Negamax();

        for(int i = 0; i < fens.length; i++){
            negamax.clearTable();
            Board b = new Board(fens[i]);
            Move[] moves = MoveGenerator.generateLegalMoves(b, new MoveMasks());
            AlphaBeta alphaBeta = new AlphaBeta();
            int moveCount = 0;
            while(!b.isGameLost(new MoveMasks(), moves.length) && moveCount < 200){
                Move m;
                if(b.getTurn() == Color.WHITE){
                    m = negamax.getBestMove(b, depth, new MoveMasks());
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
            negamax.clearTable();
            Board b = new Board(fens[i]);
            Move[] moves = MoveGenerator.generateLegalMoves(b, new MoveMasks());
            AlphaBeta alphaBeta = new AlphaBeta();
            int moveCount = 0;
            while(!b.isGameLost(new MoveMasks(), moves.length) && moveCount < 200){
                Move m;
                if(b.getTurn() == Color.BLACK){
                    m = negamax.getBestMove(b, depth, new MoveMasks());
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
        Negamax negamax = new Negamax();
        MoveMasks masks = new MoveMasks();
        Board board = new Board(args[0]);
        //Move m = alphaBeta.getBestMoveTimed(board, new MoveMasks(), 2000);
        //Move m = Negamax.getBestMove(board, Integer.parseInt(args[1]), masks);
        Move m;
        if(board.getTurn() == Color.BLACK){
            //m = Negamax.getBestMove(board, Integer.parseInt(args[1]), masks);
            m = negamax.getBestMoveTimed(board, 5000, masks);
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
        Negamax negamax = new Negamax();
        for (int i = 0; i < fens.length; i++) {
            Board b = new Board(fens[i]);
            Move move = negamax.getBestMoveTimed(b, millis, m);
            System.out.println(move);
        }
    }

}