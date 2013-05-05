package cpsexamples;

import scheduler.AbstractContinuation;
import scheduler.Activation;
import scheduler.Continuation;
import scheduler.LastActivation;
import scheduler.Scheduler;

public class SimilarFib {
    public Scheduler scheduler;

    private static final boolean NON_LINEAR_SCHEDULE = true;
    public static final long MAX_ITERS = 1L;
    // public static final long MAX_ITERS = 5000000L;
    
    public SimilarFib() {
        scheduler = null;
    }

    void fib(int k, Activation now, Activation later) {
        if (k <= 2) {
            now.tempResult = 1;
        } else {
            for (long i = 0; i < MAX_ITERS; i++){
            }

            //make left and right available inside closure
            Activation left = new Activation(scheduler);
            Activation right = new Activation(scheduler);
            Activation sum = new Activation(scheduler);
            Activation then = now;
  
            if (NON_LINEAR_SCHEDULE){
                left.continuation = new ContinuationLeft(k, this, left, sum);
            } else {
                left.continuation = new ContinuationLeft(k, this, left, right);
            }
            right.continuation = new ContinuationRight(k, this, right, sum);
            sum.continuation = new ContinuationSum(then, left, right, sum, later);

            {
                left.run();
                right.run();
                sum.run();
            }
            // synchronized (scheduler.lock){
            //     scheduler.addTask(left);
            //     scheduler.addTask(right);
            //     scheduler.addTask(sum);

            //     scheduler.happensBefore(now, left);
            //     scheduler.happensBefore(now, right);
            //     scheduler.happensBefore(now, sum);
            //     if (NON_LINEAR_SCHEDULE){
            //         scheduler.happensBefore(left, sum);
            //     } else {
            //         scheduler.happensBefore(left, right);
            //     }
            //     scheduler.happensBefore(right, sum);
            //     scheduler.happensBefore(sum, later);
            // }
            
            // left→right; //inserted by naive translation
            // right→sum;
            // sum→later;
        }
    }

    public static int getFib(final int k){
        SimilarFib similarFib = new SimilarFib();
        similarFib.scheduler = null;
        Activation now = new Activation(similarFib.scheduler);
        Activation later = new LastActivation(similarFib.scheduler);
        now.continuation = new ContinuationFib(k, similarFib, now, later);

        {
            now.run();
            later.run();
        }
        // similarFib.scheduler.addTask(now);
        // similarFib.scheduler.addTask(later);
        // similarFib.scheduler.happensBefore(now, later);

        // VVIP: MAIN-CALL
        // similarFib.scheduler.tryRunTasks(now);

        return now.tempResult;
    }

    public static int getRecursiveFib(int k){
        if (k <= 2){
            return 1;
        }

        for (int i = 0; i < MAX_ITERS; i++){
            
        }

        return SimilarFib.getRecursiveFib(k - 1) + SimilarFib.getRecursiveFib(k - 2);
    }
}

// Continuation for the fib method as a whole
class ContinuationFib extends AbstractContinuation {
    int k;
    SimilarFib object;
    
    public ContinuationFib(int k, SimilarFib object,
                            Activation now, Activation later){
        super(now, later);
        name = "Fib k: " + k;
        this.k = k;
        this.object = object;
    }
        
    @Override
    public void run(){
        object.fib(k, now, later);
    }
}

class ContinuationLeft extends AbstractContinuation {
    int k;
    SimilarFib object;
    
    public ContinuationLeft(int k, SimilarFib object,
                            Activation now, Activation later){
        super(now, later);
        name = "Left k: " + k + " k-1: " + (k - 1);
        this.k = k;
        this.object = object;
    }
        
    @Override
    public void run(){
        object.fib(k - 1, now, later);
    }
}
    
class ContinuationRight extends AbstractContinuation {
    int k;
    SimilarFib object;
    
    public ContinuationRight(int k, SimilarFib object, Activation now, Activation later){
        super(now, later);
        name = "Right k: " + k + " k-2: " + (k - 2);
        this.k = k;
        this.object = object;
    }
        
    @Override
    public void run(){
        object.fib(k - 2, now, later);
    }
}
    
class ContinuationSum extends AbstractContinuation {
    Activation left;
    Activation right;
    Activation then;
    
    public ContinuationSum(Activation then, Activation left,
                           Activation right, Activation now, Activation later){
        super(now, later);
        name = "Sum";
        this.then = then;
        this.left = left;
        this.right = right;
    }
        
    @Override
    public void run(){
        //sum ’returns’ for fib()
        then.tempResult = ((int)left.tempResult)
                + ((int)right.tempResult);
    }
}
