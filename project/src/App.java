import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) throws Exception {
        Path path = Path.of("input.txt");

        Queue<Process> processes = new LinkedList<>(
                Files.lines(path)
                        .map(line -> {
                            String[] split = line.split(";");
                            return new Process(
                                    split[0],
                                    Integer.parseInt(split[1]),
                                    Integer.parseInt(split[2]),
                                    Integer.parseInt(split[3]));
                        })
                        .sorted((a, b) -> a.time() - b.time())
                        .toList());

        Map<Integer, Queue<Process>> queuesByPriority = processes.stream()
                .map(Process::priority)
                .distinct()
                .collect(Collectors.toMap(
                        priority -> priority,
                        priority -> new LinkedList<>()));

        // while (!processes.isEmpty() && queuesByPriority.keySet().stream().anyMatch((p, queue) -> )))
    }
}
