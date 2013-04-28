package scheduler;

import java.lang.Runnable;

public class Activation implements Runnable, Comparable<Activation> {
    public Continuation continuation;
    public Result result;
    public Scheduler scheduler;
    public int tempResult;

    private boolean isScheduled;
    
    public Activation(Scheduler scheduler) {
        this(0, scheduler);
    }

    public Activation(int result, Scheduler scheduler) {
        this.tempResult = result;
        this.scheduler = scheduler;
        this.isScheduled = false;
    }

    public int compareTo(Activation other){
        int ourHashCode = this.hashCode();
        int otherHashCode = other.hashCode();
        
        if (ourHashCode == otherHashCode){
            return 0;
        }
        else if (ourHashCode < otherHashCode){
            return -1;
        } else{
            return 1;
        }
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
            System.out.println("Activation: run()"); 
            continuation.run();
            System.out.println("Activation: completed run()"); 
        }
        scheduler.signalTaskDone(this);
    }

    public boolean isScheduled(){
        return isScheduled;
    }

    public void setIsScheduled(){
        isScheduled = true;
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
