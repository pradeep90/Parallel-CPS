package scheduler;

public class LastActivation extends Activation {
    public LastActivation(Scheduler scheduler) {
        super(scheduler);
    }

    @Override
    public void run(){
        super.run();
        if (scheduler.DEBUG_ON){
            System.out.println("LastActivation running"); 
        }

        if (scheduler != null){
            scheduler.signalLastTaskDone();
        }

        if (scheduler.DEBUG_ON){
            System.out.println("LastActivation signalLastTaskDone"); 
        }
    }

    public String toString(){
        String result = "<LastActivation>";
        return result;
    }
}
