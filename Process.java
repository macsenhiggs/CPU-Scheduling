public class Process {
    public final int burst;
    public final int arrivalTime;
    public int remaining;
    public int WT;
    public int TAT;
    public final int priority;
    public double responseRatio;
    static int globalID;
    public final int ID;

    public Process(int arrivalTime, int burst, int priority) {
        this.burst = burst;
        this.arrivalTime = arrivalTime;
        this.priority = priority;
        remaining = burst;
        WT = 0;
        TAT = 0;
        responseRatio = 1.0d;
        ID = globalID++;
    }

    public void reset() {
        WT = 0;
        TAT = 0;
        responseRatio = 1.0d;
        remaining = burst;
    }

    @Override
    public String toString() {
        return "P "+ID+" arrives at "+arrivalTime+" with burst "+burst+" and priority "+priority;
    }

    public void updateWT(int time) {
        WT = time - arrivalTime;
    }

    public void updateResponseRatio() {
        responseRatio = (1 +  (double) WT/burst);
    }

}
