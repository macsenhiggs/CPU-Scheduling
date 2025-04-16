public class Process {
    public int burst;
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
        this.remaining = burst;
        this.WT = 0;
        this.TAT = 0;
        this.priority = priority;
        this.responseRatio = 1.0f;
        this.ID = globalID++;
    }

    public void reset() {
        this.WT = 0;
        this.TAT = 0;
        this.responseRatio = 1.0f;
        this.remaining = burst;
    }

    @Override
    public String toString() {
        return "P "+ID+" arrives at "+arrivalTime+" with burst "+burst+" and priority "+priority;
    }

    public void updateWT(int time) {
        this.WT = time - this.arrivalTime;
    }

    public void updateResponseRatio() {
        this.responseRatio = (1 +  (double) this.WT/this.burst);
    }
}
