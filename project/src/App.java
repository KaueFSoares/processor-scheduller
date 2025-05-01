import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App {
    public static void main(String[] args) throws Exception {
        Path path = Path.of("input.txt");

        Queue<Process> processes = parseFileContentIntoProcesses(path);

        System.out.println("Processes (file content):");
        processes.forEach(System.out::println);

        Map<Integer, Queue<Process>> queuesByPriority = processes.stream()
                .map(Process::priority)
                .distinct()
                .collect(Collectors.toMap(
                        priority -> priority,
                        priority -> new LinkedList<>()));

        int currentTime = 0;

        while (true) {
            boolean missingProcesses = !processes.isEmpty();
            boolean allQueuesAreEmpty = queuesByPriority.entrySet().stream().anyMatch(entry -> !entry.getValue().isEmpty());

            // TODO: validate if all bursts have been spent

            if (!missingProcesses && !allQueuesAreEmpty) break;

        }
    }

    private static Queue<Process> parseFileContentIntoProcesses(Path path) throws IOException {
        try (Stream<String> lines = Files.lines(path)) {
            return new LinkedList<>(
                    lines
                            .map(line -> {
                                String[] split = line.split(";");
                                return new Process(
                                        split[0],
                                        Integer.parseInt(split[1]),
                                        Integer.parseInt(split[2]),
                                        Integer.parseInt(split[3]));
                            })
                            .sorted(Comparator.comparingInt(Process::time))
                            .toList()
            );
        }
    }
}
