package scheduler;

import static org.junit.Assert.assertEquals;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;
import org.junit.Before;
import org.junit.Test;

// import scheduler.Activation;
// import scheduler.Scheduler;

public class GraphTransitivityRemoverTest {
    ListenableDirectedGraph<Activation, DefaultEdge> taskGraph;
    ListenableDirectedGraph<Activation, DefaultEdge> expectedGraph;
    ListenableDirectedGraph<Activation, DefaultEdge> optimizedGraph;
    GraphTransitivityRemover graph_expt;
	
    public Scheduler scheduler = new Scheduler();
	
    @Before
    public void setup(){
        taskGraph = new ListenableDirectedGraph<Activation, DefaultEdge>(DefaultEdge.class);
        expectedGraph = new ListenableDirectedGraph<Activation, DefaultEdge>(DefaultEdge.class);
        graph_expt = new GraphTransitivityRemover();
    }
	
    @Test
    public void testForSimpleGraph(){
        Activation act1 = new Activation(scheduler); 
        Activation act2 = new Activation(scheduler);
		
        taskGraph.addVertex(act1);	expectedGraph.addVertex(act1);
        taskGraph.addVertex(act2);	expectedGraph.addVertex(act2);
		
        taskGraph.addEdge(act1, act2);	expectedGraph.addEdge(act1, act2);
		
        optimizedGraph = graph_expt.graph_to_single_source_longest_path(taskGraph);

        assertEquals(optimizedGraph.toString(), expectedGraph.toString());
		
    }
	
    @Test
    public void testForLoop(){
        Activation act1 = new Activation(scheduler); 
        Activation act2 = new Activation(scheduler);
		
        taskGraph.addVertex(act1);		expectedGraph.addVertex(act1);
        taskGraph.addVertex(act2);		expectedGraph.addVertex(act2);
		
        taskGraph.addEdge(act1, act2);	expectedGraph.addEdge(act1, act2);
        // cycle
        taskGraph.addEdge(act1, act1);
        taskGraph.addEdge(act2, act2);
		
        optimizedGraph = graph_expt.graph_to_single_source_longest_path(taskGraph);
		
        assertEquals(optimizedGraph.toString(), expectedGraph.toString());
    }
	
    @Test
    public void testForSymmetric(){
        Activation act1 = new Activation(scheduler); 
        Activation act2 = new Activation(scheduler);
		
        taskGraph.addVertex(act1);		expectedGraph.addVertex(act1);
        taskGraph.addVertex(act2);		expectedGraph.addVertex(act2);
		
        taskGraph.addEdge(act1, act2);	expectedGraph.addEdge(act1, act2);
        taskGraph.addEdge(act2, act1);
		

        optimizedGraph = graph_expt.graph_to_single_source_longest_path(taskGraph);
        assertEquals(optimizedGraph.toString(), expectedGraph.toString());
    }
	
    @Test
    public void testForTransitive(){
        Activation act1 = new Activation(scheduler); 
        Activation act2 = new Activation(scheduler);
        Activation act3 = new Activation(scheduler);
		
        taskGraph.addVertex(act1);			expectedGraph.addVertex(act1);
        taskGraph.addVertex(act2);			expectedGraph.addVertex(act2);
        taskGraph.addVertex(act3);			expectedGraph.addVertex(act3);
		
        taskGraph.addEdge(act1, act2);		expectedGraph.addEdge(act1, act2);
        taskGraph.addEdge(act2, act3);		expectedGraph.addEdge(act2, act3);
        taskGraph.addEdge(act1, act3);
		

        optimizedGraph = graph_expt.graph_to_single_source_longest_path(taskGraph);
		
        assertEquals(optimizedGraph.toString(), expectedGraph.toString());
    }
	
    /*
     * This type of optimisation is not required
     */
	
    @Test
    public void testForCycle(){
        Activation act1 = new Activation(scheduler); 
        Activation act2 = new Activation(scheduler);
        Activation act3 = new Activation(scheduler);
        Activation act4 = new Activation(scheduler);
		
        taskGraph.addVertex(act1);			expectedGraph.addVertex(act1);
        taskGraph.addVertex(act2);			expectedGraph.addVertex(act2);
        taskGraph.addVertex(act3);			expectedGraph.addVertex(act3);
        taskGraph.addVertex(act4);			expectedGraph.addVertex(act4);
		
        taskGraph.addEdge(act1, act2);		expectedGraph.addEdge(act1, act2);
        taskGraph.addEdge(act2, act3);		expectedGraph.addEdge(act2, act3);
		 
        taskGraph.addEdge(act3, act4);		expectedGraph.addEdge(act3, act4);
        taskGraph.addEdge(act1, act4);
		
        optimizedGraph = graph_expt.graph_to_single_source_longest_path(taskGraph);
        assertEquals(optimizedGraph.toString(), expectedGraph.toString());
    }

    @Test
    public void testGraphComplicated1(){
        Activation act1 = new Activation(scheduler); 
        Activation act2 = new Activation(scheduler);
        Activation act3 = new Activation(scheduler);
        Activation act4 = new Activation(scheduler);
		
        Activation act5 = new Activation(scheduler); 
        Activation act6 = new Activation(scheduler);
        Activation act7 = new Activation(scheduler);
		
        taskGraph.addVertex(act1);			expectedGraph.addVertex(act1);
        taskGraph.addVertex(act2);			expectedGraph.addVertex(act2);
        taskGraph.addVertex(act3);			expectedGraph.addVertex(act3);
        taskGraph.addVertex(act4);			expectedGraph.addVertex(act4);
        taskGraph.addVertex(act5);			expectedGraph.addVertex(act5);
        taskGraph.addVertex(act6);			expectedGraph.addVertex(act6);
        taskGraph.addVertex(act7);			expectedGraph.addVertex(act7);
		
        taskGraph.addEdge(act1, act2);		expectedGraph.addEdge(act1, act2);
        taskGraph.addEdge(act1, act3);		expectedGraph.addEdge(act1, act3);
        taskGraph.addEdge(act2, act4);		expectedGraph.addEdge(act2, act4);
        taskGraph.addEdge(act2, act5);	
		
        taskGraph.addEdge(act3, act6);		expectedGraph.addEdge(act3, act6);
        taskGraph.addEdge(act4, act5);		expectedGraph.addEdge(act4, act5);
        taskGraph.addEdge(act4, act7);
        taskGraph.addEdge(act5, act7);		expectedGraph.addEdge(act5, act7);
		
        optimizedGraph = graph_expt.graph_to_single_source_longest_path(taskGraph);

        assertEquals(optimizedGraph.toString(), expectedGraph.toString());
    }
}
