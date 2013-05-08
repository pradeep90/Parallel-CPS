package pcpsexamples;

import java.util.Random;

import scheduler.AbstractContinuation;
import scheduler.Activation;
import scheduler.LastActivation;
import scheduler.Scheduler;

/**
 * Map Sort Example,
 *   Given an array, apply the map function to modify value of each element
 * 
 * The tasks are 
 * 		1. Partition the array into two sub arrays
 * 		2. Apply Map to left subpart
 * 		3. Apply Map to right subpart
 * 
 * Dependency is as.
 * 				current
 * 					|
 * 					V	
 * 				p a r t i t i o n
 * 				 |      |       |
 * 				 V      V       V
 * 			Left Arr apply(mid) Right Arr
 * 				 \       |    /
 * 				  \      |   /
 * 				    l a t e r
 * 	
 * @author mahesh.gupta
 *
 */
public class Map {
    public Scheduler scheduler;
    private final Random rand = new Random(System.nanoTime());
	
    private static final boolean NON_LINEAR_SCHEDULE = false;

    public Map() {
        scheduler = new Scheduler();
    }

    public void applyFunction(int[] arr, Activation now, Activation later){
        applyFunctionByPart(arr, 0, arr.length-1 , now, later);
    }
	
    void applyFunctionByPart(int[] arr, int st, int en, Activation now, Activation later) {
        if(st > en){ now.tempResult = -1;	return; }
        if(st == en){ now.tempResult = -1; arr[st] = getValue(arr[st]); return; }
		
        else {
            int mid = (st + en ) / 2;
            arr[mid] = getValue(arr[mid]);
			
            Activation leftFunction = new Activation(scheduler);
            Activation rightFunction = new Activation(scheduler);
			
            if(NON_LINEAR_SCHEDULE){
                leftFunction.continuation = new LeftFunctionContinuation(arr, st, mid-1, this, leftFunction, later);				
            }else {
                leftFunction.continuation = new LeftFunctionContinuation(arr, st, mid-1, this, leftFunction, rightFunction);
            }
            rightFunction.continuation = new RightFunctionContinuation(arr, mid+1, en, this, rightFunction, later);
			
			
            synchronized (scheduler.lock){
                scheduler.addTask(leftFunction);
                scheduler.addTask(rightFunction);
				
                scheduler.happensBefore(now, leftFunction);
                scheduler.happensBefore(now, rightFunction);
				
                if(NON_LINEAR_SCHEDULE){
                    scheduler.happensBefore(leftFunction, later);
                }else {
                    scheduler.happensBefore(leftFunction, rightFunction);
                }
                scheduler.happensBefore(rightFunction, later);
            }			
        }
		
    }

    public int getValue(int key){		
        // constatnt function
        // return (key+737)*7/441;
		
        //reflexive and transitive function
        return  (key*7)%5;
    }

}

class LeftFunctionContinuation extends AbstractContinuation {
    int[] arr;
    int st;
    int en;
    Map object;
	
    public LeftFunctionContinuation(int[] arr, int st, int en, Map mp, Activation now, Activation later) {
        super(now, later);
        this.arr = arr;
        this.st = st;
        this.en = en;
        this.object = mp;
    }

    @Override
    public void run() {
        object.applyFunctionByPart(arr, st, en, now, later);		
    }
	
}

class RightFunctionContinuation extends AbstractContinuation {
    int[] arr;
    int st;
    int en;
    Map object;
	
    public RightFunctionContinuation(int[] arr, int st, int en, Map mp, Activation now, Activation later) {
        super(now, later);
        this.arr = arr;
        this.st = st;
        this.en = en;
        this.object = mp;
    }

    @Override
    public void run() {
        object.applyFunctionByPart(arr, st, en, now, later);		
    }
	
}
