package pcpsexamples;

import cpsexamples.SimilarFib;

public class Main {
    
    public Main() {
        
    }

    public static void collectRunningTime(){
        final int k = 10;

        System.out.println(""); 
        System.out.println("k: " + k);
        MethodCall methodCall1 = new MethodCall("SequentialRecursiveSimilarFib"){
                public void call(){
                    SimilarFib.getRecursiveFib(k);
                }
            };
        methodCall1.measure();

        System.out.println("k: " + k);
        MethodCall methodCall11 = new MethodCall("SequentialRecursiveFib"){
                public void call(){
                    pcpsexamples.Fib.getRecursiveFib(k);
                }
            };
        methodCall11.measure();

        MethodCall methodCall2 = new MethodCall("CPSFib"){
                public void call(){
                    SimilarFib.getFib(k);
                }
            };
        methodCall2.measure();

        MethodCall methodCall22 = new MethodCall("ParallelCPSFib"){
                public void call(){
                    pcpsexamples.Fib.getFib(k);
                }
            };
        methodCall22.measure();
    }

    public static void main(String[] args) {
        collectRunningTime();
    }
}
