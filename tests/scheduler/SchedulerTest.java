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
        Activation activation1 = new Activation(1, scheduler);
        Activation activation2 = new Activation(2, scheduler);
        Activation activation3 = new Activation(3, scheduler);
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

    @Test
    public final void testDirectedNeighborIndex_ReflectsChanges(){
        Activation activation1 = new Activation(1, scheduler);
        Activation activation2 = new Activation(2, scheduler);
        Activation activation3 = new Activation(3, scheduler);
        Set vertexSet = scheduler.taskGraph.vertexSet();

        scheduler.taskGraph.addVertex(activation1);
        scheduler.taskGraph.addVertex(activation2);
        scheduler.taskGraph.addVertex(activation3);

        assertTrue(scheduler.neighbourIndex.predecessorListOf(activation1)
                   .isEmpty()); 

        scheduler.taskGraph.addEdge(activation1, activation2);

        assertEquals(
            1,
            scheduler.neighbourIndex.predecessorListOf(activation2).size());
        
        scheduler.taskGraph.removeVertex(activation1);
        assertTrue(scheduler.neighbourIndex.predecessorListOf(activation2)
                   .isEmpty()); 
    }
}
