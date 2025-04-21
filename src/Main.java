import java.util.*;


public class Main {

    public static double[] FCFS (Queue<Process> processQueue) {
        //System.out.println("FCFS Execution: ");
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
        return Recap(totalWT, totalTAT,time, timeWasted, np);
    }

    public static double[] SJF (Queue<Process> processQueue) { //non-preemptive SJF algorithm
        //System.out.println("SJF Execution:");
        int time = 0; int totalWT = 0; int totalTAT = 0; int completed = 0; int timeWasted = 0;
        int np = processQueue.size();
        Queue<Process> readyQueue = new LinkedList<>();
        while (completed < np) {

            while (!processQueue.isEmpty() && processQueue.peek().arrivalTime <= time) {
                readyQueue.add(processQueue.poll());
            }
            if (!readyQueue.isEmpty()) {
                List<Process> sortedList = new ArrayList<>(readyQueue);
                sortedList.sort(Comparator.comparingInt(p -> p.burst));
                readyQueue.clear();
                readyQueue.addAll(sortedList);

                Process current = readyQueue.poll();

                assert current != null;
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
                time = processQueue.stream().filter(p -> p.remaining > 0)
                        .mapToInt(p -> p.arrivalTime).min().orElse(time);
                timeWasted += time - oldTime;
            }
        }
        return Recap(totalWT,totalTAT,time,timeWasted,np);
    }

    public static double[] Priority (Queue<Process>  processQueue) { //non-preemptive priority scheduling
        //System.out.println("Priority Scheduling Execution:");

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

        return Recap(totalWT,totalTAT,time,timeWasted,np);
    }

    public static double[] RoundRobin(Queue<Process> processQueue, int quantum) {
        //System.out.println("Round Robin Execution:");
        //System.out.println("Quantum = " + quantum);
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

        return Recap(totalWT,totalTAT,time,timeWasted,np);
    }

    //start of new algorithms

    public static double[] STRF(Queue<Process> processQueue) {
        //System.out.println("STRF Execution:");
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
        return Recap(totalWT,totalTAT,time,timeWasted,np);
    }

    public static double[] HRRF(Queue<Process> processQueue) { //non-preemptive HRRF algorithm
        //System.out.println("Highest Response Ratio Next Execution:");
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

         return Recap(totalWT,totalTAT,time,timeWasted,np);
    }

    public static double[] Recap(int totalWT, int totalTAT, int time, int timeWasted, int np) {
        float avgWT = (float) totalWT/np;
        float avgTAT = (float) totalTAT/np;
        float UtilizationPCT = (float) (Math.round(((float) (time - timeWasted) / time) * 100 * 100.0) / 100.0);
        float throughput = (float) np/time;

        //System.out.printf("Avg WT: %.2f -- Avg TAT: %.2f -- Util%%: %.2f -- Throughput: %.4f%n", avgWT, avgTAT, UtilizationPCT, throughput);
        return new double[]{avgWT,avgTAT,UtilizationPCT,throughput};
    }

    public static void resetProcesses(LinkedList<Process> processList) {
        for  (Process p : processList) {
            p.reset();
        }
    }

    public static String BurstSummary(LinkedList<Process> processList) {
        processList.sort(Comparator.comparingInt(p -> p.burst));
        int n = processList.size();
        int min = processList.get(0).burst;
        int q1 = processList.get(n/4).burst;
        int med = processList.get(n/2).burst;
        int q3 = processList.get(3*n/4).burst;
        int max = processList.get(n - 1).burst;
        return String.format("""
                5-Number Summary of Process Bursts:
                min: %d, q1: %d, med: %d, q3: %d, max: %d
                """, min, q1, med, q3, max);
    }

    public static String ATSummary(LinkedList<Process> processList) {
        processList.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int n = processList.size();
        int min = processList.get(0).arrivalTime;
        int q1 = processList.get(n/4).arrivalTime;
        int med = processList.get(n/2).arrivalTime;
        int q3 = processList.get(3*n/4).arrivalTime;
        int max = processList.get(n - 1).arrivalTime;
        return String.format("""
                5-Number Summary of Process Arrival Times:
                min: %d, q1: %d, med: %d, q3: %d, max: %d
                
                """,min, q1, med, q3, max);
    }

