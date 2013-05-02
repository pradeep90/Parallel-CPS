package pcpsexamples;

import scheduler.AbstractContinuation;
import scheduler.Activation;
import scheduler.Continuation;
import scheduler.LastActivation;
import scheduler.Scheduler;

public class Fib {
    public Scheduler scheduler;

    private static final boolean NON_LINEAR_SCHEDULE = true;
    // public static final long MAX_ITERS = 1L;
    public static final long MAX_ITERS = 500000L;
    
    public Fib() {
        scheduler = new Scheduler();
    }

    void fib(int k, Activation now, Activation later) {
        if (k <= 2) {
            now.tempResult = 1;
        } else {
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
          
            synchronized (scheduler.lock){
                scheduler.addTask(left);
                scheduler.addTask(right);
                scheduler.addTask(sum);

                scheduler.happensBefore(now, left);
                scheduler.happensBefore(now, right);
                scheduler.happensBefore(now, sum);
                if (NON_LINEAR_SCHEDULE){
                    scheduler.happensBefore(left, sum);
                } else {
                    scheduler.happensBefore(left, right);
                }
                scheduler.happensBefore(right, sum);
                scheduler.happensBefore(sum, later);
            }
            
            // left→right; //inserted by naive translation
            // right→sum;
            // sum→later;
        }
    }

    public static int getFib(final int k){
        final Fib fib = new Fib();
        Activation now = new Activation(fib.scheduler);
        Activation later = new LastActivation(fib.scheduler);
        Continuation current = new AbstractContinuation(now, later){
                @Override
                public void run(){
                    fib.fib(k, now, later);
                }
            };
        now.continuation = current;

        fib.scheduler.addTask(now);
        fib.scheduler.addTask(later);

        fib.scheduler.happensBefore(now, later);

        fib.scheduler.tryRunTasks(now);

        return now.tempResult;
    }

    public static int getRecursiveFib(int k){
        if (k <= 2){
            return 1;
        }

        for (int i = 0; i < MAX_ITERS; i++){
            
        }

        return Fib.getRecursiveFib(k - 1) + Fib.getRecursiveFib(k - 2);
    }
}
    
class ContinuationLeft extends AbstractContinuation {
    int k;
    Fib object;
    
    public ContinuationLeft(int k, Fib object,
                            Activation now, Activation later){
        super(now, later);
        name = "Left k: " + k + " k-1: " + (k - 1);
        this.k = k;
        this.object = object;
    }
        
    @Override
    public void run(){
        for (long i = 0; i < object.MAX_ITERS; i++){
        }

        object.fib(k - 1, now, later);
    }
}
    
class ContinuationRight extends AbstractContinuation {
    int k;
    Fib object;
    
    public ContinuationRight(int k, Fib object, Activation now, Activation later){
        super(now, later);
        name = "Right k: " + k + " k-2: " + (k - 2);
        this.k = k;
        this.object = object;
    }
        
    @Override
    public void run(){
        for (long i = 0; i < object.MAX_ITERS; i++){
        }
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
