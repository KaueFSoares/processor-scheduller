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

        int currentTime = 0;

        while (true) {
            boolean hasProcesses = !processes.isEmpty();
            boolean allQueuesAreEmpty = queuesByPriority.entrySet().stream().anyMatch(entry -> !entry.getValue().isEmpty());

            // TODO: validate if all bursts have been spent

            if (!hasProcesses && !allQueuesAreEmpty) break;

            if (!processes.isEmpty() && processes.peek().time() == currentTime) {
                Process process = processes.poll();

                queuesByPriority.get(process.priority()).add(process);
            }

            Process runningProcess = queuesByPriority.values().stream()
                    .filter(queue -> !queue.isEmpty())
                    .map(Queue::peek)
                    .filter(Process::isRunning)
                    .findFirst()
                    .orElse(null);

            if (runningProcess != null)
                runningProcess.increaseRuntime();

            Queue<Process> highestPriorityNonEmptyQueue = queuesByPriority.entrySet().stream()
                    .sorted(Comparator.comparingInt(Map.Entry::getKey))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(null);

            boolean processIsFromHighestPriority = highestPriorityNonEmptyQueue.contains(runningProcess);

            currentTime++;
        }
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
