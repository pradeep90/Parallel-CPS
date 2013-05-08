package pcpsexamples;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import scheduler.Activation;
import scheduler.LastActivation;
import scheduler.AbstractContinuation;
import scheduler.Continuation;
import scheduler.Scheduler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FibTest{
    Fib fib;
    Activation now;
    Activation later;
    public static final int NUM_ITERS = 50;
    
    @Before
    public void setUp(){
        fib = new Fib();
        now = new Activation(fib.scheduler);
        // later = new Activation();
        later = new LastActivation(fib.scheduler);
    }
    
    @After
    public void tearDown(){
    }
    
    /**
     * Test method for {@link Fib#Fib()}.
     */
    // @Ignore
    @Test
    public final void testFib_BaseCase1(){
        System.out.println("testFib_BaseCase1"); 
        Continuation current = new AbstractContinuation(now, later){
                @Override
                public void run(){
                    fib.fib(1, now, later);
                }
            };
        now.continuation = current;

        fib.scheduler.addTask(now);
        fib.scheduler.addTask(later);

        fib.scheduler.happensBefore(now, later);
        
        fib.scheduler.tryRunTasks(now);
        assertEquals(1, now.tempResult);
    }

    /**
     * Test method for {@link Fib#Fib()}.
     */
    // @Ignore
    @Test
    public final void testFib_BaseCase2(){
        System.out.println("testFib_BaseCase2"); 
        Continuation current = new AbstractContinuation(now, later){
                @Override
                public void run(){
                    fib.fib(2, now, later);
                }
            };
        now.continuation = current;

        fib.scheduler.addTask(now);
        fib.scheduler.addTask(later);

        fib.scheduler.happensBefore(now, later);
        fib.scheduler.tryRunTasks(now);
        assertEquals(1, now.tempResult);
    }

    /**
     * Test method for {@link Fib#Fib()}.
     */
    // @Ignore
    @Test
    public final void testFib_RecursiveCase1(){
        System.out.println("testFib_RecursiveCase1"); 
        Continuation current = new AbstractContinuation(now, later){
                @Override
                public void run(){
                    fib.fib(3, now, later);
                }
            };
        now.continuation = current;

        fib.scheduler.addTask(now);
        fib.scheduler.addTask(later);

        fib.scheduler.happensBefore(now, later);

        fib.scheduler.tryRunTasks(now);
        assertEquals(2, now.tempResult);
    }

    /**
     * Test method for {@link Fib#Fib()}.
     */
    // @Ignore
    @Test
    public final void testFib_RecursiveCase2(){
        System.out.println("testFib_RecursiveCase2"); 

        long lStartTime = System.currentTimeMillis();

        Continuation current = new AbstractContinuation(now, later){
                @Override
                public void run(){
                    fib.fib(7, now, later);
                }
            };
        now.continuation = current;

        fib.scheduler.addTask(now);
        fib.scheduler.addTask(later);

        fib.scheduler.happensBefore(now, later);

        fib.scheduler.tryRunTasks(now);

        long lEndTime = System.currentTimeMillis();
   
        long difference = lEndTime - lStartTime;
   
        System.out.println("Elapsed time: " + difference + " ms");

        assertEquals(13, now.tempResult);
    }

    /**
     * Test method for {@link Fib#Fib()}.
     */
    // @Ignore
    @Test
    public final void testFib_RecursiveCase3(){
        System.out.println("testFib_RecursiveCase3"); 

        long lStartTime = System.currentTimeMillis();

        Continuation current = new AbstractContinuation(now, later){
                @Override
                public void run(){
                    fib.fib(11, now, later);
                }
            };
        now.continuation = current;

        fib.scheduler.addTask(now);
        fib.scheduler.addTask(later);

        fib.scheduler.happensBefore(now, later);

        fib.scheduler.tryRunTasks(now);

        long lEndTime = System.currentTimeMillis();
   
        long difference = lEndTime - lStartTime;
   
        System.out.println("Elapsed time: " + difference + " ms");

        assertEquals(89, now.tempResult);
    }

    /**
     * Test method for {@link Fib#Fib()}.
     */
    // @Ignore
    @Test
    public final void testFib_RecursiveCase4(){
        System.out.println("testFib_RecursiveCase4"); 

        long lStartTime = System.currentTimeMillis();

        Continuation current = new AbstractContinuation(now, later){
                @Override
                public void run(){
                    fib.fib(15, now, later);
                }
            };
        now.continuation = current;

        fib.scheduler.addTask(now);
        fib.scheduler.addTask(later);

        fib.scheduler.happensBefore(now, later);

        fib.scheduler.tryRunTasks(now);

        long lEndTime = System.currentTimeMillis();
   
        long difference = lEndTime - lStartTime;
   
        System.out.println("Elapsed time: " + difference + " ms");

        assertEquals(610, now.tempResult);
    }

    /**
     * Test method for {@link Fib#Fib()}.
     */
    // @Ignore
    @Test
    public final void testFib_RecursiveCase5(){
        System.out.println("testFib_RecursiveCase5"); 

        Continuation current = new AbstractContinuation(now, later){
                @Override
                public void run(){
                    fib.fib(21, now, later);
                }
            };

        long lStartTime = System.currentTimeMillis();
        for (int i = 0; i < 1; i++){
            runFib(current);
        }
        
        long lEndTime = System.currentTimeMillis();
   
        long difference = lEndTime - lStartTime;
   
        System.out.println("Elapsed time: " + difference + " ms");

        assertEquals(10946, now.tempResult);
    }

    public void runFib(Continuation current){
        now.continuation = current;

        fib.scheduler.addTask(now);
        fib.scheduler.addTask(later);

        fib.scheduler.happensBefore(now, later);

        fib.scheduler.tryRunTasks(now);
    }


    /**
     * Test method for {@link Fib#getFib()}.
     */
    // @Ignore
    @Test
    public final void testGetFib(){
        assertEquals(10946, Fib.getFib(21));
    }

    /**
     * Test method for {@link Fib#getRecursiveFib()}.
     */
    // @Ignore
    @Test
    public final void testGetRecursiveFib(){
        assertEquals(10946, Fib.getRecursiveFib(21));
    }
}
