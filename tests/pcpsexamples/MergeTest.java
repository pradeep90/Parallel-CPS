package pcpsexamples;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import scheduler.AbstractContinuation;
import scheduler.Activation;
import scheduler.Continuation;
import scheduler.LastActivation;

public class MergeTest {

    Merge object;
    Activation now;
    Activation later;
    boolean debug_test = false;
    static int testCase_Count;
    @Before
    public void setUp(){
    	object = new Merge();
    	
    	now = new Activation(object.scheduler);
    	later = new LastActivation(object.scheduler);
    	    	
    	if(debug_test){
            System.out.println("SetUp Done, starting testes");
    	}
    	testCase_Count = 0;
    }
    
    @After
    public void tearDown(){
    	if(debug_test){
            System.out.println("tear down phase. all tests executed.");
    	}
    }
    
    
    @Test
    public void testSorting_Size_1_Array() {
        int[] arr = {1};
        testCase(arr);
        assertEquals(isSorted(arr), true);
		
    }
	
    @Test
    public void testSorting_Size_2_Sorted_Array(){
        int[] arr = {1, 2};
        testCase(arr);
        assertEquals(isSorted(arr), true);		
    }
	
    @Test
    public void testSorting_Size_2_UnSorted_Array(){
        int[] arr = {2, 1};
        testCase(arr);
        assertEquals(isSorted(arr), true);
    }
	
    @Test
    public void testSorting_Size_5_Sorted_Array_NoDuplicates(){
        int[] arr = {2, 25, 38, 437, 2458};
        testCase(arr);
        assertEquals(isSorted(arr), true);
    }
	
    @Test
    public void testSorting_Size_5_Sorted_Array_Duplicates(){
        int[] arr = {2, 25, 25, 437, 3458};
        testCase(arr);
        assertEquals(isSorted(arr), true);
    }
	
    @Test
    public void testSorting_Size_5_UnSortedArray_Duplicates(){
        int[] arr = {20, 15, 10, 13, 15};
        testCase(arr);
        assertEquals(isSorted(arr), true);
    }
	
    @Test
    public void testSorting_Size_10000_RandomArray(){
        Random rand = new Random();
        int[] arr = new int[10000];
        for(int i =0 ; i<arr.length ; ++i){
            arr[i] = rand.nextInt();
        }
        testCase(arr);
        assertEquals(isSorted(arr), true);
    }

    public void testCase(final int[] arr){
        Continuation cont = new AbstractContinuation(now, later) {
			
                @Override
                public void run() {
                    object.sortInParts(arr, 0, arr.length - 1, now, later);				
                }
            };
		
        object.scheduler.addTask(now);
        object.scheduler.addTask(later);
        now.continuation = cont;
        object.scheduler.happensBefore(now, later);
        
        long stTime = System.currentTimeMillis();
        object.scheduler.tryRunTasks(now);		
        long enTime = System.currentTimeMillis();
        
        ++testCase_Count;
        if(debug_test){        	
            System.out.println("Test Case #"+testCase_Count+" size = "+arr.length +" time = "+(enTime-stTime));
        }
    }
	
    public boolean isSorted(int[] arr){
        if(arr == null){	return false;}
		
        if(arr.length == 0 || arr.length == 1) {	return true;}
		
        for(int i=0 ; i<arr.length-1 ; ++i){
            if(arr[i] > arr[i+1]) {	return false;}			
        }
        return true;
    }

}
