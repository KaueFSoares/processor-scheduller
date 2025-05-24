public class Timer {
    private int currentTime = 0;

    public synchronized void increaseTime() {
        currentTime++;
    }

    public int currentTime() {
        return currentTime;
    }
}
