import engine.representation.Board;

import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {
        Board board = new Board();
        System.out.println(board);
        System.out.println(board.toFENString());
    }
}