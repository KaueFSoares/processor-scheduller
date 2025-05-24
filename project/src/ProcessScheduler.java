import java.util.*;

public class ProcessScheduler {

    private static final int QUANTUM = 4;

    private final Map<Integer, Queue<Process>> processes = new HashMap<>();

    private int sumOfProcessesTimes = 0;
    private int totalProcesses = 0;

    private Process running;
    private int runningFor;

    private boolean shouldPreempt;

    public double averageProcessTime() {
        System.out.println("Sum of times: " + sumOfProcessesTimes + "; Total processes: " + totalProcesses);
        return (double) sumOfProcessesTimes / totalProcesses;
    }

    public boolean readyToStop() {
        boolean noRunningProcess = running == null;
        boolean allQueuesAreEmpty = processes.values().stream().allMatch(Queue::isEmpty);

        return noRunningProcess && allQueuesAreEmpty;
    }

    public void increaseTime(int currentTime) {
        if (running != null && running.isFinished()) {
            System.out.println("Finished execution for process: " + running.id());

            running.finish(currentTime);
            sumOfProcessesTimes += running.waitTime();

            running = null;
            runningFor = 0;
        }

        if (running != null && runningFor == QUANTUM) {
            System.out.println("Quantum expired for process: " + running.id());

            processes.get(running.priority()).add(running);

            running = null;
            runningFor = 0;
        }

        if (running != null && shouldPreempt) {
            System.out.println("Process: " + running.id() + " preempted due to other process with higher priority");

            processes.get(running.priority()).add(running);

            running = null;
            runningFor = 0;
        }

        if (running == null) {
            // TODO: this is considering that if a process has a higher priority, it might have sequential quantum's, witch might not be right
            running = processes.entrySet().stream()
                    .sorted(Comparator.comparingInt(Map.Entry::getKey))
                    .map(Map.Entry::getValue)
                    .filter(queue -> !queue.isEmpty())
                    .findFirst()
                    .map(Queue::poll)
                    .orElse(null);

            if (running != null) {
                System.out.println("Process: " + running.id() + " started / resumed execution");
            }
        }

        if (running != null) {
            runningFor++;
            running.increaseRuntime();
        }
    }

    public void addProcess(Process process) {
        processes
                .computeIfAbsent(process.priority(), k -> new LinkedList<>())
                .add(process);

        if (running != null && process.priority() > running.priority())
            shouldPreempt = true;

        totalProcesses++;

        System.out.println("Added process: " + process);
    }
}
