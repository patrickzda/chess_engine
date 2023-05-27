package engine.representation;

import java.util.Arrays;
import java.util.Random;

public class TranspositionTable {

    private final int HASHTABLE_SIZE = 10000;
    private final TranspositionTableEntry[] entries = new TranspositionTableEntry[HASHTABLE_SIZE];
    private final long[][] zobristValues = new long[64][12];
    private final long blackToMove;

    TranspositionTable(){
        Random random = new Random();

        blackToMove = random.nextLong();
        for(int i = 0; i < 64; i++){
            for(int j = 0; j < 12; j++){
                zobristValues[i][j] = random.nextLong();
            }
        }

        Arrays.fill(entries, null);
    }

    public void addEntry(Board board, Move bestMove, int depth, int evaluation){
        long zobristKey = generateKey(board);
        int index = (int) (zobristKey % HASHTABLE_SIZE);

        if(entries[index] == null || entries[index].getDepth() <= depth || (entries[index].getZobristKey() != zobristKey && depth >= 3)){
            entries[index] = new TranspositionTableEntry(zobristKey, bestMove, depth, evaluation);
        }
    }

    public TranspositionTableEntry getEntry(Board board, int depth){
        long zobristKey = generateKey(board);
        int index = (int) (zobristKey % HASHTABLE_SIZE);

        if(entries[index] != null && entries[index].getZobristKey() == zobristKey && entries[index].getDepth() >= depth){
            return entries[index];
        }else{
            return null;
        }
    }

    private long generateKey(Board board){
        long key = 0L;

        if(board.getTurn() == Color.BLACK){
            key = key ^ blackToMove;
        }

        for(int i = 0; i < 64; i++){
            long index = 1L << i;
            if((board.whitePieces & index) != 0){
                if((board.whitePieces & board.pawns & index) != 0){
                    key = key ^ zobristValues[i][0];
                }else if((board.whitePieces & board.knights & index) != 0){
                    key = key ^ zobristValues[i][1];
                }else if((board.whitePieces & board.bishops & index) != 0){
                    key = key ^ zobristValues[i][2];
                }else if((board.whitePieces & board.rooks & index) != 0){
                    key = key ^ zobristValues[i][3];
                }else if((board.whitePieces & board.queens & index) != 0){
                    key = key ^ zobristValues[i][4];
                }else if((board.whitePieces & board.kings & index) != 0){
                    key = key ^ zobristValues[i][5];
                }
            }else{
                if((board.blackPieces & board.pawns & index) != 0){
                    key = key ^ zobristValues[i][6];
                }else if((board.blackPieces & board.knights & index) != 0){
                    key = key ^ zobristValues[i][7];
                }else if((board.blackPieces & board.bishops & index) != 0){
                    key = key ^ zobristValues[i][8];
                }else if((board.blackPieces & board.rooks & index) != 0){
                    key = key ^ zobristValues[i][9];
                }else if((board.blackPieces & board.queens & index) != 0){
                    key = key ^ zobristValues[i][10];
                }else if((board.blackPieces & board.kings & index) != 0){
                    key = key ^ zobristValues[i][11];
                }
            }
        }

        return key;
    }

}
