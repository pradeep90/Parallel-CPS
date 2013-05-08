package pcpsexamples;

import scheduler.AbstractContinuation;
import scheduler.Activation;
import scheduler.Continuation;
import scheduler.LastActivation;
import scheduler.Scheduler;

// VVIP: This is actually Quick Sort
/**
 * Merge Sort Example,
 *   Given an array, the task is to do merge sort.
 * 
 * The tasks are 
 * 		1. Partition the array into two sub arrays
 * 		2. Sort left subpart
 * 		3. Sort right subpart
 * 
 * Dependency is as.
 * 				current
 * 					|
 * 					V	
 * 				partition
 * 				 |     |
 * 				 V     V
 * 			Left Arr  Right Arr
 * 				 \    /
 * 				  \  /
 * 				  later
 * 	
 * @author mahesh.gupta
 *
 */
public class Merge {
    public Scheduler scheduler;

    private static final boolean NON_LINEAR_SCHEDULE = true;

    public Merge() {
        scheduler = new Scheduler();
    }

    void sortInParts(int[] arr, int low, int high, Activation now, Activation later) {

        if(low >= high){
            now.tempResult = -1;
            return;
			
        } else {
            Activation leftSort = new Activation(scheduler);
            Activation rightSort = new Activation(scheduler);

            split(arr, low, high, now);

            int pivot = now.tempResult;

            if (NON_LINEAR_SCHEDULE){
                // leftSort and rightSort are independent of each other
                leftSort.continuation = new ContinuationLeftSort(arr, low, pivot-1, this, leftSort, later);
            } else {
                // sequential way of doing it.
                leftSort.continuation = new ContinuationLeftSort(arr, low, pivot-1, this, leftSort, rightSort);
            }
            rightSort.continuation = new ContinuationRightSort(arr, pivot+1, high, this, rightSort, later);

            synchronized (scheduler.lock){
                scheduler.addTask(leftSort);
                scheduler.addTask(rightSort);

                scheduler.happensBefore(now, leftSort);
                scheduler.happensBefore(now, rightSort);

                if (NON_LINEAR_SCHEDULE){
                    scheduler.happensBefore(leftSort, later);
                } else {
                    scheduler.happensBefore(leftSort, rightSort);
                }

                scheduler.happensBefore(rightSort, later);			
            }
        }
    }

    private void split(int[] arr, int st, int en, Activation now) {
        int left;
        int right;
        int temp;

        left=st+1;
        right=en;

        while(true){
            while(left <=right && arr[left] <= arr[st]) { ++left; }
            while(right >=left && arr[right] > arr[st])  { --right; }

            if(left >=right){
                break;
            }
            if(left < right && arr[left] > arr[right]){
                temp = arr[left];
                arr[left] = arr[right];
                arr[right] = temp;

            }
        }
        temp = arr[st];
        arr[st] = arr[right];
        arr[right] = temp;

        now.tempResult = right;		
    }
}

class ContinuationLeftSort extends AbstractContinuation implements Continuation {
    int[] arr;
    int low;
    int high;
    Merge object;

    public ContinuationLeftSort(int[] arr, int low, int high, Merge merger, Activation now, Activation later) {
        super(now, later);
        this.arr = arr;
        this.low = low;
        this.high = high;
        this.object = merger;
    }

    @Override
    public void run() {
        object.sortInParts(arr, low, high, now, later);
    }

}

class ContinuationRightSort extends AbstractContinuation implements Continuation {
    int[] arr;
    int low;
    int high;
    Merge object;

    public ContinuationRightSort(int[] arr, int low, int high, Merge merger, Activation now, Activation later) {
        super(now, later);
        this.arr = arr;
        this.low = low;
        this.high = high;
        this.object = merger;
    }

    @Override
    public void run() {
        object.sortInParts(arr, low, high, now, later);
    }
}
