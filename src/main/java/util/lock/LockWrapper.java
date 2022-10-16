package util.lock;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Getter
public class LockWrapper {
    private final Lock lock = new ReentrantLock();
    private final AtomicInteger numberOfThreadsInQueue = new AtomicInteger(1);

    public LockWrapper addThreadInQueue() {
        numberOfThreadsInQueue.incrementAndGet();
        return this;
    }

    public int removeThreadFromQueue() {
        return numberOfThreadsInQueue.decrementAndGet();
    }
}
