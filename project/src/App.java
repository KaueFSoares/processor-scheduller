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
