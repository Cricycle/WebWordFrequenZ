package util;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Driver
{

	private AtomicInteger unfinishedThreadCount = new AtomicInteger(0);

	public void incrementThreadCount()
	{
		unfinishedThreadCount.incrementAndGet();
	}

	public void decrementThreadCount()
	{
		unfinishedThreadCount.decrementAndGet();
	}

	public boolean allThreadsFinished()
	{
		return (unfinishedThreadCount.get() == 0);
	}

}
