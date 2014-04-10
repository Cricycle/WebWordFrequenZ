package analyze;

import java.util.concurrent.PriorityBlockingQueue;

import util.PageInfo;

/**
 * Page Analysis Driver.  It accepts PageInfo objects which indicate a
 * downloaded file from the inboundQueue, and pushes PageInfo objects which
 * have been analyzed to the outboundQueue.
 * @author Alex
 *
 */
public class PADriver
	implements Runnable
{
	
	/**
	 * A queue which expects PageInfo about already downloaded webpages
	 */
	private final PriorityBlockingQueue<PageInfo> inboundQueue;
	
	/**
	 * A queue which indicates that the queued PageInfo has been analyzed
	 */
	private final PriorityBlockingQueue<PageInfo> outboundQueue;
	
	/**
	 * @param inboundQueue Queue with elements indicating files to be analyzed
	 * @param outboundQueue Queue with elements indicating files already analyzed 
	 */
	public PADriver(PriorityBlockingQueue<PageInfo> inboundQueue,
			PriorityBlockingQueue<PageInfo> outboundQueue) {
		this.inboundQueue = inboundQueue;
		this.outboundQueue = outboundQueue;
	}
	
	public void run() {
		while (true) {
			try {
				PageInfo pi = inboundQueue.take();
				if (pi == PageInfo.END)
					break;
				Thread t = new Thread(new WordCountAnalyzer(pi, outboundQueue));
				t.start();
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
				break;
			}
		}
	}
	
}
