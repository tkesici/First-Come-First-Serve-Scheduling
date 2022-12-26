import java.io.*;
import java.util.*;
import java.util.stream.*;

/*
 * @author     Tevfik KESICI
 * @studentId  20200808004
 * @since      21.12.22
 *
 * CSE 303 – Fundamentals of Operating Systems
 * HW#2 FCFS CPU Scheduling with IO
 *
 */

public class FCFS {
    public static void main(String[] args) throws Exception {


        ArrayList<Integer> cpuBurst;
        ArrayList<Integer> ioBurst;
        ArrayList<Integer> jobs;
        ArrayList<String> lines;
        int count;
        int processCount;

        // Read from file dynamically

        try (BufferedReader reader = new BufferedReader(new FileReader(args[0]))) {

            cpuBurst = new ArrayList<>();
            ioBurst = new ArrayList<>();
            jobs = new ArrayList<>();
            lines = new ArrayList<>();

            count = 0;
            processCount = 0;

            // Count the processes and tuples length
            while (reader.ready()) {
                StringBuilder line = new StringBuilder(reader.readLine());
                lines.add(line.toString());
                int index = line.indexOf(":");
                processCount++;
                String[] tuples = line.substring(index + 1).split(";");
                count += tuples.length;
            }
        }

        // Smaller processes arriving earlier, so we need to compare and sort our processes
        Comparator<String> comparator = (s1, s2) -> {
            int index1 = s1.indexOf(':');
            int index2 = s2.indexOf(':');
            int value1 = Integer.parseInt(s1.substring(0, index1));
            int value2 = Integer.parseInt(s2.substring(0, index2));
            return Integer.compare(value1, value2);
        };
        lines.sort(comparator);
        IntStream.range(0, count + processCount).forEach(i -> {
            cpuBurst.add(0);
            ioBurst.add(0);
            jobs.add(0);
        });
        int i = 0;

        // Getting our desired values from .txt file, and putting them into an ArrayList
        int k = 0;
        while (k < lines.size()) {
            StringBuilder line = new StringBuilder(lines.get(i));
            int index = line.indexOf(":");
            int processId = Integer.parseInt(line.substring(0, index));
            line.delete(0, index + 1);
            String[] tuples = line.toString().split(";");
            int j = i;
            for (String tuple : tuples) {
                String[] burstArray = tuple.replace("(", "").
                        replace(")", "").split(",");
                cpuBurst.set(j, Integer.parseInt(burstArray[0]));
                ioBurst.set(j, Integer.parseInt(burstArray[1]));
                jobs.set(j, processId);
                j += processCount;
            }
            i++;
            k++;
        }

        // Creating 3 more arraylists for processes, CPU/IO bursts.
        // The aim is getting our values in desired order, and this will help us
        // while computing.
        ArrayList<Integer> process = new ArrayList<>();
        ArrayList<Integer> cpu = new ArrayList<>();
        ArrayList<Integer> io = new ArrayList<>();


        // Putting them into arraylist
        IntStream.range(0, cpuBurst.size()).filter(j -> jobs.get(j) != 0).forEach(j -> {
            process.add(jobs.get(j));
            cpu.add(cpuBurst.get(j));
            io.add(ioBurst.get(j));
        });
        Integer[] cpuBursts = cpu.toArray(new Integer[0]);
        Integer[] ioBursts = io.toArray(new Integer[0]);
        Integer[] processes = process.toArray(new Integer[0]);
        FCFS(processes, cpuBursts, ioBursts);

    }

    public static void FCFS(Integer[] processes, Integer[] cpu_bursts, Integer[] io_bursts) {

        // I have a map here, and I will use this as my process queue. I will make my
        // controls here with determining the process is available at this time or not.
        Map<Integer, Integer> queue = new HashMap<>();

        Integer[] waitingTime = new Integer[processes.length];
        Integer[] turnAroundTime = new Integer[processes.length];
        boolean isIdle = false;
        int idleCount = 0;
        int totalWaitingTime = 0;
        int totalTurnAroundTime = 0;
        int current = 0;

        for (int i = 0; i < processes.length; i++) {
            // We are checking if our process is an IDLE or not
            if (queue.containsKey(processes[i]) && queue.get(processes[i]) > current) {
                isIdle = true;
            }

            if (isIdle) {
                io_bursts[i] = 0;
                cpu_bursts[i] = queue.get(processes[i]) - 1 - current;
                waitingTime[i] = waitingTime[i - 1] + cpu_bursts[i - 1] + io_bursts[i - 1];
                turnAroundTime[i] = cpu_bursts[i] + waitingTime[i];
                idleCount++;
                current += queue.get(processes[i]) - 1 - current;
                totalWaitingTime = totalWaitingTime + waitingTime[i];
                totalTurnAroundTime = totalTurnAroundTime + turnAroundTime[i];
            } else {
                if (i > 0) {
                    waitingTime[i] = waitingTime[i - 1] + cpu_bursts[i - 1] + io_bursts[i - 1];
                }
                if (i == 0) {
                    waitingTime[0] = 0;
                }
                // It's the last process of that project, so we shouldn't get the value -1, it is
                // just a flag.
                if (io_bursts[i] == -1) {
                    io_bursts[i] = 0;
                }
                turnAroundTime[i] = cpu_bursts[i] + waitingTime[i];
                totalWaitingTime = totalWaitingTime + waitingTime[i];
                totalTurnAroundTime = totalTurnAroundTime + turnAroundTime[i];
                current += cpu_bursts[i];
            }
            queue.put(processes[i], io_bursts[i]);
            isIdle = false;
        }

        int averageWaitingTime = totalWaitingTime / processes.length;
        int averageTurnaroundTime = totalTurnAroundTime / processes.length;

        System.out.println("Average turn around time: " + averageTurnaroundTime);
        System.out.println("Average waiting time: " + averageWaitingTime);
        System.out.println("The number of times that the IDLE process executed: " + idleCount);
        System.out.print("HALT");

    }
}
