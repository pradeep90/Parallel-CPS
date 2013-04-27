package scheduler;

public class LastActivation extends Activation {
    public LastActivation(Scheduler scheduler) {
        super(scheduler);
    }

    @Override
    public void run(){
        super.run();
        System.out.println("LastActivation running"); 
        scheduler.signalLastTaskDone();
        System.out.println("LastActivation signalLastTaskDone"); 
    }

    public String toString(){
        String result = "<LastActivation>";
        return result;
    }
}
