import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Stream;

public class App {

    public static void main(String[] args) throws Exception {
        Path path = Path.of("input.txt");

        Queue<Process> processes = parseFileContentIntoProcesses(path);

        System.out.println("Processes (file content):");
        processes.forEach(System.out::println);

        Timer timer = new Timer();
        ProcessScheduler processScheduler = new ProcessScheduler();

        while (true) {
            boolean noProcesses = processes.isEmpty();
            boolean processSchedulerIsReadyToStop = processScheduler.readyToStop();

            if (noProcesses && processSchedulerIsReadyToStop)
                break;

            timer.increaseTime();

            System.out.println("Current time: " + timer.currentTime());

            while (!processes.isEmpty() && processes.peek().time() == timer.currentTime())
                processScheduler.addProcess(processes.poll());

            processScheduler.increaseTime(timer.currentTime());
        }

        System.out.printf("Average time for processes: %.4f", processScheduler.summary());
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

        if (split.length != 4)
            throw new IllegalArgumentException("Invalid line length");

        String id = split[0];
        int time = Integer.parseInt(split[1]);
        int burst = Integer.parseInt(split[2]);
        int priority = Integer.parseInt(split[3]);

        return new Process(id, time, burst, priority
        );
    }
}
