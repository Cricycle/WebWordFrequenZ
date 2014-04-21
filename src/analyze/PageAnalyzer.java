package analyze;

import java.util.concurrent.PriorityBlockingQueue;

import util.Driver;
import util.PageInfo;

/**
 * Abstract superclass for all classes which analyze PageInfo
 * @author Alex
 *
 */
public abstract class PageAnalyzer implements Runnable {
	
	/**
	 * The downloaded page that we are going to analyze
	 */
	private final PageInfo pi;
	
	/**
	 * Outbound queue, to indicate that PageInfo has been analyzed
	 */
	private final PriorityBlockingQueue<PageInfo> outboundQueue;
	
	/**
	 * Parent Driver to which we report completion
	 */
	private final Driver parentDriver;
	
	/**
	 * Creates a new PageAnalyzer, which has a final implementation of
	 * the run() method.
	 * @param pi The PageInfo to be analyzed
	 * @param outboundQueue The outboundQueue of finished PageInfo
	 */
	public PageAnalyzer(PageInfo pi, Driver parentDriver,
			PriorityBlockingQueue<PageInfo> outboundQueue) {
		this.pi = pi;
		this.outboundQueue = outboundQueue;
		this.parentDriver = parentDriver;
	}
	
	/**
	 * Analyzes the PageInfo, then adds the PageInfo to the outbound Queue
	 */
	public final void run() {
		analyze(pi);
		outboundQueue.add(pi);
		synchronized(outboundQueue) {
			outboundQueue.notify();
		}
		parentDriver.decrementThreadCount();
	}
	
	/**
	 * Implement the class specific analysis code
	 * @param pi The page info of the page to be analyzed
	 */
	protected abstract void analyze(PageInfo pi);
	
}
