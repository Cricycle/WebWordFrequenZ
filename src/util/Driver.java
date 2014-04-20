package util;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Driver {
	private AtomicInteger unfinishedThreadCount;
	
	public void incrementThreadCount() {
		unfinishedThreadCount.incrementAndGet();
	}
	
	public void decrementThreadCount() {
		unfinishedThreadCount.decrementAndGet();
	}
	
	public int getUnfinishedThreadCount() {
		return unfinishedThreadCount.get();
	}
}
