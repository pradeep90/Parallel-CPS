package scheduler;

import java.lang.Runnable;

public interface Continuation extends Runnable{
    public void run();
}
