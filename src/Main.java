import java.util.*;
import org.apache.commons.math3.distribution.BetaDistribution;


public class Main {

    public static void FCFS (Queue<Process> processQueue) {
        System.out.println("FCFS Execution: ");
        int time = 0; int totalWT = 0; int totalTAT = 0; int timeWasted = 0;
        int np = processQueue.size();
        while (!processQueue.isEmpty()) {
            Process current = processQueue.poll();
            if (time <  current.arrivalTime) {
                int oldTime = time;
                time = current.arrivalTime;
                timeWasted += time - oldTime;
            }

            time += current.burst;

            current.WT = time - current.arrivalTime;
            current.TAT = current.WT + current.burst;
            totalWT += current.WT;
            totalTAT += current.TAT;
        }
        Recap(totalWT, totalTAT,time, timeWasted, np);
    }

    public static void SJF (LinkedList<Process> processList) { //non-preemptive SJF algorithm
        System.out.println("SJF Execution:");
        int time = 0; int totalWT = 0; int totalTAT = 0; int completed = 0; int timeWasted = 0;
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
                completed++;
            } else {
                //no ready processes yet, jump to next arrival time
                int oldTime = time;
                time = processList.stream().filter(p -> p.remaining > 0)
                        .mapToInt(p -> p.arrivalTime).min().orElse(time);
                timeWasted += time - oldTime;
            }
        }
        Recap(totalWT,totalTAT,time,timeWasted,np);
    }

    public static void Priority (Queue<Process>  processQueue) { //non-preemptive priority scheduling
        System.out.println("Priority Scheduling Execution:");

        int time = 0; int totalWT = 0; int totalTAT = 0; int completed = 0; int timeWasted = 0;
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
                completed++;

            } else {
                if (!processQueue.isEmpty()) {
                    int oldTime = time;
                    time = processQueue.peek().arrivalTime;
                    timeWasted += time - oldTime;
                }
            }
        }

        Recap(totalWT,totalTAT,time,timeWasted,np);
    }

    public static void RoundRobin(Queue<Process> processQueue, int quantum) {
        System.out.println("Round Robin Execution:");
        System.out.println("Quantum = " + quantum);
        int time = 0; int totalWT = 0; int totalTAT = 0; int completed = 0; int timeWasted = 0;
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
                    completed++;
                } else {
                    current.remaining -= quantum;
                    time += quantum;
                    //add all new arrivals anytime time is updated
                    while (!processQueue.isEmpty() && processQueue.peek().arrivalTime <= time) {
                        readyQueue.add(processQueue.poll());
                    }

                    readyQueue.add(current); // Re-add the process back to the ready queue
                }

            } else {
                // If no processes are ready, increment the time to the next arriving process
                if (!processQueue.isEmpty()) {
                    int oldTime = time;
                    time = processQueue.peek().arrivalTime;
                    timeWasted += time - oldTime;
                }
                //add all new arrivals anytime time is updated
                while (!processQueue.isEmpty() && processQueue.peek().arrivalTime <= time) {
                    readyQueue.add(processQueue.poll());
                }
            }
        }

        Recap(totalWT,totalTAT,time,timeWasted,np);
    }

    //start of new algorithms

    public static void STRF(Queue<Process> processQueue) {
        System.out.println("STRF Execution:");
        int time = 0; int totalWT = 0; int totalTAT = 0; int completed = 0; int timeWasted = 0;
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
                timeWasted++;
            }
        }
        Recap(totalWT,totalTAT,time,timeWasted,np);
    }

    public static void HighestResponseRatioNext(Queue<Process> processQueue) { //non-preemptive HRRN algorithm
        System.out.println("Highest Response Ratio Next Execution:");
        int time = 0; int totalWT = 0; int totalTAT = 0; int completed = 0; int timeWasted = 0;
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
                    //System.out.println(p + " - " + p.responseRatio);
                }

                List<Process> sortedList = new ArrayList<>(readyQueue);
                sortedList.sort(Comparator.comparingDouble(p -> -p.responseRatio));
                readyQueue.clear();
                readyQueue.addAll(sortedList);

                Process current = readyQueue.poll();
                //System.out.println("t = " + time + " - Selected process " + current.ID + " with response ratio " + current.responseRatio);

                assert current != null;

                time += current.burst;
                current.TAT = time - current.arrivalTime;
                current.WT = current.TAT - current.burst;
                totalWT += current.WT; totalTAT += current.TAT;

                current.remaining = 0;

                completed++;
            } else {
                if (!processQueue.isEmpty()) {
                    int oldTime = time;
                    time = processQueue.peek().arrivalTime;
                    timeWasted += time - oldTime;
                }
            }
        }

        Recap(totalWT,totalTAT,time,timeWasted,np);
    }

    public static void Recap(int totalWT, int totalTAT, int time, int timeWasted, int np) {
        System.out.println("Avg WT: " + (double) totalWT/np);
        System.out.println("Avg TAT: " + (double) totalTAT/np);
        double UtilizationPCT = ((double) (time - timeWasted) / time) * 100;
        System.out.println("CPU Utilization: " + Math.round(UtilizationPCT * 100.0) / 100.0 + "%");
        System.out.println("Throughput: " + (float) np/time + " processes/second");
    }

    public static void resetProcesses(LinkedList<Process> processList) {
        for  (Process p : processList) {
            p.reset();
        }
    }

    public static void BurstSummary(LinkedList<Process> processList) {
        processList.sort(Comparator.comparingInt(p -> p.burst));
        int n = processList.size();
        int min = processList.get(0).burst;
        int q1 = processList.get(n/4).burst;
        int med = processList.get(n/2).burst;
        int q3 = processList.get(3*n/4).burst;
        int max = processList.get(n - 1).burst;
        System.out.printf("""
                5-Number Summary of Process Bursts:
                min: %d
                q1: %d
                med: %d
                q3: %d
                max: %d
                
                """,min, q1, med, q3, max);
    }

    public static void ATSummary(LinkedList<Process> processList) {
        processList.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int n = processList.size();
        int min = processList.get(0).arrivalTime;
        int q1 = processList.get(n/4).arrivalTime;
        int med = processList.get(n/2).arrivalTime;
        int q3 = processList.get(3*n/4).arrivalTime;
        int max = processList.get(n - 1).arrivalTime;
        System.out.printf("""
                5-Number Summary of Process Arrival Times:
                min: %d
                q1: %d
                med: %d
                q3: %d
                max: %d
                
                """,min, q1, med, q3, max);
    }

    public static void main(String[] args) {
        Random rand = new Random();
        LinkedList<Process> processList = new LinkedList<>();

        int n = 40;
        int totalBT = 0;

        BetaDistribution betaAT = new BetaDistribution(3,2);
        BetaDistribution betaBurst = new BetaDistribution(2,5);

        int arrivalTime; int burst;
        for (int i = 0; i < n; i++) {
            if (i < n/5) {
                arrivalTime = (int) Math.round(betaAT.sample() * 5);
                burst = (int) Math.round(50 + betaBurst.sample() * 250);
            } else {
                arrivalTime = (int) Math.round(20 + betaAT.sample() * 80);
                burst = (int) Math.round(betaBurst.sample() * 50);
            }

            totalBT += burst;

            int priority = rand.nextInt(3) + 1;
            Process p = new Process(arrivalTime, burst, priority);
            //System.out.println(p);
            processList.add(p);

        }

        System.out.println("Generated " + n  + " processes with average BT of " + (double) totalBT/n);
        BurstSummary(processList);
        ATSummary(processList);

        processList.sort(Comparator.comparingInt(p -> p.arrivalTime));

        System.out.println("Solving processes with originally provided methods");

        FCFS(new LinkedList<>(processList));
        resetProcesses(processList);

        SJF(processList);
        resetProcesses(processList);

        int quantum = rand.nextInt(5) + 5;
        RoundRobin(new LinkedList<>(processList), quantum);
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