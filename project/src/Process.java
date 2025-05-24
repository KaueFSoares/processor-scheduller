public class Process {

    private final String id;
    private final int time;
    private final int burst;
    private final int priority;

    private int runtime = 0;

    public Process(String id, int time, int burst, int priority) {
        this.id = id;
        this.time = time;
        this.burst = burst;
        this.priority = priority;
    }

    public synchronized void increaseRuntime() {
        if (runtime + 1 > burst)
            throw new IllegalArgumentException();

        this.runtime += 1;
    }

    public int runtime() {
        return runtime;
    }

    public boolean isFinished() {
        return burst == runtime;
    }

    public String id() {
        return id;
    }

    public int burst() {
        return burst;
    }

    public int priority() {
        return priority;
    }

    public int time() {
        return time;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof Process))
            return false;

        return ((Process) obj).id().equals(this.id());
    }


    @Override
    public String toString() {
        return String.format("Process(id=%s;burst=%d;priority=%d;time=%d;runtime=%d)", id, burst, priority, time, runtime);
    }
}
