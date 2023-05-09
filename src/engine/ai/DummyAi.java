package engine.ai;

import engine.move_generation.MoveGenerator;
import engine.move_generation.MoveMasks;
import engine.representation.Board;
import engine.representation.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DummyAi {

    public static Move selectMove(Board board){
        MoveMasks masks = new MoveMasks();
        Random random = new Random();

        Move[] possibleMoves = MoveGenerator.generateLegalMoves(board, masks);
        Move bestMove = possibleMoves[0];
        int bestScore = -39;

        for(int i = 0; i < possibleMoves.length; i++){
            board.doMove(possibleMoves[i]);
            if(Evaluation.evaluate(board) > bestScore){
                bestMove = possibleMoves[i];
                bestScore = Evaluation.evaluate(board);
            }
            board.undoLastMove();
        }

        try {
            TimeUnit.SECONDS.sleep(1);
        }catch (InterruptedException e){
            System.out.println(e.toString());
        }

        if(random.nextInt(4) == 3){
            return bestMove;
        }else{
            return possibleMoves[random.nextInt(possibleMoves.length)];
        }
    }

}