    public static void Execute(LinkedList<Process> processList, String fileName) {
        Random rand = new Random();
        //System.out.println("Generated " + processList.size()  + " processes.");
        processList.sort(Comparator.comparingInt(p -> p.arrivalTime));

        CSVWriter writer = new CSVWriter();
        writer.createFile(fileName, false);
        String burstSummary = BurstSummary(processList);
        //writer.write(burstSummary,true);
        String ATSummary = ATSummary(processList);
        //writer.write(ATSummary,true);

        //System.out.println("Solving processes with originally provided methods");
        double[] FCFSRecap = FCFS(new LinkedList<>(processList));
        writer.write("FCFS,",true, false);
        writer.writeNums(FCFSRecap,true, true);
        resetProcesses(processList);

        double[] SJFRecap = SJF(new LinkedList<>(processList));
        writer.write("SJF,",true, false);
        writer.writeNums(SJFRecap,true, true);
        resetProcesses(processList);

        int quantum = rand.nextInt(5) + 5;
        double[] roundRobinRecap = RoundRobin(new LinkedList<>(processList), quantum);
        writer.write("RoundRobin,",true, false);
        writer.writeNums(roundRobinRecap,true, true);
        resetProcesses(processList);

        double[] priorityRecap = Priority(new LinkedList<>(processList));
        writer.write("Priority,",true, false);
        writer.writeNums(priorityRecap,true, true);
        resetProcesses(processList);

        //System.out.println("\nSolving processes with new methods");
        double[] STRFRecap = STRF(new LinkedList<>(processList));
        writer.write("STRF,",true, false);
        writer.writeNums(STRFRecap,true,true);
        resetProcesses(processList);

        double[] HRRFRecap = HRRF(new LinkedList<>(processList));
        writer.write("HRRF, ",true, false);
        writer.writeNums(HRRFRecap,true,true);
        resetProcesses(processList);
    }

    public static void main(String[] args) {
        Random rand = new Random();
        int arrivalTime; int burst; int priority;

        //x = number of simulations run
        for (int x = 0; x < 20; x++) {
            //System.out.println("-----TEST CASE 1: 3-5 PROCESSES FOR EASY CHECKING-----");
            LinkedList<Process> smallTest = new LinkedList<>();
            int n = rand.nextInt(3)+2;
            for (int i = 0; i < n; i++) {
                arrivalTime = rand.nextInt(10)+1;
                burst = rand.nextInt(10)+1;
                priority = rand.nextInt(3) + 1;
                Process p = new Process(arrivalTime, burst, priority);
                smallTest.add(p);
            }
            Execute(smallTest, "smallTest.csv");

            //System.out.println("-----TEST CASE 2: LARGER POOL OF PROCESSES WITH RANDOM BT, AT & PRIORITY-----");
            LinkedList<Process> largeTest = new LinkedList<>();
            n = rand.nextInt(40)+10;
            for (int i = 0; i < n; i++) {
                arrivalTime = rand.nextInt(100)+1;
                burst = rand.nextInt(25)+1;
                priority = rand.nextInt(3) + 1;
                Process p = new Process(arrivalTime, burst, priority);
                largeTest.add(p);
            }
            Execute(largeTest, "largeTest.csv");

            //System.out.println("-----TEST CASE 3: 20 PROCESSES WITH SAME RANDOM BT ALL ARRIVE AT T=0-----");
            LinkedList<Process> sameBTAndAT = new LinkedList<>();
            burst = rand.nextInt(10)+10;
            for (int i = 0; i < 20; i++) {
                Process p = new Process(0, burst, 1);
                sameBTAndAT.add(p);
            }
            Execute(sameBTAndAT, "sameBTAndAT.csv");

            //System.out.println("-----TEST CASE 4: 20 PROCESSES WITH 10 SHORT BURSTS AND 10 VERY LONG BURSTS-----");
            LinkedList<Process> longAndShort = new LinkedList<>();
            for (int i = 0; i < 20; i++) {
                if (i < n/2) {
                    burst = rand.nextInt(25)+75;
                } else {
                    burst = rand.nextInt(10);
                }
                arrivalTime = rand.nextInt(100)+1;
                priority = rand.nextInt(3) + 1;
                Process p = new Process(arrivalTime, burst, priority);
                longAndShort.add(p);
            }
            Execute(longAndShort, "longAndShort.csv");


        //System.out.println("-----TEST CASE 5: CONTINUOUS ARRIVAL OF SHRINKING PROCESSES-----");
        LinkedList<Process> shrinking = new LinkedList<>();
        int globalBurst = 50;
        int step;
        for (int i = 0; i < 20; i++) {
            arrivalTime = i + rand.nextInt(1)+1;
            step = rand.nextInt(3) + 1;
            burst = Math.max(1, globalBurst - step);
            globalBurst = burst;
            priority = rand.nextInt(3) + 1;
            Process p = new Process(arrivalTime, burst, priority);
            shrinking.add(p);
        }
        Execute(shrinking, "shrinking.csv");
        }
    }
}