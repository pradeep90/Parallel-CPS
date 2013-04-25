package scheduler;

import java.util.LinkedList;
import java.util.Stack;


public class Scheduler {
    public Stack<Activation> tasks;
    
    public Scheduler() {
        tasks = new Stack<Activation>();
    }

    public void schedule(Activation activation){
        tasks.push(activation);
    }

    public void addEdge(Activation from, Activation to){
        
    }

    public Activation removeReadyNode(){
        return tasks.pop();
    }

    public void tryRunTask(){
        while (!tasks.isEmpty()){
            Activation currTask = removeReadyNode();
            // System.out.println("currTask: " + currTask);
            currTask.run();
        }
    }

    public String toString(){
        String result = "<Scheduler: ";
        result += tasks;
        result += ">";
        return result;
    }
}
