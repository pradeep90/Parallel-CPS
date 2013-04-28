package scheduler;

import org.jgrapht.alg.*;
import org.jgrapht.*;
import org.jgrapht.event.*;
import org.jgrapht.graph.*;

public class ReadyNodeListener
        extends DirectedNeighborIndex<Activation, DefaultEdge> {
    Scheduler scheduler;
    DirectedGraph<Activation, DefaultEdge> graph;
    
    public ReadyNodeListener(DirectedGraph<Activation, DefaultEdge> g,
                             Scheduler scheduler) {
        super(g);
        this.graph = g;
        this.scheduler = scheduler;
    }

    /** 
     * If the destination node's degree becomes zero, notify scheduler.
     */
    @Override
        public void edgeRemoved(GraphEdgeChangeEvent<Activation, DefaultEdge> e){
        super.edgeRemoved(e);
        Activation target = this.graph.getEdgeTarget(e.getEdge());
        if (this.predecessorListOf(target).isEmpty()){
            if (scheduler.DEBUG_ON){
                System.out.println("edgeRemoved: Zero degree ready node"); 
                System.out.println("target: " + target);
            }
            scheduler.signalSomeNodeIsReady(target);
        }
    }
}
