public class Process {
    public int burst;
    public final int arrivalTime;
    public int remaining;
    public int WT;
    public int TAT;
    public final int priority;
    static int globalID;
    public final int ID;

    public Process(int arrivalTime, int burst, int priority) {
        this.burst = burst;
        this.arrivalTime = arrivalTime;
        this.remaining = burst;
        this.WT = 0;
        this.TAT = 0;
        this.priority = priority;
        this.ID = globalID++;
    }

    public void reset() {
        this.WT = 0;
        this.TAT = 0;
        this.remaining = burst;
    }

    @Override
    public String toString() {
        return "P "+ID+" arrives at "+arrivalTime+" with burst "+burst+" and priority "+priority;
    }
}
