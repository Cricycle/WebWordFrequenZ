package analyze;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
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
	
	/**
	 * While there are still things on the inboundQueue, the PageInfo is taken, passed
	 * to a new WorkCountAnalyzer, and finally we write all the analyzed data to a file.
	 */
	public void run() {
		ConcurrentHashMap<String, Integer> sharedWordCount = new ConcurrentHashMap<String, Integer>();
		ArrayList<Thread> threads = new ArrayList<Thread>();
		while (true) {
			try {
				PageInfo pi = inboundQueue.take();
				Thread t = new Thread(new WordCountAnalyzer(pi, sharedWordCount, outboundQueue));
				threads.add(t);
				t.start();
			} catch (InterruptedException e) {
				break;
			}
		}
		
		while (!inboundQueue.isEmpty()) {
			outboundQueue.add(inboundQueue.poll());
		}
		
		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
		
		WordCountAnalyzer.saveDataToFile("word_counts.txt", sharedWordCount);
		System.err.printf("PageAnalysisDriver has exited.%n");
	}
	
}
