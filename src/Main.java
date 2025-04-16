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

            /*
            System.out.println("P" + current.ID + " (AT=" + current.arrivalTime +
                    ", BT=" + (current.TAT - current.WT) + ") executed at T=" +
                    (time - (current.TAT - current.WT)));
             */
        }
        System.out.println("Avg WT: " + (double) totalWT/np);
        System.out.println("Avg TAT: " + (double) totalTAT/np);
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
                /*
                System.out.println("P" + current.ID + " (AT=" + current.arrivalTime +
                        ", BT=" + (current.TAT - current.WT) + ") executed at T=" +
                        (time - (current.TAT - current.WT)));

                 */
                completed++;
            } else {
                //no ready processes yet, jump to next arrival time
                time = processList.stream().filter(p -> p.remaining > 0)
                        .mapToInt(p -> p.arrivalTime).min().orElse(time);
            }
        }
        System.out.println("Avg WT: " + (double) totalWT/np);
        System.out.println("Avg TAT: " + (double) totalTAT/np);
    }

    public static void Priority (Queue<Process>  processQueue) { //non-preemptive priority scheduling
        System.out.println("Priority Scheduling Execution:");

        int time = 0; int totalWT = 0; int totalTAT = 0; int completed = 0;
        int np = processQueue.size();

        // Create a queue for processes that are currently in the ready state
        Queue<Process> readyQueue = new LinkedList<>();

        while (completed < np) {
            //add all new arrivals anytime time is updated
            while (!processQueue.isEmpty() && processQueue.peek().arrivalTime <= time) {
                readyQueue.add(processQueue.poll());
            }

            if (!readyQueue.isEmpty()) {
                //sort queue of ready processes by their priority
                List<Process> sortedList = new ArrayList<>(readyQueue);
                sortedList.sort(Comparator.comparingInt(p -> p.priority));
                readyQueue.clear();
                readyQueue.addAll(sortedList);

                Process current = readyQueue.poll();

                assert current != null;
                time += current.burst;
                current.TAT = time - current.arrivalTime;
                current.WT = current.TAT - current.burst;
                totalWT += current.WT; totalTAT += current.TAT;

                current.remaining = 0;

                /*
                System.out.println("P" + current.ID + " (AT=" + current.arrivalTime +
                        ", BT=" + (current.TAT - current.WT) + ") executed at T=" +
                        (time - (current.TAT - current.WT)));
                 */
                completed++;

            } else {
                if (!processQueue.isEmpty()) {
                    time = processQueue.peek().arrivalTime;
                }
            }
        }

        System.out.println("Avg WT: " + (double) totalWT / np);
        System.out.println("Avg TAT: " + (double) totalTAT / np);
    }

    public static void RoundRobin(Queue<Process> processQueue, int quantum, boolean print) {
        System.out.println("Round Robin Execution:");
        System.out.println("Quantum = " + quantum);
        int time = 0; int totalWT = 0; int totalTAT = 0; int completed = 0;
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
                    totalTAT += current.TAT;
                    if (print) {
                        System.out.println("P" + current.ID + " (AT=" + current.arrivalTime +
                                ", BT=" + current.burst + ") finished at T=" + time);
                    }
                    completed++;
                } else {
                    if (print) {
                        System.out.println("P" + current.ID + " (AT=" + current.arrivalTime +
                                ", BT=" + current.burst + ") started at T=" + time);
                    }
                    current.remaining -= quantum;
                    time += quantum;
                    //add all new arrivals anytime time is updated
                    while (!processQueue.isEmpty() && processQueue.peek().arrivalTime <= time) {
                        readyQueue.add(processQueue.poll());
                    }
                    if (print) {
                        System.out.println("P" + current.ID + " (AT=" + current.arrivalTime +
                                ", BT=" + current.burst + ") paused at T=" +
                                time + " with R=" + current.remaining);
                    }

                    readyQueue.add(current); // Re-add the process back to the ready queue
                }
                if (print && !readyQueue.isEmpty()) {
                    System.out.println("Next in Queue: " + readyQueue);
                }

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
        System.out.println("Avg WT: " + (double) totalWT / np);
        System.out.println("Avg TAT: " + (double) totalTAT / np);
    }

    //start of new algorithms

    public static void STRF(Queue<Process> processQueue) {
        System.out.println("STRF Execution:");
        int time = 0; int totalWT = 0; int totalTAT = 0; int completed = 0;
        int np = processQueue.size();

        Queue<Process> readyQueue = new LinkedList<>();

        while (completed < np) {
            //add all ready processes to queue
            while (!processQueue.isEmpty() && processQueue.peek().arrivalTime <= time) {
                readyQueue.add(processQueue.poll());
            }

            if (!readyQueue.isEmpty()) {
                List<Process> sortedList = new ArrayList<>(readyQueue);
                sortedList.sort(Comparator.comparingInt(p -> p.remaining));
                readyQueue.clear();
                readyQueue.addAll(sortedList);

                Process current = readyQueue.poll();

                assert current != null;
                current.remaining--;
                time++;

                if (current.remaining <= 0) {
                    completed++;
                    current.TAT = time - current.arrivalTime;
                    current.WT = current.TAT - current.burst;
                    totalWT += current.WT; totalTAT += current.TAT;
                } else {
                    readyQueue.add(current);
                }

            } else {
                time++;
            }
        }
        System.out.println("Avg WT: " + (double) totalWT / np);
        System.out.println("Avg TAT: " + (double) totalTAT / np);
    }

    public static void HighestResponseRatioNext(Queue<Process> processQueue) {
        System.out.println("Highest Response Ratio First Execution:");
        int time = 0; int totalWT = 0; int totalTAT = 0; int completed = 0;
        int np = processQueue.size();

        Queue<Process> readyQueue = new LinkedList<>();

        while (completed < np) {
            while (!processQueue.isEmpty() && processQueue.peek().arrivalTime <= time) {
                readyQueue.add(processQueue.poll());
            }

            if (!readyQueue.isEmpty()) {
                for (Process p : readyQueue) {
                    p.updateWT(time);
                    p.updateResponseRatio();
                }

                List<Process> sortedList = new ArrayList<>(readyQueue);
                sortedList.sort(Comparator.comparingDouble(p -> -p.responseRatio));
                readyQueue.clear();
                readyQueue.addAll(sortedList);

                Process current = readyQueue.poll();

                assert current != null;

                time += current.burst;
                current.TAT = time - current.arrivalTime;
                current.WT = current.TAT - current.burst;
                totalWT += current.WT; totalTAT += current.TAT;

                current.remaining = 0;

                completed++;
            } else {
                if (!processQueue.isEmpty()) {
                    time = processQueue.peek().arrivalTime;
                }
            }
        }

        System.out.println("Avg WT: " + (double) totalWT / np);
        System.out.println("Avg TAT: " + (double) totalTAT / np);
    }

    public static void resetProcesses(LinkedList<Process> processList) {
        for  (Process p : processList) {
            p.reset();
        }
    }

    public static void main(String[] args) {
        Random rand = new Random();
        LinkedList<Process> processList = new LinkedList<>();

        int n = 50;
        int totalBT = 0;

        for (int i = 0; i < n; i++) {
            //int arrivalTime = rand.nextInt(100) + 1;
            //int burst = rand.nextInt(20) + 1;

            int arrivalTime = (i < n/10) ? 0 : rand.nextInt(10) + 1; // Few big jobs early, rest arrive soon after
            int burst = (i < n/10) ? rand.nextInt(40) + 30 : rand.nextInt(10) + 1; // First 5 are long, rest are short

            totalBT += burst;

            int priority = rand.nextInt(3) + 1;
            Process p = new Process(arrivalTime, burst, priority);
            System.out.println(p);
            processList.add(p);


        }

        System.out.println("Generated " + n  + " processes with average BT of " + (double) totalBT/n);

        processList.sort(Comparator.comparingInt(p -> p.arrivalTime));

        System.out.println("Solving processes with originally provided methods");

        FCFS(new LinkedList<>(processList));
        resetProcesses(processList);

        SJF(processList);
        resetProcesses(processList);

        int quantum = rand.nextInt(5) + 5;
        RoundRobin(new LinkedList<>(processList), quantum, false);
        resetProcesses(processList);

        Priority(new LinkedList<>(processList));
        resetProcesses(processList);

        System.out.println("\nSolving processes with new methods");
        STRF(new LinkedList<>(processList));
        resetProcesses(processList);

        HighestResponseRatioNext(new LinkedList<>(processList));
        resetProcesses(processList);
    }
}