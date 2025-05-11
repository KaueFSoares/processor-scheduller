import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App {
    private static final int QUANTUM = 4;

    public static void main(String[] args) throws Exception {
        Path path = Path.of("input.txt");

        Queue<Process> processes = parseFileContentIntoProcesses(path);

        System.out.println("Processes (file content):");
        processes.forEach(System.out::println);

        Map<Integer, Queue<Process>> queuesByPriority = processes.stream()
                .map(Process::priority)
                .distinct()
                .collect(Collectors.toMap(priority -> priority, priority -> new LinkedList<>()));

        int currentTime = -1;
        Process last = null;
        int runningFor = 0;

        while (!shouldStop(processes, queuesByPriority)) {
            currentTime++;

            putProcessesToPriorityQueue(processes, currentTime, queuesByPriority);

            Queue<Process> highestPriorityNonEmptyQueue = queuesByPriority.entrySet().stream()
                    // removes empty queues
                    .filter(entry -> !entry.getValue().isEmpty())
                    // sorts by priority
                    .sorted(Comparator.comparingInt(Map.Entry::getKey))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(null);

            if (highestPriorityNonEmptyQueue == null) continue;

            Process finalLast = last;
            Process runningProcess = highestPriorityNonEmptyQueue.stream().filter(p -> p.equals(finalLast)).findFirst().orElse(null);

            if (runningProcess != null) {
                if (runningProcess.isFinished()) {
                    // switch to another one if there is another from same queue or else got to another queue
                }

                if (runningFor == QUANTUM) {
                    // switch to another one if there is another from same queue
                }
                runningFor++;
                runningProcess.increaseRuntime();

                continue;
            }

            last = highestPriorityNonEmptyQueue.stream().findFirst().get();
            runningFor = 1;
        }
    }

    private static boolean shouldStop(Queue<Process> processes, Map<Integer, Queue<Process>> queuesByPriority) {
        boolean noMissingProcess = processes.isEmpty();
        boolean allQueuesAreEmpty = queuesByPriority.entrySet().stream().allMatch(entry -> entry.getValue().isEmpty());

        // TODO: validate if all bursts have been spent

        return noMissingProcess && allQueuesAreEmpty;
    }

    private static void putProcessesToPriorityQueue(Queue<Process> processes, int currentTime, Map<Integer, Queue<Process>> queuesByPriority) {
        if (processes.isEmpty() || processes.peek().time() != currentTime)
            return;

        Process process = processes.poll();

        queuesByPriority.get(process.priority()).add(process);

        putProcessesToPriorityQueue(processes, currentTime, queuesByPriority);
    }

    private static Queue<Process> parseFileContentIntoProcesses(Path path) throws IOException {
        try (Stream<String> lines = Files.lines(path)) {
            List<Process> processes = lines.map(App::parseFileLineToProcess)
                    .sorted(Comparator.comparingInt(Process::time))
                    .toList();

            // Making it a Queue
            return new LinkedList<>(processes);
        }
    }

    private static Process parseFileLineToProcess(String line) {
        String[] split = line.split(";");
        return new Process(
                split[0],
                Integer.parseInt(split[1]),
                Integer.parseInt(split[2]),
                Integer.parseInt(split[3])
        );
    }
}
