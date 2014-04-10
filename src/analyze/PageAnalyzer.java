package analyze;

import java.util.concurrent.PriorityBlockingQueue;

import util.PageInfo;

public abstract class PageAnalyzer implements Runnable {
	
	/**
	 * The downloaded page that we are going to analyze
	 */
	private final PageInfo pi;
	
	/**
	 * Outbound queue, to indicate that PageInfo has been analyzed
	 */
	private final PriorityBlockingQueue<PageInfo> outboundQueue;
	
	public PageAnalyzer(PageInfo pi,
			PriorityBlockingQueue<PageInfo> outboundQueue) {
		this.pi = pi;
		this.outboundQueue = outboundQueue;
	}
	
	public final void run() {
		analyze(pi);
		outboundQueue.add(pi);
	}
	
	protected abstract void analyze(PageInfo pi);
}
