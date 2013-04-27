package scheduler;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SchedulerTest{
    Scheduler scheduler;
    
    @Before
    public void setUp(){
        scheduler = new Scheduler();
    }
    
    @After
    public void tearDown(){
    }
    
    @Test
    public final void testVertexSet_ReflectsChanges(){
        Activation activation1 = new Activation(1);
        Activation activation2 = new Activation(2);
        Activation activation3 = new Activation(3);
        Set vertexSet = scheduler.taskGraph.vertexSet();

        assertTrue(vertexSet.isEmpty()); 

        scheduler.taskGraph.addVertex(activation1);
        assertEquals(vertexSet.size(), 1); 
        
        scheduler.taskGraph.addVertex(activation2);
        scheduler.taskGraph.addVertex(activation3);
        assertEquals(vertexSet.size(), 3); 

        scheduler.taskGraph.removeVertex(activation3);
        assertEquals(vertexSet.size(), 2); 
    }
}
