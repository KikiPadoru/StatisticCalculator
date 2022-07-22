import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class StatisticCalculator {
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RESET = "\u001B[0m";
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private double min = Double.MAX_VALUE;
    private double max = Double.MIN_VALUE;
    private long counter = 0;
    private double sum = 0;
    public void add(double v) {
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        min = Math.min(min, v);
        max = Math.max(max, v);
        sum = +v;
        counter++;
        writeLock.unlock();
    }

    public List<Double> get() {
        Lock readLock = lock.readLock();
        List<Double> result = new LinkedList<>();
        readLock.lock();
        result.add(sum / counter);
        result.add(max);
        result.add(min);
        readLock.unlock();
        return result;
    }


    public static void main(String[] args) throws InterruptedException {
        StatisticCalculator statisticCalculator = new StatisticCalculator();
        Random random = new Random();
        for (int i = 0; i < 12; i++) {
            new Thread(() -> {
                statisticCalculator.add(random.nextDouble() * 100000);
                System.out.println(ANSI_CYAN+Thread.currentThread().getName()
                        +ANSI_RESET+ "\n"
                        + statisticCalculator.get());
            }).start();
        }
    }

}
