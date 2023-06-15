package engine.tools;

import engine.representation.Board;
import engine.representation.EvaluationType;
import engine.representation.Move;
import engine.tools.TranspositionTableEntry;

import java.util.Random;

public class TranspositionTable {
    private final int HASHTABLE_SIZE = 64000;
    private final TranspositionTableEntry[][] depthEntries = new TranspositionTableEntry[HASHTABLE_SIZE][2], alwaysReplaceEntries = new TranspositionTableEntry[HASHTABLE_SIZE][2];
    private final long[][] zobristValues = new long[64][12];
    private final long whiteKingMoved, blackKingMoved, whiteShortRookMoved, whiteLongRookMoved, blackShortRookMoved, blackLongRookMoved;
    //private int filledEntries = 0, triedToAddCount = 0, overriddenEntries = 0;

    public TranspositionTable(){
        Random random = new Random();

        whiteKingMoved = random.nextLong();
        blackKingMoved = random.nextLong();
        whiteShortRookMoved = random.nextLong();
        whiteLongRookMoved = random.nextLong();
        blackShortRookMoved = random.nextLong();
        blackLongRookMoved = random.nextLong();

        for(int i = 0; i < 64; i++){
            for(int j = 0; j < 12; j++){
                zobristValues[i][j] = random.nextLong();
            }
        }

        for(int i = 0; i < HASHTABLE_SIZE; i++){
            depthEntries[i] = new TranspositionTableEntry[]{null, null};
            alwaysReplaceEntries[i] = new TranspositionTableEntry[]{null, null};
        }
    }

    public void addEntry(Board board, Move bestMove, int depth, int evaluation, EvaluationType type){
        long zobristKey = generateKey(board);
        int index = (int) Long.remainderUnsigned(zobristKey, HASHTABLE_SIZE);

        //triedToAddCount++;

        if(Math.abs(evaluation) > 69000){
            return;
        }

        if(depthEntries[index][board.getTurn().ordinal()] == null || depthEntries[index][board.getTurn().ordinal()].getDepth() <= depth){

            //if(depthEntries[index][board.getTurn().ordinal()] == null){
            //    filledEntries++;
            //}else{
            //    overriddenEntries++;
            //}

            depthEntries[index][board.getTurn().ordinal()] = new TranspositionTableEntry(zobristKey, bestMove, depth, evaluation, type);
        }else{

            //if(alwaysReplaceEntries[index][board.getTurn().ordinal()] == null){
            //    filledEntries++;
            //}else{
            //    overriddenEntries++;
            //}

            alwaysReplaceEntries[index][board.getTurn().ordinal()] = new TranspositionTableEntry(zobristKey, bestMove, depth, evaluation, type);
        }
    }

    public TranspositionTableEntry getEntry(Board board, int depth){
        long zobristKey = generateKey(board);
        int index = (int) Long.remainderUnsigned(zobristKey, HASHTABLE_SIZE);

        if(depthEntries[index][board.getTurn().ordinal()] != null && depthEntries[index][board.getTurn().ordinal()].getZobristKey() == zobristKey && depthEntries[index][board.getTurn().ordinal()].getDepth() >= depth){
            return depthEntries[index][board.getTurn().ordinal()];
        }else if(alwaysReplaceEntries[index][board.getTurn().ordinal()] != null && alwaysReplaceEntries[index][board.getTurn().ordinal()].getZobristKey() == zobristKey && alwaysReplaceEntries[index][board.getTurn().ordinal()].getDepth() >= depth){
            return alwaysReplaceEntries[index][board.getTurn().ordinal()];
        }else{
            return null;
        }
    }

    public TranspositionTableEntry lookup(Board board){
        long zobristKey = generateKey(board);
        int index = (int) Long.remainderUnsigned(zobristKey, HASHTABLE_SIZE);

        if(depthEntries[index][board.getTurn().ordinal()] != null && depthEntries[index][board.getTurn().ordinal()].getZobristKey() == zobristKey){
            return depthEntries[index][board.getTurn().ordinal()];
        }else if(alwaysReplaceEntries[index][board.getTurn().ordinal()] != null && alwaysReplaceEntries[index][board.getTurn().ordinal()].getZobristKey() == zobristKey){
            return alwaysReplaceEntries[index][board.getTurn().ordinal()];
        }else{
            return null;
        }
    }

    public long generateKey(Board board){
        long key = 0L;

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

            if(board.getHasWhiteKingMoved()){
                key = key ^ whiteKingMoved;
            }
            if(board.getHasBlackKingMoved()){
                key = key ^ blackKingMoved;
            }
            if(board.getHasWhiteShortRookMoved()){
                key = key ^ whiteShortRookMoved;
            }
            if(board.getHasBlackShortRookMoved()){
                key = key ^ blackShortRookMoved;
            }
            if(board.getHasWhiteLongRookMoved()){
                key = key ^ whiteLongRookMoved;
            }
            if(board.getHasBlackLongRookMoved()){
                key = key ^ blackLongRookMoved;
            }
        }

        return key;
    }

    public void printStats(){
        //System.out.println("---");
        //System.out.println("Table is filled up to " + (((double) (filledEntries / 1000)) / ((double) (HASHTABLE_SIZE * 4) / 1000)) * 100 + " %");
        //System.out.println("Tried to fill table with " + triedToAddCount + " entries");
        //System.out.println("Total filled entries: " + filledEntries);
        //System.out.println("Total amount of overridden entries: " + overriddenEntries);
        //System.out.println("---");
    }

    public void clear(){
        //filledEntries = 0;
        //triedToAddCount = 0;
        //overriddenEntries = 0;

        for(int i = 0; i < HASHTABLE_SIZE; i++){
            depthEntries[i] = new TranspositionTableEntry[]{null, null};
            alwaysReplaceEntries[i] = new TranspositionTableEntry[]{null, null};
        }
    }

    public int getSize() {
        return HASHTABLE_SIZE;
    }
}
