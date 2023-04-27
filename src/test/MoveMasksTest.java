package test;

import static org.junit.jupiter.api.Assertions.*;
import engine.move_generation.MoveMasks;
import org.junit.Test;

class MoveMasksTest {
    @Test
    public void MoveMasks() {
        System.out.println("Starte Test für MoveMasks");
        MoveMasks m = new MoveMasks();

        for (int i = 0; i < 8; i++) {
            System.out.print("Horizontale " + i + ":");
            long h = m.horizontals[i];
            for(int j = 0; j < Long.numberOfLeadingZeros((long)h); j++) {
                System.out.print('0');
            }
            System.out.println(Long.toBinaryString((long)h));


            System.out.print("Vertikale " + i + ":");
            long v = m.verticals[i];
            for(int j = 0; j < Long.numberOfLeadingZeros((long)v); j++) {
                System.out.print('0');
            }
            System.out.println(Long.toBinaryString((long)v));
        }

    }
}