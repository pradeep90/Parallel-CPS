package scheduler;

public class Activation {
    public Continuation continuation;
    public Result result;
    public int tempResult;

    public Activation() {
        this.tempResult = 0;
    }

    public Activation(int result) {
        this.tempResult = result;
    }
    
    // public Activation(Continuation continuation, Result result) {
    //     this.continuation = continuation;
    //     this.result = result;
    // }

    // public Activation(Continuation continuation, int result) {
    //     this.continuation = continuation;
    //     this.tempResult = result;
    // }

    public void run(){
        if (continuation == null){
            System.out.println("Empty Continuation"); 
            return;
        }

        continuation.call();
    }

    public String toString(){
        String result = "<Activation: ";
        result += continuation;
        result += ", ";
        result += tempResult;
        result += ">";
        return result;
    }
}
