package scheduler;

import java.lang.Runnable;
import java.util.concurrent.atomic.AtomicLong;

public class Activation implements Runnable, Comparable<Activation> {
    public Continuation continuation;
    public Result result;
    public Scheduler scheduler;
    public int tempResult;

    private long id;

    // TODO: This might lead to overflow for large programs
    public static final AtomicLong idGenerator = new AtomicLong();
    
    private boolean isScheduled;
    
    public Activation(Scheduler scheduler) {
        this(0, scheduler);
    }

    public Activation(int result, Scheduler scheduler) {
        this.tempResult = result;
        this.scheduler = scheduler;
        this.isScheduled = false;
        this.id = idGenerator.getAndIncrement();
    }

    public int compareTo(Activation other){
        if (this.id < other.id){
            return -1;
        } else if (this.id > other.id){
            return +1;
        } else {
            return 0;
        }

        // return new Long(this.id).compareTo(new Long(other.id));
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
        try {
            if (continuation != null){
                if (scheduler.DEBUG_ON){
                    System.out.println("Activation: run()"); 
                }
                continuation.run();
                if (scheduler.DEBUG_ON){
                    System.out.println("Activation: completed run()"); 
                }
            }
            if (scheduler != null){
                scheduler.signalTaskDone(this);
            }
        } catch(RuntimeException e) {
            e.printStackTrace();
        } catch(AssertionError e) {
            e.printStackTrace();
        }
    }

    public boolean isScheduled(){
        return isScheduled;
    }

    public void setIsScheduled(){
        isScheduled = true;
    }

    public String toString(){
        String result = "<Activation: ";
        result += id;
        result += ", ";
        result += continuation;
        result += ", ";
        result += tempResult;
        result += ">";
        return result;
    }
}
