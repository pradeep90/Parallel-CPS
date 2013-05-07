package scheduler;

import java.util.*;
import java.util.Map.Entry;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.ListenableDirectedGraph;
import org.jgrapht.alg.DirectedNeighborIndex;

import java.util.concurrent.atomic.AtomicInteger;

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/** 
 * Class for scheduling tasks obeying the happensBefore constraints
 * between them.
 *
 * TODO: This is wrong now
 * @classinvariant !taskGraph.vertexSet().isEmpty() => getReadyNode() != null
 *
 * This is because our schedule is always a DAG - no circular
 * dependences.
 *
 * ASSUMPTION: Initially, there is only one ready node (i.e., the
 * first activation)
 *
 * Ready node: Unscheduled zero-degree node
 */
public class Scheduler {
    // @invariant will not be null
    public ListenableDirectedGraph<Activation, DefaultEdge> taskGraph;
    public DirectedNeighborIndex<Activation, DefaultEdge> neighbourIndex;

    ConcurrentSkipListSet<Activation> readyNodes;
    
    public final Object lock = new Object();

    public final Lock readyNodeAvailableLock = new ReentrantLock();
    public final Condition readyNodeAvailableCondition =
            readyNodeAvailableLock.newCondition();
    
    public ExecutorService executor;

    private boolean lastTaskDone;
    public static final boolean DEBUG_ON = false;

    public Random randomGenerator;

    long totalWaitingTime = 0;

    // All the tasks in the Scheduler graph (backed by the graph)
    Set<Activation> nodes;
    
    /**
     * numReadyNodes may be incremented by concurrent threads (as
     * happensBefore edges are removed), but it is decremented only by
     * this scheduler, i.e., it can't happen concurrently.
     * So, numReadyNodes > 0 => ready node is available.
     */
    private AtomicInteger numReadyNodes;
    
    private int numActiveTasks = 0;
    private static final int NUM_THREADS = 10;
    
    public Scheduler() {
        taskGraph = new ListenableDirectedGraph<Activation, DefaultEdge>(
            DefaultEdge.class);

        neighbourIndex = new ReadyNodeListener(taskGraph, this);
        taskGraph.addGraphListener(neighbourIndex);

        nodes = taskGraph.vertexSet();
        
        readyNodes = new ConcurrentSkipListSet<Activation>();
        
        // Initially, NOW Activation is ready
        numReadyNodes = new AtomicInteger(1);
        
        executor = Executors.newFixedThreadPool(NUM_THREADS);
        lastTaskDone = false;

        randomGenerator = new Random();
    }

    public void scheduleSubtasks(
        Set<Activation> tasks,
        Set<Entry<Activation, Activation>> happensBeforeConstraints){

        synchronized (lock) {
            for (Activation task : tasks){
                taskGraph.addVertex(task);
            }

            for (Entry<Activation, Activation> edge : happensBeforeConstraints){
                Activation from = edge.getKey();
                Activation to = edge.getValue();
                taskGraph.addEdge(from, to);
                assert taskGraph.containsEdge(from, to): "Must contain the edge";
            }
        }
    }

    public void addTask(Activation activation){
        synchronized (lock){
            taskGraph.addVertex(activation);
        }
    }

    public void happensBefore(Activation from, Activation to){
        synchronized (lock){
            taskGraph.addEdge(from, to);
            assert taskGraph.containsEdge(from, to): "Must contain the edge";
        }
    }

    public void signalSomeNodeIsReady(Activation givenReadyNode){
        numReadyNodes.incrementAndGet();
        boolean wasNotAlreadyPresent = readyNodes.add(givenReadyNode);
        readyNodeAvailableLock.lock();
        try {
            // TODO: Should I always signal or should I do it only
            // when readyNodes was empty?
            readyNodeAvailableCondition.signal();
        } finally {
            readyNodeAvailableLock.unlock();
        }

        assert wasNotAlreadyPresent: "givenReadyNode must not already be in the set.";
        // assert false: "checking";
        
        if (DEBUG_ON){
            System.out.println("signalSomeNodeIsReady: numReadyNodes.get(): " + numReadyNodes.get());
        }
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
        boolean returnValue;
        synchronized (lock){
            returnValue = taskGraph.removeVertex(finishedTask);
        }

        assert returnValue == true: "finishedTask must have been in taskGraph";

        if (DEBUG_ON){
            System.out.println("signalTaskDone: " + finishedTask); 
        }

        numActiveTasks--;
    }

