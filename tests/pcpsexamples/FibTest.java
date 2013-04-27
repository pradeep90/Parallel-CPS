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
        
        fib.scheduler.tryRunTasks();
        assertEquals(1, now.tempResult);
    }

    /**
     * Test method for {@link Fib#Fib()}.
     */
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
        fib.scheduler.tryRunTasks();
        assertEquals(1, now.tempResult);
    }

    /**
     * Test method for {@link Fib#Fib()}.
     */
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

        fib.scheduler.tryRunTasks();
        assertEquals(2, now.tempResult);
    }

    /**
     * Test method for {@link Fib#Fib()}.
     */
    @Test
    public final void testFib_RecursiveCase2(){
        System.out.println("testFib_RecursiveCase2"); 
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

        fib.scheduler.tryRunTasks();
        assertEquals(13, now.tempResult);
    }
}
