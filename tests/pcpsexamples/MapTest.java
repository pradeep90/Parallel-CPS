package pcpsexamples;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import scheduler.AbstractContinuation;
import scheduler.Activation;
import scheduler.LastActivation;

public class MapTest {
    Map map;
    Activation now;
    Activation later;
    boolean debug_test = false;
    static int testCase_Count;
    
	
    @Before
    public void setup(){
        map = new Map();
        now = new Activation(map.scheduler);
        later = new LastActivation(map.scheduler);
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
    public void testReflexion(){
        int[] arr1 = {1, 1};
        applyMap(arr1);
		
        assertEquals(arr1[0], arr1[1]);		
    }
	
    @Test
    public void testTransitivity(){
        int[] arr1 = {1, 2, 3};
        applyMap(arr1);
		
        assertEquals((arr1[0]+arr1[1])%5, arr1[2]);
		
    }
	
    // TODO: Check why this is cupping
    // @Test
    // public void testSymmetric(){
    //     int[] arr1 = {1, 2};
    //     int[] arr2 = {2, 1};
		
    //     applyMap(arr1);
    //     applyMap(arr2);
	
    //     assertEquals(arr1[0], arr2[1]);
    //     assertEquals(arr1[1], arr2[0]);
    // }
	
    public void applyMap(final int[] arr){
		
        now.continuation = new AbstractContinuation(now, later) {
			
                @Override
                public void run() {
                    map.applyFunction(arr, now, later);
				
                }
            };
	
        map.scheduler.addTask(now);
        map.scheduler.addTask(later);
	
        map.scheduler.happensBefore(now, later);
        long st = System.nanoTime();	
        map.scheduler.tryRunTasks(now);
        long en = System.nanoTime();
		
        ++testCase_Count;
        if(debug_test){
            System.out.println("Test Case #"+testCase_Count+" size = "+arr.length +" time = "+(en-st));
        }
    }
	

}
