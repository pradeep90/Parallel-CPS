package scheduler;

import java.lang.Runnable;

public class Activation implements Runnable {
    public Continuation continuation;
    public Result result;
    public Scheduler scheduler;
    public int tempResult;

    public Activation(Scheduler scheduler) {
        this(0, scheduler);
    }

    public Activation(int result, Scheduler scheduler) {
        this.tempResult = result;
        this.scheduler = scheduler;
    }
    
    // public Activation(Continuation continuation, Result result) {
    //     this.continuation = continuation;
    //     this.result = result;
    // }

    // public Activation(Continuation continuation, int result) {
    //     this.continuation = continuation;
    //     this.tempResult = result;
    // }

    @Override
    public void run(){
        if (continuation != null){
            continuation.run();
        }
        scheduler.signalTaskDone(this);
    }

    public String toString(){
        String result = "<Activation: ";
        result += continuation;
        result += ", ";
        result += tempResult;
        result += ">";
        return result;
    }
}
