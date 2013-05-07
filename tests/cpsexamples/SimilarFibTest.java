package cpsexamples;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import pcpsexamples.MethodCall;
import pcpsexamples.Fib;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SimilarFibTest{
    SimilarFib similarFib;
    
    @Before
    public void setUp(){
        similarFib = new SimilarFib();
    }
    
    @After
    public void tearDown(){
    }
    
    /**
     * Test method for {@link SimilarFib#getFib()}.
     */
    @Test
    public final void testGetSimilarFib_BaseCases(){
        assertEquals(1, similarFib.getFib(1));
        assertEquals(1, similarFib.getFib(2));
    }

    /**
     * Test method for {@link SimilarFib#getFib()}.
     */
    @Test
    public final void testGetSimilarFib_RecursiveCase(){
        assertEquals(13, similarFib.getFib(7));
        assertEquals(21, similarFib.getFib(8));
        assertEquals(144, similarFib.getFib(12));
        assertEquals(10946, similarFib.getFib(21));
    }

    // // @Ignore
    // @Test
    // public final void testCollectRunningTime(){
    //     final int k = 10;

    //     System.out.println(""); 
    //     System.out.println("k: " + k);
    //     MethodCall methodCall1 = new MethodCall("SequentialRecursiveSimilarFib"){
    //             public void call(){
    //                 SimilarFib.getRecursiveFib(k);
    //             }
    //         };
    //     methodCall1.measure();

    //     System.out.println("k: " + k);
    //     MethodCall methodCall11 = new MethodCall("SequentialRecursiveFib"){
    //             public void call(){
    //                 pcpsexamples.Fib.getRecursiveFib(k);
    //             }
    //         };
    //     methodCall11.measure();

    //     MethodCall methodCall2 = new MethodCall("CPSFib"){
    //             public void call(){
    //                 SimilarFib.getFib(k);
    //             }
    //         };
    //     methodCall2.measure();

    //     MethodCall methodCall22 = new MethodCall("ParallelCPSFib"){
    //             public void call(){
    //                 pcpsexamples.Fib.getFib(k);
    //             }
    //         };
    //     methodCall22.measure();
    // }
}
