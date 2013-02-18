package cb;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CircuitBreaker
{
    private final int failureThreshold;
    private AtomicInteger failureCount;
    private final int timeUntilRetry;
    private CircuitBreakerStatus status;
    private AtomicLong lastOpenedTime;
    private final String name;
    private AtomicInteger openCount;
    private final Lock lock = new ReentrantLock();
    
    
    public CircuitBreaker(String name, int failureThreshold, int timeUntilRetry)
    {
        this.name = name;
        this.failureThreshold = failureThreshold;
        this.timeUntilRetry = timeUntilRetry;
        this.status = CircuitBreakerStatus.CLOSED;
        this.lastOpenedTime = new AtomicLong();
        this.openCount = new AtomicInteger();
        this.failureCount = new AtomicInteger();
    }

    public String getName()
    {
        return name;
    }

    public void setStatus(final CircuitBreakerStatus status)
    {
    	lock.lock();
    	try{
    		this.status = status;
    	}finally{
    		lock.unlock();
    	}
    }

    public CircuitBreakerStatus getStatus()
    {
        return status;
    }

    public void setLastOpenedTime(final long lastOpenedTime)
    {
        this.lastOpenedTime.set(lastOpenedTime);
    }

    public long getLastOpenedTime()
    {
        return lastOpenedTime.get();
    }

    public void addFailure()
    {
        failureCount.incrementAndGet();
    }

    private void setFailureCount(final int failureCount)
    {
        this.failureCount.set(failureCount);
    }

    public int getFailureCount()
    {
        return failureCount.get();
    }

    public boolean isWaitTimeExceeded()
    {
        return System.currentTimeMillis() - timeUntilRetry > lastOpenedTime.get();
    }

    public boolean isThresholdReached()
    {
        return getFailureCount() >= getFailureThreshold();
    }

    public int getFailureThreshold()
    {
        return failureThreshold;
    }

    public void open()
    {
        setLastOpenedTime(System.currentTimeMillis());
        setStatus(CircuitBreakerStatus.OPEN);
        openCount.incrementAndGet();
    }

    public void openHalf()
    {
        setStatus(CircuitBreakerStatus.HALF_OPEN);
    }

    public void close()
    {
        setFailureCount(0);
        setStatus(CircuitBreakerStatus.CLOSED);
    }

    public boolean isOpen()
    {
        return getStatus() == CircuitBreakerStatus.OPEN;
    }

    public boolean isHalfOpen()
    {
        return getStatus() == CircuitBreakerStatus.HALF_OPEN;
    }

    public boolean isClosed()
    {
        return getStatus() == CircuitBreakerStatus.CLOSED;
    }

    public int getOpenCount()
    {
        return openCount.get();
    }
}