import java.util.*;

public class ProcessScheduler {

    private static final int QUANTUM = 4;

    private final Map<Integer, Queue<Process>> processes = new HashMap<>();

    private final List<Process> finishedProcesses = new ArrayList<>();

    private Process running;
    private int runningFor;

    private boolean shouldPreempt;

    public double summary() {
        System.out.println("--------------Summary--------------");
        finishedProcesses.forEach(System.out::println);

        int sumOfProcessesTimes = finishedProcesses.stream()
                .map(Process::waitTime)
                .reduce(0, Integer::sum);
        int totalProcesses = finishedProcesses.size();

        System.out.println("Sum of times: " + sumOfProcessesTimes + "; Total processes: " + totalProcesses);
        return (double) sumOfProcessesTimes / totalProcesses;
    }

    public boolean readyToStop() {
        boolean noRunningProcess = running == null;
        boolean allQueuesAreEmpty = processes.values().stream().allMatch(Queue::isEmpty);

        return noRunningProcess && allQueuesAreEmpty;
    }

    public void increaseTime(int currentTime) {
        boolean currentRunningProcessIfFinished = running != null && running.isFinished();
        if (currentRunningProcessIfFinished) {
            System.out.println("Finished execution for process: " + running.id());

            running.finish(currentTime);
            finishedProcesses.add(running);

            removeCurrentRunningProcess();
        }

        if (running != null && runningFor == QUANTUM) {
            System.out.println("Quantum expired for process: " + running.id());

            returnProjectToQueue();
            removeCurrentRunningProcess();
        }

        if (running != null && shouldPreempt) {
            System.out.println("Process: " + running.id() + " preempted due to other process with higher priority");

            shouldPreempt = false;

            returnProjectToQueue();
            removeCurrentRunningProcess();
        }

        if (running == null) {
            running = processes.entrySet().stream()
                    .sorted(Comparator.comparingInt(Map.Entry::getKey))
                    .map(Map.Entry::getValue)
                    .filter(queue -> !queue.isEmpty())
                    .findFirst()
                    .map(Queue::poll)
                    .orElse(null);

            if (running != null)
                System.out.println("Process: " + running.id() + " started / resumed execution");
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

        if (running != null && process.priority() < running.priority())
            shouldPreempt = true;

        System.out.println("Added process: " + process);
    }

    private void returnProjectToQueue() {
        processes.get(running.priority()).add(running);
    }

    private void removeCurrentRunningProcess() {
        running = null;
        runningFor = 0;
    }
}
