public class Activation {
    Continuation continuation;
    Result result;
    
    public Activation(Continuation aContinuation, Result aResult) {
        this.continuation = aContinuation;
        this.result = aResult;
    }

    public void run(){
        continuation.call();
    }
}
