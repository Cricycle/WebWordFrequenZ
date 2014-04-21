package delete;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

import util.Driver;
import util.PageInfo;

/**
 * Manages creation of all PageDeleter threads.
 * Once a PageInfo object has been sent to the queue exactly twice, the
 * downloaded web page that it references will be deleted.
 * @author Alex
 *
 */
public class DDriver extends Driver 
	implements Runnable {
	
	/**
	 * Shared semaphore to allow taking from a queue
	 */
	private final Semaphore executionSemaphore;
	
	/**
	 * InboundQueue of PageInfo instances which have been used
	 */
	private PriorityBlockingQueue<PageInfo> inboundQueue;
	
	/**
	 * A data structure with O(1) method to check existence
	 */
	private HashSet<PageInfo> occurrences;
	
	/**
	 * Number of times the PageDeleter threads should try to delete the file
	 * if it fails for some reason.
	 */
	private int retryCount;
	
	/**
	 * Creates the DDriver, with a specific connecting queue which indicates
	 * completed processing. There should only be one instance of DDriver
	 * @param inboundQueue The queue which indicates PageInfo objects have been
	 * processed.
	 * @param retryCnt The number of times the PageDeleter threads should retry
	 * if they fail to delete the file.
	 */
	public DDriver(PriorityBlockingQueue<PageInfo> inboundQueue,
			Semaphore executionSemaphore, int retryCnt) {
		this.inboundQueue = inboundQueue;
		this.occurrences = new HashSet<PageInfo>();
		this.executionSemaphore = executionSemaphore;
		retryCount = retryCnt;
	}
	
	@Override
	public void run() {
		// Remember references to the threads that have been created
		ArrayList<Thread> threads = new ArrayList<Thread>();
		
		// Loop forever until either Interrupted or PageInfo
		while (true) {
			try {
				// Get the next PageInfo object from the queue
				synchronized (inboundQueue) {
					while (inboundQueue.isEmpty()) { inboundQueue.wait(); }
				}
				executionSemaphore.acquire();
				PageInfo pi = inboundQueue.take();
				
				if (!occurrences.contains(pi)) {
					// If we haven't seen this PageInfo object before, add it
					occurrences.add(pi);
				} else {
					// We have seen it before, now we delete it.
					incrementThreadCount();
					Thread t = new Thread(new PageDeleter(pi, this, retryCount));
					threads.add(t);
					t.start();
				}
				executionSemaphore.release();
				
				// Wait a short time period to slow the creation of new threads
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// We were interrupted, should exit the loop immediately.
				break;
			}
		}
		
		// Iterate through the created threads, make sure they have all finished
		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
		System.out.printf("DeletionDriver has exited.%n");
	}

}
