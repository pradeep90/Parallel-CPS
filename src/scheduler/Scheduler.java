package scheduler;

import java.util.*;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** 
 * Class for scheduling tasks obeying the happensBefore constraints
 * between them.
 *
 * @classinvariant !taskGraph.vertexSet().isEmpty() => getReadyNode() != null
 *
 * This is because there are our schedule is always a DAG - no
 * circular dependences.
 */
public class Scheduler {
    // @invariant will not be null
    public DirectedGraph<Activation, DefaultEdge> taskGraph;

    public ExecutorService executor;

    private boolean lastTaskDone;
    private static final int NUM_THREADS = 10;
    
    public Scheduler() {
        taskGraph = new DefaultDirectedGraph<Activation, DefaultEdge>(
            DefaultEdge.class);
        executor = Executors.newFixedThreadPool(NUM_THREADS);
        lastTaskDone = false;
    }

    public void addTask(Activation activation){
        taskGraph.addVertex(activation);
    }

    public void happensBefore(Activation from, Activation to){
        taskGraph.addEdge(from, to);

        assert taskGraph.containsEdge(from, to): "Must contain the edge";
    }

    /** 
     * Remove finishedTask from taskGraph.
     *
     * @precondition finishedTask must be in taskGraph
     * 
     * @postcondition taskGraph will not contain this particular
     * finishedTask (may contain others equal to finishedTask)
     */
    public void signalTaskDone(Activation finishedTask){
        boolean returnValue = taskGraph.removeVertex(finishedTask);
        assert returnValue == true: "finishedTask must have been in taskGraph";
        System.out.println("signalTaskDone: " + finishedTask); 
    }

    /** 
     * Set lastTaskDone to true.
     *
     * @precondition vertexSet has no Tasks; Ideally, no thread is
     * running.
     * 
     */
    public void signalLastTaskDone(){
        Set<Activation> nodes = taskGraph.vertexSet();
        System.out.println("nodes: " + nodes);

        // TODO: I believe this gives an error in the LastActivation
        // thread but it doesn't print anything on the console
        assert nodes.isEmpty(): "No tasks must remain";

        System.out.println("signalLastTaskDone: assert over"); 

        System.out.println("signalAllTasksDone"); 
        lastTaskDone = true;
    }

    /** 
     * Wait till a node becomes ready or till the last task is done.
     *
     * @precondition Either there is already a zero-degree node ready
     * or there is a running task.
     *
     * A running task will either produce new tasks (one of which will
     * have degree zero) or it will be the last task.
     *
     * @postcondition ready node is available or all tasks have
     * finished.
     */
    public void tryWaitForReadyNode(){
        Set<Activation> nodes = taskGraph.vertexSet();
        System.out.println("Last seen here - tryWaitForReadyNode"); 
        System.out.println("taskGraph: " + taskGraph);
        System.out.println("nodes.isEmpty(): " + nodes.isEmpty());
        System.out.println("!isLastTaskDone(): " + !isLastTaskDone());
        while (nodes.isEmpty() && !isLastTaskDone());
        System.out.println("out of tryWaitForReadyNode"); 
    }

    /**
     * @precondition there exists at least one ready node in taskGraph
     * 
     * @return a zero-degree vertex
     */
    public Activation getReadyNode(){
        assert !taskGraph.vertexSet().isEmpty(): "Vertex set can't be empty";

        Activation result = null;
        for (Activation currActivation : taskGraph.vertexSet()){
            if (taskGraph.inDegreeOf(currActivation) == 0){
                result = currActivation;
                break;
            }
        }
        return result;
    }

    /** 
     * Assuming that the last task to be executed will set
     * lastTaskDone to true.
     * 
     * @return true iff no tasks are in execution or remain to be scheduled 
     */
    public boolean isLastTaskDone(){
        // System.out.println("isLastTaskDone()"); 
        // System.out.println("lastTaskDone: " + lastTaskDone);
        if (lastTaskDone){
            assert taskGraph.vertexSet().isEmpty(): "No tasks must remain";
            System.out.println("assert in isLastTaskDone done"); 
        }

        return lastTaskDone;
    }

    /** 
     * Run a zero-degree node from taskGraph.
     *
     * @precondition there should be at least one zero-degree vertex
     * 
     * @postcondition a ready task would have been scheduled to run;
     * It would be removed from the graph.
     */
    public void runReadyTask(){
        Activation task = getReadyNode();
        System.out.println("taskGraph: " + taskGraph);
        System.out.println("task: " + task);
        // executor.submit(task);
        task.run();
        // taskGraph.removeVertex(task);
    }

    /** 
     * Guaranteed that there will be at least one zero-degree node
     * inside the loop cos at least one of the last task's subtasks
     * (e.g., the first function call) will be ready. Basically, no
     * circular happensBefore dependences in the schedule.
     */
    public void tryRunTasks(){
        System.out.println("hello"); 

        // Exit loop when last activation has finished
        while (!isLastTaskDone()){
            tryWaitForReadyNode();

            if (isLastTaskDone()){
                break;
            }

            System.out.println("ready node received"); 
            runReadyTask();
            System.out.println("task completed"); 
        }
        executor.shutdownNow();
    }

    public String toString(){
        String result = "<Scheduler: ";
        result += taskGraph;
        result += ">";
        return result;
    }
}
