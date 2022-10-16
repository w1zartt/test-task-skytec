package util.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class UserThreadLocker {
    private static final ConcurrentHashMap<Long, LockWrapper> locks = new ConcurrentHashMap<>();

    public void lock(Long key) {
        log.error("LOCKING FOR USER ID {}. FROM MAP {}", key, locks.get(key));
        LockWrapper lockWrapper = locks.compute(key, (k, v) -> v == null ? new LockWrapper() : v.addThreadInQueue());
        lockWrapper.getLock().lock();
    }

    public void unlock(Long key) {
        LockWrapper lockWrapper = locks.get(key);
        lockWrapper.getLock().unlock();
        if (lockWrapper.removeThreadFromQueue() == 0) {
            locks.remove(key, lockWrapper);
        }
    }
}
