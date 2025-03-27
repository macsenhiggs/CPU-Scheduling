import java.util.*;

public class Main {

    public static void FCFS (Queue<Process> processQueue) {
        int time = 0; int totalWT = 0; int totalTAT = 0;
        int np = processQueue.size();
        while (!processQueue.isEmpty()) {
            Process current = processQueue.poll();
            if (time <  current.arrivalTime) {
                time = current.arrivalTime;
            }

            time += current.burst;

            current.waitTime = time - current.arrivalTime;
            current.turnaroundTime = current.waitTime + current.burst;
            totalWT += current.waitTime;
            totalTAT += current.turnaroundTime;

            System.out.println("P" + current.ID + " (AT=" + current.arrivalTime +
                    ", BT=" + (current.turnaroundTime - current.waitTime) + ") executed at T=" +
                    (time - (current.turnaroundTime - current.waitTime)));
        }
        System.out.println("Avg WT: " + (float) totalWT/np);
        System.out.println("Avg TAT: " + (float) totalTAT/np);
    }

    public static void SJF (Queue<Process> processQueue) { //non-preemptive SJF algorithm
        System.out.println("SJF Execution:");
        int time = 0; int totalWT = 0; int totalTAT = 0; int completed = 0;
        int np = processQueue.size();
        while (completed < np) {
            List<Process> availableProcesses = new ArrayList<>();
            for (Process p  : processQueue) {
                if (p.arrivalTime <= time && p.remaining > 0) {
                    availableProcesses.add(p);
                }
            }
            if (!availableProcesses.isEmpty()) {
                availableProcesses.sort(Comparator.comparingInt(p -> p.burst));
                Process current = availableProcesses.get(0);

                current.waitTime = time - current.arrivalTime;
                current.turnaroundTime = current.waitTime + current.burst;
                totalWT += current.waitTime;
                totalTAT += current.turnaroundTime;

                time += current.burst;
                current.remaining = 0;
                System.out.println("P" + current.ID + " (AT=" + current.arrivalTime +
                        ", BT=" + (current.turnaroundTime - current.waitTime) + ") executed at T=" +
                        (time - (current.turnaroundTime - current.waitTime)));
                completed++;
            } else {
                //no ready processes yet, jump to next arrival time
                time = processQueue.stream().filter(p -> p.remaining > 0)
                        .mapToInt(p -> p.arrivalTime).min().orElse(time);
            }
        }
        System.out.println("Avg WT: " + (float) totalWT/np);
        System.out.println("Avg TAT: " + (float) totalTAT/np);
    }

    public static LinkedList<Process> sortByArrivalTime(LinkedList<Process> processList) {
        processList.sort(Comparator.comparingInt(p -> p.arrivalTime));
        return new LinkedList<>(processList);
    }

    public static void main(String[] args) {
        Random rand = new Random();
        LinkedList<Process> processList = new LinkedList<>();
        for (int i = 0; i < 20; i++) {
            int arrivalTime = rand.nextInt(100) + 1;
            int burst = rand.nextInt(20) + 1;
            int priority = rand.nextInt(3) + 1;
            processList.add(new Process(burst, arrivalTime, priority));
        }

        Queue<Process> processQueue = sortByArrivalTime(processList);
        processList = sortByArrivalTime(processList);

        FCFS(processQueue);
        SJF(processList);
    }
}