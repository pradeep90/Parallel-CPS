package callabletry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static callabletry.Factorial.*;

public abstract class MethodCall {
    Factorial factorial;
    public String name;

    long elapsedTime;
    
    public MethodCall(String name){
        this.name = name;
        factorial = new Factorial();
    }
                
    public abstract void call();

    public void measure(){
        long startTime = System.nanoTime();
        for (int i = 0; i < NUM_ITERS; i++){
            this.call();
        }
        elapsedTime = System.nanoTime() - startTime;

        System.out.println(this.name + ": (" + NUM_ITERS + " rounds): "
                           + elapsedTime / NUM_ITERS + " ns");
    }
}
