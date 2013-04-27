package pcpsexamples;

import scheduler.Activation;
import scheduler.AbstractContinuation;
import scheduler.Continuation;
import scheduler.Scheduler;

public class Fib {
    public Scheduler scheduler;
    
    public Fib() {
        scheduler = new Scheduler();
    }

    void fib(int k, Activation now, Activation later) {
        if (k <= 2) {
            now.tempResult = 1;
        } else {
            //make left and right available inside closure
            Activation left = new Activation();
            Activation right = new Activation();
            Activation sum = new Activation();
            Activation then = now;
  
            left.continuation = new ContinuationLeft(k, this, left, sum);
            right.continuation = new ContinuationRight(k, this, right, sum);
            sum.continuation = new ContinuationSum(then, left, right, sum, later);
          
            // TODO: For now, cos I'm using a Stack, schedule them in
            // reverse order
            scheduler.addTask(left);
            scheduler.addTask(right);
            scheduler.addTask(sum);

            scheduler.happensBefore(now, left);
            scheduler.happensBefore(now, right);
            scheduler.happensBefore(now, sum);
            scheduler.happensBefore(left, sum);
            scheduler.happensBefore(right, sum);
            scheduler.happensBefore(sum, later);
            
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
        name = "Left";
        this.k = k;
        this.object = object;
    }
        
    @Override
    public void call(){
        object.fib(k - 1, now, later);
    }
}
    
class ContinuationRight extends AbstractContinuation {
    int k;
    Fib object;
    
    public ContinuationRight(int k, Fib object, Activation now, Activation later){
        super(now, later);
        name = "Right";
        this.k = k;
        this.object = object;
    }
        
    @Override
    public void call(){
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
    public void call(){
        //sum ’returns’ for fib()
        then.tempResult = ((int)left.tempResult)
                + ((int)right.tempResult);
    }
}
