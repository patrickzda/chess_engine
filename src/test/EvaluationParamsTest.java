package test;

import engine.tools.EvaluationParams;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EvaluationParamsTest {

    @Test
    void getMirroredIndex() {
        int counter = 63;
        for(int i = 0; i < 8; i++){
            for(int j = 7; j >= 0; j--){
                int mirroredIndex = i * 8 + j;
                assertEquals(EvaluationParams.PAWN_PST_MID[mirroredIndex], EvaluationParams.PAWN_PST_MID[EvaluationParams.getMirroredIndex(counter)]);
                counter--;
            }
        }
    }
}