package scheduler;

public abstract class AbstractContinuation implements Continuation {
    public Activation now;
    public Activation later;
    public String name = "BaseContinuation";
    
    public AbstractContinuation(Activation now, Activation later) {
        this.now = now;
        this.later = later;
    }

    public AbstractContinuation(){
        this.name = "NormalContinuation";
    }

    public abstract void run();

    public String toString(){
        String result = "";
        result += "<" + name + ">";
        return result;
    }
}
