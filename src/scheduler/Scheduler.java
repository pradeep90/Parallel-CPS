package scheduler;

import java.util.LinkedList;
import java.util.Stack;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultDirectedGraph;

public class Scheduler {
    public Stack<Activation> tasks;
    public DirectedGraph<Activation, DefaultEdge> taskGraph;
    
    public Scheduler() {
        tasks = new Stack<Activation>();
        taskGraph = new DefaultDirectedGraph<Activation, DefaultEdge>(
            DefaultEdge.class);
    }

    public void schedule(Activation activation){
        tasks.push(activation);
        taskGraph.addVertex(activation);
    }

    public void addEdge(Activation from, Activation to){
        taskGraph.addEdge(from, to);
    }

    public Activation removeReadyNode(){
        Activation result;
        result = tasks.pop();
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
        result += tasks;
        result += ">";
        return result;
    }
}