    /** 
     * Set lastTaskDone to true.
     *
     * @precondition vertexSet has no Tasks; Ideally, no thread is
     * running.
     * 
     */
    public void signalLastTaskDone(){
        if (DEBUG_ON){
            System.out.println("nodes: " + nodes);
        }

        // TODO: I believe this gives an error in the LastActivation
        // thread but it doesn't print anything on the console

        assert nodes.isEmpty(): "No tasks must remain";

        if (DEBUG_ON){
            System.out.println("signalLastTaskDone: assert over"); 
            System.out.println("signalAllTasksDone"); 
        }

        lastTaskDone = true;
        readyNodeAvailableLock.lock();
        try {
            // TODO: Should I always signal or should I do it only
            // when readyNodes was empty?
            readyNodeAvailableCondition.signal();
        } finally {
            readyNodeAvailableLock.unlock();
        }
    }

    /** 
     * Wait till a ready node is available or till the last task is
     * done.
     *
     * @precondition either there is ALREADY a ready node or there is
     * a running task or all tasks are over.
     *
     * If there is no pre-existing ready node, the running task would
     * either be the last one or would create subtasks eventually (in
     * that case, wait).
     *
     * @postcondition ready node is now available or all tasks have
     * finished.
     */
    public void tryWaitForReadyNode(){
        // TODO: In the future, do a non-blocking wait or something
        // while (numReadyNodes.get() == 0 && !isLastTaskDone()){
        long startTime = System.nanoTime();

        readyNodeAvailableLock.lock();
        
        try {
            while (readyNodes.isEmpty() && !isLastTaskDone()){
                readyNodeAvailableCondition.await();

                if (DEBUG_ON){
                    // System.out.println("tryWaitForReadyNode: still in loop"); 
                    // System.out.println("tryWaitForReadyNode: numReadyNodes.get(): "
                    //                    + numReadyNodes.get());
                    // System.out.println("!isLastTaskDone(): " + !isLastTaskDone());
                }
            }
        } catch(InterruptedException e){
            e.printStackTrace();
        } finally {
            readyNodeAvailableLock.unlock();
        }

        totalWaitingTime += System.nanoTime() - startTime;
        
        if (DEBUG_ON){
            System.out.println("out of tryWaitForReadyNode"); 
        }
    }

    /**
     * @precondition there exists at least one ready node
     * 
     * @return a ready node
     * 
     */
    public Activation getReadyNode(){
        Activation result;
        assert !readyNodes.isEmpty(): "must be at least one ready node";
        result = readyNodes.iterator().next();
        assert !result.isScheduled(): "ready node must be unscheduled";

        assert result != null: "ready node must exist";
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
            assert nodes.isEmpty(): "No tasks must remain";
            if (DEBUG_ON){
                System.out.println("assert in isLastTaskDone done"); 
            }
        }

        return lastTaskDone;
    }

    /** 
     * Run a ready node from taskGraph.
     *
     * @precondition there should be at least one ready node
     * 
     * @postcondition a ready task would have been scheduled to run
     */
    public void runReadyTask(){
        if (DEBUG_ON){
            // System.out.println("readyNodes: " + readyNodes);
        }

        Activation task = getReadyNode();
        // System.out.println("taskGraph: " + taskGraph);


        task.setIsScheduled();
        readyNodes.remove(task);
        numReadyNodes.decrementAndGet();
        numActiveTasks++;
        // System.out.println("numActiveTasks: " + numActiveTasks);

        if (DEBUG_ON){
            System.out.println("runReadyTask: task: " + task);
            System.out.println("runReadyTask: numReadyNodes.get(): " + numReadyNodes.get());
            System.out.println("numActiveTasks: " + numActiveTasks);
        }

        // TODO: 
        executor.submit(task);
        // task.run();
    }

    /**
     * @precondition firstTask is a ready node
     * 
     * Guaranteed that there will be at least one zero-degree node
     * inside the loop cos at least one of the last task's subtasks
     * (e.g., the first function call) will be ready. Basically, no
     * circular happensBefore dependences in the schedule.
     */
    public void tryRunTasks(Activation firstTask){
        if (DEBUG_ON){
            System.out.println("hello"); 
        }

        readyNodes.add(firstTask);
        
        // Exit loop when last activation has finished
        while (!isLastTaskDone()){
            tryWaitForReadyNode();

            if (isLastTaskDone()){
                break;
            }

            if (DEBUG_ON){
                System.out.println("tryRunTasks: ready node received"); 
            }

            runReadyTask();

            if (DEBUG_ON){
                System.out.println("tryRunTasks: task scheduled"); 
            }
        }
        // TODO: Maybe put this in a finally block
        executor.shutdownNow();
        // System.out.println("totalWaitingTime: " + totalWaitingTime + " ns");
    }

    public String toString(){
        String result = "<Scheduler: ";
        result += "Not printing the taskGraph for concurrency reasons";
        // result += taskGraph;
        result += ">";
        return result;
    }
}
