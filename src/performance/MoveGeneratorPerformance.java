package performance;


import java.util.Arrays;

public class MoveGeneratorPerformance {

    public void FunktionToTest(){
        long total = 0;
        for (long i = Integer.MIN_VALUE; i < Integer.MAX_VALUE* 2L; i++) {
            total += i;

            /**/
        }
        for (int j = Integer.MIN_VALUE; j < Integer.MAX_VALUE; j++) {
            total += j;
        }
    }

    public void PerfomanceTime(int passes){
        long startTime = System.currentTimeMillis();
        FunktionToTest();
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("First Time took: "+elapsedTime+" Milliseconds");
        if(passes > 0) System.out.println("The Average is: " +AverageExecutionTime(passes));

    }

    String AverageExecutionTime(int passes){
        long[] times = new long[passes];
        for (int i = 0; i < passes; i++){
            long startTime = System.currentTimeMillis();
            FunktionToTest();
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            times[i] = elapsedTime;
        }
        long avg = findAverage(times);
        return ""+avg;
    }

    public static long findAverage(long[] array){
        long sum = findSumWithoutUsingStream(array);
        long res = sum / array.length;
        return res;
    }
    public static long findSumWithoutUsingStream(long[] array) {
        long sum = 0;
        for (long value : array) {
            sum += value;
        }
        return sum;
    }



}
