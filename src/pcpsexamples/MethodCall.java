package pcpsexamples;

public abstract class MethodCall {
    public String name;
    long elapsedTime;
    public static final int NUM_ITERS = 10;
    
    public MethodCall(String name){
        this.name = name;
    }
                
    public abstract void call();

    public void measure(){
        long startTime = System.nanoTime();
        for (long i = 0; i < NUM_ITERS; i++){
            this.call();
        }
        elapsedTime = System.nanoTime() - startTime;

        System.out.println(this.name + ": (" + NUM_ITERS + " rounds, MAX_ITERS: " + Fib.MAX_ITERS + "): "
                           + elapsedTime / NUM_ITERS + " ns");
    }
}
