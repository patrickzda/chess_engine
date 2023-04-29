package performance;


import java.util.ArrayList;
import java.util.Arrays;

public class MoveGeneratorPerformance {

    public void FunktionToTest(){
        fillArray();

        /*long total = 0;
        for (long i = Integer.MIN_VALUE; i < Integer.MAX_VALUE* 2L; i++) {
            total += i;


        }
        for (int j = Integer.MIN_VALUE; j < Integer.MAX_VALUE; j++) {
            total += j;
        }*/
    }

    public void fillArray(){
        String arr[] = new String[100000000];
        for (int i = 0; i < arr.length; i++){
            arr[i] = "hello_"+i ;
        }
    }
    public void fillArrayList(){
        ArrayList<String> arr = new ArrayList<String>();
        for (int i = 0; i < 100000000; i++){
            arr.add("hello_"+i);
        }
    }

    public void PerfomanceTime(int passes){
        long startTime = System.currentTimeMillis();
        fillArray();
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Array Firsttime took: "+elapsedTime+" Milliseconds");
        if(passes > 0) System.out.println("The Average is: " +AverageExecutionTime(passes));


        startTime = System.currentTimeMillis();
        fillArrayList();
        stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;
        System.out.println("ArrayList Firsttime took: "+elapsedTime+" Milliseconds");
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
