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
import engine.tools.genetic_algorithm.GeneticAlgorithm;
import engine.tools.genetic_algorithm.GeneticNegamax;
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

        AiArena arena = new AiArena(100, 150, 50);
        arena.playAgainstBasicAlphaBeta(new Chromosome(new EvolutionData(), new Integer[]{126, 742, 473, 1481, 4463, 79, 854, 296, 1709, 1500, 0, 0, 0, 0, 0, 0, 0, 0, 2, 3, 12, 18, 3, 21, 11, -4, -14, -12, 11, 15, 19, 25, 4, -9, -3, -23, 13, 5, 68, 5, 2, -5, 9, -5, -12, 2, 12, 0, -14, 2, 3, -14, -5, 13, -5, -5, -17, -14, -3, 6, -2, -17, 4, -7, 6, -3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -9, -3, 13, 5, 46, 4, -6, -45, -13, -6, -10, 6, 1, 3, -11, -12, 9, 1, -6, -1, -11, -20, -6, -4, 11, 6, 2, -3, -5, -4, 14, 7, 17, 5, 23, 25, 18, 11, 12, 10, -1, -11, 16, 19, 22, 8, 5, 4, 0, 0, 0, 0, 0, 0, 0, 0, -156, -91, -25, -36, -38, -141, -92, -227, -72, -31, -59, -8, -10, -9, -41, -21, -95, -12, 7, 22, 13, 3, -17, -133, -25, 9, 40, 12, 38, 28, 10, -9, -44, 18, 45, 9, 39, 144, 10, -44, -9, 11, 58, 39, 51, 75, 22, -9, -15, -36, 2, 7, 48, 4, -19, -129, -172, -33, -79, -21, -19, -106, -99, -297, -81, -65, -26, -27, -14, -35, -107, -96, -92, -70, -6, 8, 6, -12, -82, -36, -9, -35, -8, 27, 29, -3, -19, -31, -13, -2, 16, 20, 98, 15, -2, -15, -49, -31, 7, 26, 28, 18, -10, -30, -66, -70, -17, 12, 12, -28, -45, -24, -105, -27, -42, 12, 7, -25, -72, -173, -98, -81, -58, -27, -21, -32, -50, -214, -16, -7, -4, -16, -17, -12, -3, -21, -10, 6, 13, 4, 3, 12, 7, -11, -5, 6, -3, 5, 15, -3, 10, -4, -2, 4, 23, 21, 21, 18, 10, -4, -4, 34, 7, 12, 17, 16, 14, -10, -14, 2, 1, 5, 3, 1, 2, -16, -30, -3, 3, 0, 0, 2, -10, -3, -18, 1, -15, -16, -8, -5, 1, -37, -28, -26, -26, -8, -12, -45, -4, -63, -26, -12, -16, 1, 1, -5, -5, -26, -8, -1, -1, 9, 11, -1, -1, -11, -3, -3, 0, 14, 7, 0, -3, -15, -22, -1, -5, 6, 8, -14, -1, -8, -23, 4, 5, 4, 5, 1, 3, -17, -29, -6, -1, 1, 1, -1, -14, -9, -13, -47, -34, -11, -16, -26, -37, -47, -22, -19, -14, -5, -4, -10, -13, -34, -9, -14, -10, 7, 11, -7, -13, -15, -25, -9, -1, 2, 3, -1, -31, -32, -11, -4, -3, -5, -6, -2, -5, -14, -62, -13, -5, 4, 2, -3, -5, -16, -17, -2, 6, 17, 8, 5, -2, -40, -2, 12, 23, 25, 9, 12, 12, -2, -23, -16, -1, 12, 13, -1, -17, -6, -9, -12, -12, -2, -2, -14, -17, -7, -7, -5, -1, -2, -3, -1, -4, -15, 5, -3, -2, -7, -3, -2, -15, 3, -4, 1, -6, 5, 11, -13, 1, -6, -5, 7, 11, -4, -7, 9, 8, -4, 6, 1, -6, 7, 23, -7, 1, 4, 4, 5, 18, -5, -7, 29, 3, 4, 19, 0, 19, 6, 11, 16, 0, 26, 1, -3, -2, 2, 5, -7, -6, 3, -4, 3, 9, 7, 9, 8, 4, -2, -2, 8, 11, 10, 2, 7, 4, -3, 2, 4, 5, 3, 8, 5, 3, 5, 0, 17, 12, 4, 3, 24, 8, 0, -4, 5, 6, 6, 10, 4, 9, -4, -5, 7, 9, 5, 8, 12, 5, -4, -2, -2, 1, -3, -2, 1, -2, -2, -42, -14, -47, -26, -19, -30, -54, -23, -38, -36, -12, -9, -5, -41, -61, -54, -57, -37, -21, 2, 4, -10, -18, -14, -17, -3, 14, 31, 14, 9, -3, -11, -21, -6, 10, 30, 24, 7, -11, -43, -38, -31, -9, 1, 1, -12, -20, -27, -32, -14, -45, -4, -6, -42, -27, -58, -32, -34, -21, -49, -44, -92, -17, -10, 190, 781, 364, 133, 245, 190, 308, 286, 277, 126, 201, 126, 126, 304, 290, 180, 135, 255, 169, 84, 173, 180, 133, 195, 68, 142, 142, 63383, 48902, 194, 104, 199, 472, 138, 74, 75261, 218616, 77, 190, 109, 137, 176, 202, 57, 57, 73, 212, 170, 73, 227, 4, 46, 57, 33, 84, 87, 34, 89, 66, -1, -1, 13, 68, 56, 1, 46, 60, 28, 33, 60, 61, 1, 68, 226, 72, 211, 135, 132, 70, 99, 137, 38, 569, 898, 575, 1516, 122, 79, 146, 64, 401, 18700, 163568, 743, 150, 59, 124, 191, 681, 101346, 99715, 464, 117, 111, 119, 148, 567, 1149, 604, 186, 291, 45, 55, 149, 341, 572, 598, 430, 87, 27, 3, 87, 94, 82, 42, 94, 59, 4, -150, -31, -8, -3, 3, 9, 25, 43, 16, -148, -197, -14, -12, 7, 4, 24, 20, 33, -44, -16, 11, 36, 45, 53, 53, 65, 83, 69, 86, 50, 48, 212, -76, -24, -5, 9, 9, 19, 66, 17, 43, 27, 78, 61, 82, 98, -76, -31, 0, 4, 5, 6, 26, 54, 51, 44, 14, 43, 21, 42, 41, -232, -26, 15, 72, 173, 90, 99, 117, 79, 59, 111, 124, 47, 139, 88, -75, -16, -12, -4, 13, 25, 29, 31, 41, 89, 109, 88, 48, 91, 70, 70, 118, 73, 61, 186, 90, 212, 132, 139, 78, 353, 175, 37, -69, -37, -9, 6, 34, 54, 38, 48, 64, 210, 28, 85, 197, 135, 151, 88, 84, 84, 68, 302, 185, 165, 171, 140, 26, 45, 869, 210, -1, -14, -7, -33, -4, -1, 28, 39, 55, 34, 30, 386, 6, 31, 1, -107, 6, 3, 7, 3, 155, 17, 5, 70, 78, 62, 81, 28, 61, 38, 139, 211, 3, 36, 11, 0, 75, 75, 9, 45, 18, 19}));
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