package test;

import engine.tools.TaperedPST;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaperedPSTTest {

    @Test
    void getMirroredIndex() {
        int counter = 63;
        for(int i = 0; i < 8; i++){
            for(int j = 7; j >= 0; j--){
                int mirroredIndex = i * 8 + j;
                assertEquals(TaperedPST.PAWN_PST_MID[mirroredIndex], TaperedPST.PAWN_PST_MID[TaperedPST.getMirroredIndex(counter)]);
                counter--;
            }
        }
    }
}