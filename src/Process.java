public class Process {
    public final int burst;
    public final int arrivalTime;
    public int remaining;
    public int waitTime;
    public int turnaroundTime;
    public final int priority;
    static int globalID;
    public final int ID;

    public Process(int burst, int arrivalTime, int priority) {
        this.burst = burst;
        this.arrivalTime = arrivalTime;
        this.remaining = burst;
        this.waitTime = 0;
        this.turnaroundTime = 0;
        this.priority = priority;
        this.ID = globalID++;
    }

    @Override
    public String toString() {
        return "Process " + ID + " has burst " + burst + " and arrival time " + arrivalTime;
    }
}
