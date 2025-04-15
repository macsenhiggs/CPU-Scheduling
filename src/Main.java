import java.util.*;

public class Main {

    public static void FCFS (Queue<Process> processQueue) {
        System.out.println("FCFS Execution: ");
        int time = 0; int totalWT = 0; int totalTAT = 0;
        int np = processQueue.size();
        while (!processQueue.isEmpty()) {
            Process current = processQueue.poll();
            if (time <  current.arrivalTime) {
                time = current.arrivalTime;
            }

            time += current.burst;

            current.WT = time - current.arrivalTime;
            current.TAT = current.WT + current.burst;
            totalWT += current.WT;
            totalTAT += current.TAT;

            System.out.println("P" + current.ID + " (AT=" + current.arrivalTime +
                    ", BT=" + (current.TAT - current.WT) + ") executed at T=" +
                    (time - (current.TAT - current.WT)));
        }
        System.out.println("Avg WT: " + (float) totalWT/np);
        System.out.println("Avg TAT: " + (float) totalTAT/np);
    }

    public static void SJF (LinkedList<Process> processList) { //non-preemptive SJF algorithm
        System.out.println("SJF Execution:");
        int time = 0; int totalWT = 0; int totalTAT = 0; int completed = 0;
        int np = processList.size();
        while (completed < np) {
            List<Process> availableProcesses = new ArrayList<>();
            for (Process p  : processList) {
                if (p.arrivalTime <= time && p.remaining > 0) {
                    availableProcesses.add(p);
                }
            }
            if (!availableProcesses.isEmpty()) {
                availableProcesses.sort(Comparator.comparingInt(p -> p.burst));
                Process current = availableProcesses.get(0);

                current.WT = time - current.arrivalTime;
                current.TAT = current.WT + current.burst;
                totalWT += current.WT;
                totalTAT += current.TAT;

                time += current.burst;
                current.remaining = 0;
                System.out.println("P" + current.ID + " (AT=" + current.arrivalTime +
                        ", BT=" + (current.TAT - current.WT) + ") executed at T=" +
                        (time - (current.TAT - current.WT)));
                completed++;
            } else {
                //no ready processes yet, jump to next arrival time
                time = processList.stream().filter(p -> p.remaining > 0)
                        .mapToInt(p -> p.arrivalTime).min().orElse(time);
            }
        }
        System.out.println("Avg WT: " + (float) totalWT/np);
        System.out.println("Avg TAT: " + (float) totalTAT/np);
    }

    public static void Priority (LinkedList<Process>  processList) {
        System.out.println("Priority Scheduling Execution:");

    }

    public static void RoundRobin(Queue<Process> processQueue, int quantum) {
        System.out.println("Round Robin Execution:");
        System.out.println("Quantum = " + quantum);
        int time = 0;
        int totalWT = 0;
        int totalBT = 0;
        int totalTAT = 0;
        int completed = 0;
        int np = processQueue.size();

        // Create a queue for processes that are currently in the ready state
        Queue<Process> readyQueue = new LinkedList<>();

        // Main loop
        while (completed < np) {

            if (!readyQueue.isEmpty()) {
                Process current = readyQueue.poll();

                // Execute the current process for 'quantum' time
                if (current.remaining <= quantum) {
                    time += current.remaining;
                    current.remaining = 0;
                    current.TAT = time - current.arrivalTime;
                    current.WT = current.TAT - current.burst;
                    totalWT += current.WT;
                    totalBT += current.burst;
                    totalTAT += current.TAT;
                    System.out.println("P" + current.ID + " (AT=" + current.arrivalTime +
                            ", BT=" + current.burst + ") finished at T=" + time);
                    completed++;
                } else {
                    System.out.println("P" + current.ID + " (AT=" + current.arrivalTime +
                            ", BT=" + current.burst + ") started at T=" + time);
                    current.remaining -= quantum;
                    time += quantum;
                    //add all new arrivals anytime time is updated
                    while (!processQueue.isEmpty() && processQueue.peek().arrivalTime <= time) {
                        readyQueue.add(processQueue.poll());
                    }
                    System.out.println("P" + current.ID + " (AT=" + current.arrivalTime +
                            ", BT=" + current.burst + ") paused at T=" +
                            time + " with R=" + current.remaining);
                    readyQueue.add(current); // Re-add the process back to the ready queue
                }
                System.out.println("Next in Queue: " + readyQueue);
            } else {
                // If no processes are ready, increment the time to the next arriving process
                if (!processQueue.isEmpty()) {
                    time = processQueue.peek().arrivalTime;
                }
                //add all new arrivals anytime time is updated
                while (!processQueue.isEmpty() && processQueue.peek().arrivalTime <= time) {
                    readyQueue.add(processQueue.poll());
                }
            }
        }

        // Output average waiting time and turnaround time
        System.out.println("Avg WT: " + (float) totalWT / np);
        System.out.println("Avg BT: " + (float) totalBT / np);
        System.out.println("Avg TAT: " + (float) totalTAT / np);
    }

    public static void resetProcesses(LinkedList<Process> processList) {
        for  (Process p : processList) {
            p.reset();
        }
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

        processList.sort(Comparator.comparingInt(p -> p.arrivalTime));
        Queue<Process> processQueue = new LinkedList<>(processList);


        FCFS(new LinkedList<>(processList));
        resetProcesses(processList);
        SJF(processList);
        resetProcesses(processList);
        //Priority(processList);
        //resetProcesses(processList);
        int quantum = rand.nextInt(10) + 1;
        RoundRobin(processQueue, quantum);
    }
}