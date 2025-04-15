public class Process {
    public int burst;
    public final int arrivalTime;
    public int remaining;
    public int WT;
    public int TAT;
    public int last_execution_time;
    public final int priority;
    static int globalID;
    public final int ID;

    public Process(int burst, int arrivalTime, int priority) {
        this.burst = burst;
        this.arrivalTime = arrivalTime;
        this.remaining = burst;
        this.WT = 0;
        this.TAT = 0;
        this.priority = priority;
        this.ID = globalID++;
        this.last_execution_time = -1;
    }

    public void reset() {
        this.last_execution_time = -1;
        this.WT = 0;
        this.TAT = 0;
        this.remaining = burst;
    }

    @Override
    public String toString() {
        return "P"+ID;
    }
}
