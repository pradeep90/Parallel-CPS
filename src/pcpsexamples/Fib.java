package pcpsexamples;

import scheduler.Activation;
import scheduler.AbstractContinuation;
import scheduler.Continuation;
import scheduler.Scheduler;

public class Fib {
    public Scheduler scheduler;

    private static final boolean NON_LINEAR_SCHEDULE = true;
    
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

    public void methodFib1(int k, Activation now, Activation later){
        fib(k - 1, now, later);
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
