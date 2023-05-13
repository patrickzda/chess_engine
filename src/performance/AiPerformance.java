package performance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class AiPerformance {

    //Damit diese Methode funktioniert muss folgendes gewährleistet sein: Im Ordner, in dem sich das Projekt chess_engine befindet, muss die Stockfish-Binary liegen.
    //Gibt für eine bestimmte Elo-Spielstärke (min 1320, max 3190), einen bestimmten FEN-String und eine Rechenzeitangabe einen Zug zurück.
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

}
