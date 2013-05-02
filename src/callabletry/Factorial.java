package callabletry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Factorial {
    private static final int NUM_THREADS = 10;
    public static final int NUM_ITERS = 10;
    public static final int NUM = 25;
    public static final int FIB_NUM = 8;
    // public static final int FIB_NUM = 15;

    // Setting it to 16 causes StackOverflowError
    public static final int FIB_CONT_NUM = 15;
    
    ExecutorService executor;
    
    public Factorial(){
        executor = Executors.newFixedThreadPool(NUM_THREADS);
    }

    public Long fibRecursive(Long n){
        if (n <= 2){
            return new Long(1);
        }
        return fibRecursive(n - 1) + fibRecursive(n - 2);
    }

    public void fibCont(final Long n, final Continuation k){
        if (n <= 2){
            k.call(new Long(1));
        } else {
            fibCont(n - 1, new FibContinuation1(n, this, k));
        }
    }

    public void fibFutures(Long n){
        // Callable<Long> worker = new MyCallable();
        // Future<Long> submit = executor.submit(worker);

        // try {
        //     sum += future.get();
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // } catch (ExecutionException e) {
        //     e.printStackTrace();
        // }
        // executor.shutdown();
        facCont(n, new IdentityContinuation());
    }

    public void fibUsingCont(Long n){
        // fibCont(n, new IdentityPrintContinuation());
        fibCont(n, new IdentityContinuation());
    }

    public Long facIter(Long n, Long acc){
        if (n == 0){
            return acc;
        } else {
            return facIter(n - 1, n * acc);
        }
    }

    public void facCont(Long n, Continuation k){
        if (n == 0){
            k.call(new Long(1));
        } else {
            facCont(n - 1, new Continuation1(n, k));
        }
    }

    public void facFutures(Long n){
        // Callable<Long> worker = new MyCallable();
        // Future<Long> submit = executor.submit(worker);

        // try {
        //     sum += future.get();
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // } catch (ExecutionException e) {
        //     e.printStackTrace();
        // }
        // executor.shutdown();
        facCont(n, new IdentityContinuation());
    }

    public Long fac(Long n){
        if (n == 0){
            return new Long(1);
        } else {
            return n * fac(n - 1);
        }
    }

    public Long facUsingIter(Long n){
        return facIter(n, new Long(1));
    }

    public void facUsingCont(Long n){
        // ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        // Callable<Long> worker = new MyCallable();
        // Future<Long> submit = executor.submit(worker);

        // try {
        //     sum += future.get();
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // } catch (ExecutionException e) {
        //     e.printStackTrace();
        // }
        // executor.shutdown();
        facCont(n, new IdentityContinuation());
    }

    public static void measureFacNormal(){
        MethodCall methodCall = new MethodCall("fac"){
                public void call(){
                    factorial.fac(new Long(NUM));
                }
            };
        methodCall.measure();
    }
    
    public static void measureFacIter(){
        MethodCall methodCall = new MethodCall("facIter"){
                public void call(){
                    factorial.facUsingIter(new Long(NUM));
                }
            };
        methodCall.measure();
    }
    
    public static void measureFacCont(){
        MethodCall methodCall = new MethodCall("facCont"){
                public void call(){
                    factorial.facUsingCont(new Long(NUM));
                }
            };
        methodCall.measure();
    }
    
    public static void measureFibRecursive(){
        MethodCall methodCall = new MethodCall("fibRecursive"){
                public void call(){
                    factorial.fibRecursive(new Long(FIB_NUM));
                }
            };
        methodCall.measure();
    }
    
    public static void measureFibCont(){
        MethodCall methodCall = new MethodCall("fibCont"){
                public void call(){
                    factorial.fibUsingCont(new Long(FIB_CONT_NUM));
                }
            };
        methodCall.measure();
    }

    public static void main(String[] args) {
        Factorial factorial = new Factorial();
        System.out.println("factorial.fac(NUM): " + factorial.fac(new Long(NUM)));

        System.out.println("factorial.fibRecursive(new Long(FIB_NUM)): " + factorial.fibRecursive(new Long(FIB_NUM)));

        factorial.fibCont(new Long(FIB_CONT_NUM), new IdentityPrintContinuation());
        
        measureFibRecursive();
        measureFibCont();
        measureFacNormal();
        measureFacIter();
        measureFacCont();
        
        // List<Future<Long>> list = new ArrayList<Future<Long>>();
        // for (int i = 0; i < 20000; i++) {
        //     Callable<Long> worker = new MyCallable();
        //     Future<Long> submit = executor.submit(worker);
        //     list.add(submit);
        // }
        // long sum = 0;
        // System.out.println(list.size());
        // // Now retrieve the result
        // for (Future<Long> future : list) {
        //     try {
        //         sum += future.get();
        //     } catch (InterruptedException e) {
        //         e.printStackTrace();
        //     } catch (ExecutionException e) {
        //         e.printStackTrace();
        //     }
        // }
        // System.out.println(sum);
        // executor.shutdown();
    }
} 

interface Continuation {
    public void call(Long n);
}

class Continuation1 implements Continuation {
    Long n;
    Continuation k;
    
    public Continuation1(Long n, Continuation k) {
        this.n = n;
        this.k = k;
    }

    @Override
    public void call(Long ans){
        k.call(n * ans);
    }
}

class FibContinuation1 implements Continuation {
    Long n;
    Continuation k;
    Factorial factorial;
    
    public FibContinuation1(Long n, Factorial factorial, Continuation k) {
        this.n = n;
        this.factorial = factorial;
        this.k = k;
    }

    @Override
    public void call(Long ans1){
        factorial.fibCont(n - 2, new FibContinuation2(ans1, k));
    }
}

class FibContinuation2 implements Continuation {
    Long ans1;
    Continuation k;
    
    public FibContinuation2(Long ans1, Continuation k) {
        this.ans1 = ans1;
        this.k = k;
    }

    @Override
    public void call(Long ans2){
        k.call(ans1 + ans2);
    }
}

class IdentityContinuation implements Continuation {
    @Override
    public void call(Long ans){
    }
}

class IdentityPrintContinuation extends IdentityContinuation {
    @Override
    public void call(Long ans){
        System.out.println("ans: " + ans);
    }
}
