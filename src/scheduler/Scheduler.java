package scheduler;

import java.util.LinkedList;
import java.util.Stack;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultDirectedGraph;

public class Scheduler {
    public DirectedGraph<Activation, DefaultEdge> taskGraph;
    
    public Scheduler() {
        taskGraph = new DefaultDirectedGraph<Activation, DefaultEdge>(
            DefaultEdge.class);
    }

    public void addTask(Activation activation){
        taskGraph.addVertex(activation);
    }

    public void happensBefore(Activation from, Activation to){
        taskGraph.addEdge(from, to);
    }

    /** 
     * Remove a zero-degree node from taskGraph.
     *
     * Precondition: there should be at least one zero-degree vertex
     * 
     * Since there is only one scheduler, there won't be any
     * concurrent remove operations.
     *
     * Postcondition: result will be a zero-degree vertex
     * 
     * @return a zero-degree vertex
     */
    public Activation removeReadyNode(){
        Activation result = null;
        for (Activation currActivation : taskGraph.vertexSet()){
            if (taskGraph.inDegreeOf(currActivation) == 0){
                result = currActivation;
                break;
            }
        }
        return result;
    }

    public boolean areNodesRemaining(){
        return !taskGraph.vertexSet().isEmpty();
    }

    public void tryRunTask(){
        while (areNodesRemaining()){
            Activation currTask = removeReadyNode();
            currTask.run();
            taskGraph.removeVertex(currTask);
        }
    }

    public String toString(){
        String result = "<Scheduler: ";
        result += taskGraph;
        result += ">";
        return result;
    }
}
