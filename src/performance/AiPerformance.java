package performance;

import engine.ai.AlphaBeta;
import engine.ai.Negamax;
import engine.move_generation.MoveMasks;
import engine.representation.Board;
import engine.representation.GameState;
import engine.representation.Move;
import engine.representation.PieceType;

import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class AiPerformance {

    //Damit diese Methode funktioniert muss folgendes gewährleistet sein: Im Ordner, in dem sich das Projekt chess_engine befindet, muss die Stockfish-Binary liegen.
    //Gibt für eine bestimmte Elo-Spielstärke (min 1320, max 3190), einen bestimmten FEN-String und eine Rechenzeitangabe einen Zug zurück.
    public static void howMuchElohasMyAI(int startelo,int eloCalctime){
        boolean kiWon = false;
        int kiWoncounter = 0;

        if(startelo < 1320 || startelo > 3190){
            System.out.println("Elo has to be in the bound of 1320 and 3190\n Elo Set to 1320");
            startelo = 1320;
        }
        int lowElo = startelo;
        int highElo = 3100;
        int middleElo = (lowElo+highElo)/2;
        boolean lowerEnd = playAgainstStockfish(lowElo,eloCalctime);
        boolean uperEnd = playAgainstStockfish(highElo,eloCalctime);
        boolean middle = playAgainstStockfish(middleElo,eloCalctime);
        if(uperEnd){
            System.out.println("already better than 3190!");
            //return;
        }
        if (lowerEnd){
            System.out.println("lost against the smallest elo of Stockfish your Ai is Trash");
            //return;
        }
        int eloStagnation = middleElo+1;
        while (eloStagnation != middleElo){
            if (middle){ // uper end
                lowElo = middleElo;
                middleElo = (lowElo+highElo)/2;
            }else { // lower end
                highElo = middleElo;
                middleElo = (lowElo+highElo)/2;
            }
            eloStagnation = middleElo;
            middle = playAgainstStockfish(middleElo,eloCalctime);

            System.out.println(middleElo);
        }
    }
    public static void calcElo(int startelo,int calculationTime){
        if(startelo < 500 || startelo > 2850){
            System.out.println("Elo has to be in the bound of 500 and 2850\n Elo Set to 1320");
            startelo = 1320;
        }
        boolean winning = true;
        while (winning){
            winning = playAgainstFairyStockfish(startelo,calculationTime);
            startelo = startelo +100;
        }
        winning = true;
        startelo = startelo - 200;
        while (winning){
            winning = playAgainstFairyStockfish(startelo,calculationTime);
            startelo = startelo +10;
        }
        winning = true;
        startelo = startelo - 20;
        while (winning){
            winning = playAgainstFairyStockfish(startelo,calculationTime);
            startelo = startelo +1;
        }

        System.out.println("AI elo: "+startelo);
    }
    public static boolean playAgainstFairyStockfish(int elo,int calcTimeMilli){
        Board board = new Board();
        MoveMasks masks = new MoveMasks();
        GameState gameState;
        AlphaBeta alphaBeta = new AlphaBeta();
        Negamax negamax = new Negamax();
        System.out.println(board.toFENString());
        while (true){

            Move m = negamax.getBestMoveTimed(board,calcTimeMilli,masks); // alphaBeta.getBestMoveTimed(board,masks,calcTimeMilli);
            board.doMove(m);
            System.out.println(board.toFENString());
            gameState = board.getGameState(masks);
            if (gameState == GameState.DRAW || gameState == GameState.WHITE_WON) {
                System.out.println("The Ai won with elocount:"+elo);
                return true;
            }
            Move fairyMove = fairyMove(getMoveFromFairy(elo, board.toFENString(), calcTimeMilli),board);
            board.doMove(fairyMove);
            System.out.println(board.toFENString());
            gameState = board.getGameState(masks);
            if (gameState == GameState.DRAW || gameState == GameState.BLACK_WON) {
                System.out.println("Stockfish won with elocount:"+elo);
                return false;
            }
        }
    }
    public static boolean playAgainstStockfish(int elo,int calcTimeMilli){
        Board board = new Board();
        MoveMasks masks = new MoveMasks();
        GameState gameState;
        AlphaBeta alphaBeta = new AlphaBeta();
        while (true){
            Move m = alphaBeta.getBestMoveTimed(board,masks,calcTimeMilli);
            board.doMove(m);
            gameState = board.getGameState(masks);
            if (gameState == GameState.DRAW || gameState == GameState.WHITE_WON) {
                System.out.println("The Ai won with elocount:"+elo);
                return true;
            }
            Move stockFishMove = stockfishMove(getMoveFromStockfish(elo, board.toFENString(), calcTimeMilli),board);
            board.doMove(stockFishMove);
            gameState = board.getGameState(masks);
            if (gameState == GameState.DRAW || gameState == GameState.BLACK_WON) {
                System.out.println("Stockfish won with elocount:"+elo);
                return false;
            }
        }
    }
    public static Move stockfishMove(String move,Board board){
           String []fields = {"a1","b1","c1","d1","e1","f1","g1","h1",
                              "a2","b2","c2","d2","e2","f2","g2","h2",
                              "a3","b3","c3","d3","e3","f3","g3","h3",
                              "a4","b4","c4","d4","e4","f4","g4","h4",
                              "a5","b5","c5","d5","e5","f5","g5","h5",
                              "a6","b6","c6","d6","e6","f6","g6","h6",
                              "a7","b7","c7","d7","e7","f7","g7","h7",
                              "a8","b8","c8","d8","e8","f8","g8","h8",};

           int startPos = Arrays.asList(fields).indexOf(move.substring(0,2));
           int endPos = Arrays.asList(fields).indexOf(move.substring(2));
           String aktuellesB = board.toString();
           PieceType pieceType = getPieceTyp(reoderString(aktuellesB).charAt(startPos));

           Move move1 = new Move(startPos,endPos,pieceType);
           return move1;
    }
    private static String reoderString(String b){
        String res = "";
        for (int i = b.length()-2; i > 0; i--) {
            if(b.charAt(i) == '\n'){
                for (int j = i+1; j < i+9; j++) {
                    res = res + b.charAt(j);
                }
            }
        }
        for (int i = 0; i < 8; i++) {
            res = res + b.charAt(i);
        }
        return res;
    }
    private static PieceType getPieceTyp(char figur){
        if (figur == 'p' || figur == 'P'){
            return PieceType.PAWN;
        } else if (figur == 'r' || figur == 'R') {
            return PieceType.ROOK;
        } else if (figur == 'q' || figur == 'Q') {
            return PieceType.QUEEN;
        } else if (figur == 'b' || figur == 'B') {
            return PieceType.BISHOP;
        } else if (figur == 'n' || figur == 'N') {
            return PieceType.KNIGHT;
        } else if (figur == 'k' || figur == 'K') {
            return PieceType.KING;
        }
        return null;
    }
    public static Move fairyMove(String move,Board board){
        String []fields = {
                "a1","b1","c1","d1","e1","f1","g1","h1",
                "a2","b2","c2","d2","e2","f2","g2","h2",
                "a3","b3","c3","d3","e3","f3","g3","h3",
                "a4","b4","c4","d4","e4","f4","g4","h4",
                "a5","b5","c5","d5","e5","f5","g5","h5",
                "a6","b6","c6","d6","e6","f6","g6","h6",
                "a7","b7","c7","d7","e7","f7","g7","h7",
                "a8","b8","c8","d8","e8","f8","g8","h8"};

        int startPos = Arrays.asList(fields).indexOf(move.substring(0,2));
        int endPos = Arrays.asList(fields).indexOf(move.substring(2));
        String aktuellesB = board.toString();
        if (reoderString(aktuellesB).length() < 64 || startPos < 0
        ){

            return new Move(startPos,startPos,PieceType.KING);
        }
        try{
        PieceType pieceType = getPieceTyp(reoderString(aktuellesB).charAt(startPos));

        return new Move(startPos,endPos,pieceType);
        } catch (Exception e) {
            return new Move(startPos,startPos,PieceType.KING);
        }
    }
    public static String getMoveFromStockfish(int elo, String fen, int timeInMillis){
        try {
            Process engine = Runtime.getRuntime().exec("../stockfish");
            BufferedReader reader = new BufferedReader(new InputStreamReader(engine.getInputStream()));
            OutputStreamWriter writer = new OutputStreamWriter(engine.getOutputStream());

            writer.write("setoption name UCI_LimitStrength value true\n");
            writer.flush();
            writer.write("setoption name UCI_Elo value " + elo + "\n");
            writer.flush();

            writer.write("position fen " + fen + "\n");
            writer.flush();
            writer.write("go movetime " + timeInMillis + "\n");
            writer.flush();

            Thread.sleep(timeInMillis + 25);
            String currentLine = reader.readLine();
            while (!currentLine.contains("bestmove")) {
                currentLine = reader.readLine();
            }
            return currentLine.split(" ")[1];
        }catch (IOException | InterruptedException e){
            System.out.println(e.getMessage());
            return "";
        }
    }

    public static String getMoveFromFairy(int elo, String fen, int timeInMillis){
        try {
            Process engine = Runtime.getRuntime().exec("../fairystockfish");
            BufferedReader reader = new BufferedReader(new InputStreamReader(engine.getInputStream()));
            OutputStreamWriter writer = new OutputStreamWriter(engine.getOutputStream());
            writer.write("setoption name UCI_Variant value kingofthehill\n");
            writer.flush();
            writer.write("setoption name UCI_LimitStrength value true\n");
            writer.flush();
            writer.write("setoption name UCI_Elo value " + elo + "\n");
            writer.flush();

            writer.write("position fen " + fen + "\n");
            writer.flush();
            writer.write("go movetime " + timeInMillis + "\n");
            writer.flush();

            Thread.sleep(timeInMillis + 25);
            String currentLine = reader.readLine();
            while (!currentLine.contains("bestmove")) {
                //System.out.println(currentLine);
                currentLine = reader.readLine();
            }
            engine.destroy();
            return currentLine.split(" ")[1];
        }catch (IOException | InterruptedException e){
            System.out.println(e.getMessage());
            return "";
        }
    }

    public static String getEvaluateFromFairy(int elo, String fen, int timeInMillis){
        try {
            Process engine = Runtime.getRuntime().exec("../fairystockfish");
            BufferedReader reader = new BufferedReader(new InputStreamReader(engine.getInputStream()));
            OutputStreamWriter writer = new OutputStreamWriter(engine.getOutputStream());
            writer.write("setoption name UCI_Variant value kingofthehill\n");
            writer.flush();
            writer.write("setoption name UCI_LimitStrength value true\n");
            writer.flush();
            writer.write("setoption name UCI_Elo value " + elo + "\n");
            writer.flush();

            writer.write("position fen " + fen + "\n");
            writer.flush();
            writer.write("eval"+"\n");
            writer.flush();

            Thread.sleep( 1000);
            String currentLine = reader.readLine();
            while (!currentLine.contains("Final evaluation")) {
                //System.out.println(currentLine);
                currentLine = reader.readLine();
            }
            engine.destroy();
            if(currentLine.split(" ").length < 8){
                return "10000.999";
            }

            return currentLine.split(" ")[8];
        }catch (IOException | InterruptedException e){
            System.out.println(e.getMessage());
            return "";
        }
    }


    public static void readGamesFromFile() throws IOException {
        Random r = new Random();
        File path = new File("C:\\Users\\adamg\\Desktop\\chess_engine\\fens.java");
        File pathmove = new File("C:\\Users\\adamg\\Desktop\\chess_engine\\ove.java");
        File patheval = new File("C:\\Users\\adamg\\Desktop\\chess_engine\\eval.java");
        File file = new File("C:\\Users\\adamg\\PycharmProjects\\pythonProject\\fen_games.txt");
        FileWriter wr = new FileWriter(path);
        FileWriter wrmove = new FileWriter(pathmove);
        FileWriter wreval = new FileWriter(patheval);
        wr.write("private final String[] FEN_DATA = new String[]{\n");
        wrmove.write("import engine.representation.Move;\n" +
                "import engine.representation.PieceType;\n" +
                "\n" +
                "class test1 {\n\tprivate final Move[] MOVE = new Move[]{\n");
        wreval.write("private final double[] EVAL = new double[]{\n");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int x = r.nextInt(10,20);
            int counter = 0;
            int i = 0;
            while ((line = br.readLine()) != null) {
                if(line.contains("Game")){
                    counter = 0;
                    i++;
                    System.out.println("game"+i);
                    x = r.nextInt(10,20);
                }
                counter++;
                if (counter == x){
                    String eval = getEvaluateFromFairy(3500,line,10000);
                    double evaluate = Double.parseDouble(eval);
                    String back = getMoveFromFairy(3500,line,10000);
                    Board board = new Board(line);
                    Move move = fairyMove(back,board);
                    //System.out.println(move.getPieceType());
                    wr.write("\""+line+"\",\n");
                    wrmove.write("new Move(" + move.getStartFieldIndex() +","+ move.getEndFieldIndex() +", PieceType."+move.getPieceType()+"),\n" );
                    wreval.write(evaluate+",\n");
                    wr.flush();
                    wrmove.flush();
                    wreval.flush();
                }
            }
            wr.write("\t}\n");
            wrmove.write("\t};\n}");
            wreval.write("\t}\n");
            wr.close();
            wrmove.close();
            wreval.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}



/*
package performance;

import engine.ai.AlphaBeta;
import engine.move_generation.MoveMasks;
import engine.representation.Board;
import engine.representation.GameState;
import engine.representation.Move;
import engine.representation.PieceType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;

public class AiPerformance {

    //Damit diese Methode funktioniert muss folgendes gewährleistet sein: Im Ordner, in dem sich das Projekt chess_engine befindet, muss die Stockfish-Binary liegen.
    //Gibt für eine bestimmte Elo-Spielstärke (min 1320, max 3190), einen bestimmten FEN-String und eine Rechenzeitangabe einen Zug zurück.
    public static void calcElo(int startelo,int calculationTime){
        boolean lowerEnd = playAgainstFairyStockfish(500,calculationTime);
        if(!lowerEnd){
            System.out.println("lost against 500 elo");
            return;
        }
        if(startelo < 500 || startelo > 2850){
            System.out.println("Elo has to be in the bound of 500 and 2850\n Elo Set to 1320");
            startelo = 1320;
        }
        while (lowerEnd){
            lowerEnd = playAgainstFairyStockfish(startelo,calculationTime);
            startelo = startelo +10;
        }
    }
    public static void howMuchElohasMyAI(int startelo,int eloCalctime){
        boolean kiWon = false;
        int kiWoncounter = 0;

        if(startelo < 1320 || startelo > 3190){
            System.out.println("Elo has to be in the bound of 1320 and 3190\n Elo Set to 1320");
            startelo = 1320;
        }
        int lowElo = startelo;
        int highElo = 3100;
        int middleElo = (lowElo+highElo)/2;
        boolean lowerEnd = playAgainstStockfish(lowElo,eloCalctime);
        boolean uperEnd = playAgainstStockfish(highElo,eloCalctime);
        boolean middle = playAgainstStockfish(middleElo,eloCalctime);
        if(uperEnd){
            System.out.println("already better than 3190!");
            //return;
        }
        if (lowerEnd){
            System.out.println("lost against the smallest elo of Stockfish your Ai is Trash");
            //return;
        }
        int eloStagnation = middleElo+1;
        while (eloStagnation != middleElo){
            if (middle){ // uper end
                lowElo = middleElo;
                middleElo = (lowElo+highElo)/2;
            }else { // lower end
                highElo = middleElo;
                middleElo = (lowElo+highElo)/2;
            }
            eloStagnation = middleElo;
            middle = playAgainstStockfish(middleElo,eloCalctime);

            System.out.println(middleElo);
        }
    }
    public static boolean playAgainstStockfish(int elo,int calcTimeMilli){
        Board board = new Board();
        MoveMasks masks = new MoveMasks();
        GameState gameState;
        while (true){
            Move m = AlphaBeta.getBestMoveTimed(board,masks,calcTimeMilli);
            board.doMove(m);
            gameState = board.getGameState(masks);
            if (gameState == GameState.DRAW || gameState == GameState.WHITE_WON) {
                System.out.println("The Ai won with elocount:"+elo);
                return true;
            }
            Move stockFishMove = stockfishMove(getMoveFromStockfish(elo, board.toFENString(), calcTimeMilli),board);
            board.doMove(stockFishMove);
            gameState = board.getGameState(masks);
            if (gameState == GameState.DRAW || gameState == GameState.BLACK_WON) {
                System.out.println("Stockfish won with elocount:"+elo);
                return false;
            }
        }
    }
    public static boolean playAgainstFairyStockfish(int elo,int calcTimeMilli){
        Board board = new Board();
        MoveMasks masks = new MoveMasks();
        GameState gameState;
        while (true){
            Move m = AlphaBeta.getBestMoveTimed(board,masks,calcTimeMilli);
            board.doMove(m);
            gameState = board.getGameState(masks);
            if (gameState == GameState.DRAW || gameState == GameState.WHITE_WON) {
                System.out.println("The Ai won with elocount:"+elo);
                return true;
            }
            Move fairyMove = fairyMove(getMoveFromFairy(elo, board.toFENString(), calcTimeMilli),board);
            board.doMove(fairyMove);
            gameState = board.getGameState(masks);
            if (gameState == GameState.DRAW || gameState == GameState.BLACK_WON) {
                System.out.println("Stockfish won with elocount:"+elo);
                return false;
            }
        }
    }
    public static Move stockfishMove(String move,Board board){
           String []fields = {"a1","b1","c1","d1","e1","f1","g1","h1",
                              "a2","b2","c2","d2","e2","f2","g2","h2",
                              "a3","b3","c3","d3","e3","f3","g3","h3",
                              "a4","b4","c4","d4","e4","f4","g4","h4",
                              "a5","b5","c5","d5","e5","f5","g5","h5",
                              "a6","b6","c6","d6","e6","f6","g6","h6",
                              "a7","b7","c7","d7","e7","f7","g7","h7",
                              "a8","b8","c8","d8","e8","f8","g8","h8",};

           int startPos = Arrays.asList(fields).indexOf(move.substring(0,2));
           int endPos = Arrays.asList(fields).indexOf(move.substring(2));
           String aktuellesB = board.toString();
           PieceType pieceType = getPieceTyp(reoderString(aktuellesB).charAt(startPos));

           Move move1 = new Move(startPos,endPos,pieceType);
           return move1;
    }
    public static Move fairyMove(String move,Board board){
        String []fields = {
                "a1","b1","c1","d1","e1","f1","g1","h1",
                "a2","b2","c2","d2","e2","f2","g2","h2",
                "a3","b3","c3","d3","e3","f3","g3","h3",
                "a4","b4","c4","d4","e4","f4","g4","h4",
                "a5","b5","c5","d5","e5","f5","g5","h5",
                "a6","b6","c6","d6","e6","f6","g6","h6",
                "a7","b7","c7","d7","e7","f7","g7","h7",
                "a8","b8","c8","d8","e8","f8","g8","h8",};

        int startPos = Arrays.asList(fields).indexOf(move.substring(0,2));
        int endPos = Arrays.asList(fields).indexOf(move.substring(2));
        String aktuellesB = board.toString();
        PieceType pieceType = getPieceTyp(reoderString(aktuellesB).charAt(startPos));

        return new Move(startPos,endPos,pieceType);
    }
    private static String reoderString(String b){
        String res = "";
        for (int i = b.length()-2; i > 0; i--) {
            if(b.charAt(i) == '\n'){
                for (int j = i+1; j < i+9; j++) {
                    res = res + b.charAt(j);
                }
            }
        }
        for (int i = 0; i < 8; i++) {
            res = res + b.charAt(i);
        }
        return res;
    }
    private static PieceType getPieceTyp(char figur){
        if (figur == 'p' || figur == 'P'){
            return PieceType.PAWN;
        } else if (figur == 'r' || figur == 'R') {
            return PieceType.ROOK;
        } else if (figur == 'q' || figur == 'Q') {
            return PieceType.QUEEN;
        } else if (figur == 'b' || figur == 'B') {
            return PieceType.BISHOP;
        } else if (figur == 'n' || figur == 'N') {
            return PieceType.KNIGHT;
        } else if (figur == 'k' || figur == 'K') {
            return PieceType.KING;
        }
        return null;
    }

    public static String getMoveFromStockfish(int elo, String fen, int timeInMillis){
        try {
            Process engine = Runtime.getRuntime().exec("../stockfish");
            BufferedReader reader = new BufferedReader(new InputStreamReader(engine.getInputStream()));
            OutputStreamWriter writer = new OutputStreamWriter(engine.getOutputStream());

            writer.write("setoption name UCI_LimitStrength value true\n");
            writer.flush();
            writer.write("setoption name UCI_Elo value " + elo + "\n");
            writer.flush();

            writer.write("position fen " + fen + "\n");
            writer.flush();
            writer.write("go movetime " + timeInMillis + "\n");
            writer.flush();

            Thread.sleep(timeInMillis + 25);
            String currentLine = reader.readLine();
            while (!currentLine.contains("bestmove")) {
                currentLine = reader.readLine();
            }
            return currentLine.split(" ")[1];
        }catch (IOException | InterruptedException e){
            System.out.println(e.getMessage());
            return "";
        }
    }

    public static String getMoveFromFairy(int elo, String fen, int timeInMillis){
        try {
            Process engine = Runtime.getRuntime().exec("../fairystockfish");
            BufferedReader reader = new BufferedReader(new InputStreamReader(engine.getInputStream()));
            OutputStreamWriter writer = new OutputStreamWriter(engine.getOutputStream());
            writer.write("setoption name UCI_Variant value kingofthehill\n");
            writer.flush();
            writer.write("setoption name UCI_LimitStrength value true\n");
            writer.flush();
            writer.write("setoption name UCI_Elo value " + elo + "\n");
            writer.flush();

            writer.write("position fen " + fen + "\n");
            writer.flush();
            writer.write("go movetime " + timeInMillis + "\n");
            writer.flush();

            Thread.sleep(timeInMillis + 25);
            String currentLine = reader.readLine();
            while (!currentLine.contains("bestmove")) {
                currentLine = reader.readLine();
            }
            System.out.println(currentLine);
            return currentLine.split(" ")[1];
        }catch (IOException | InterruptedException e){
            System.out.println(e.getMessage());
            return "";
        }
    }

}




 */
