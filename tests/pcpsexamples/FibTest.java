package pcpsexamples;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import scheduler.Activation;
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

    @Before
    public void setUp(){
        fib = new Fib();
        now = new Activation();
        later = new Activation();
    }
    
    @After
    public void tearDown(){
    }
    
    /**
     * Test method for {@link Fib#Fib()}.
     */
    @Test
    public final void testFib_BaseCase1(){
        Continuation current = new AbstractContinuation(now, later){
                @Override
                public void call(){
                    fib.fib(1, now, later);
                }
            };
        now.continuation = current;

        fib.scheduler.schedule(now);
        fib.scheduler.schedule(later);

        fib.scheduler.addEdge(now, later);
        
        fib.scheduler.tryRunTask();
        assertEquals(1, now.tempResult);
    }

    /**
     * Test method for {@link Fib#Fib()}.
     */
    @Test
    public final void testFib_BaseCase2(){
        Continuation current = new AbstractContinuation(now, later){
                @Override
                public void call(){
                    fib.fib(2, now, later);
                }
            };
        now.continuation = current;

        fib.scheduler.schedule(now);
        fib.scheduler.schedule(later);

        fib.scheduler.addEdge(now, later);
        fib.scheduler.tryRunTask();
        assertEquals(1, now.tempResult);
    }

    /**
     * Test method for {@link Fib#Fib()}.
     */
    @Test
    public final void testFib_RecursiveCase1(){
        Continuation current = new AbstractContinuation(now, later){
                @Override
                public void call(){
                    fib.fib(3, now, later);
                }
            };
        now.continuation = current;

        fib.scheduler.schedule(now);
        fib.scheduler.schedule(later);

        fib.scheduler.addEdge(now, later);

        fib.scheduler.tryRunTask();
        assertEquals(2, now.tempResult);
    }

    /**
     * Test method for {@link Fib#Fib()}.
     */
    @Test
    public final void testFib_RecursiveCase2(){
        Continuation current = new AbstractContinuation(now, later){
                @Override
                public void call(){
                    fib.fib(7, now, later);
                }
            };
        now.continuation = current;

        fib.scheduler.schedule(now);
        fib.scheduler.schedule(later);

        fib.scheduler.addEdge(now, later);

        fib.scheduler.tryRunTask();
        assertEquals(13, now.tempResult);
    }
}
