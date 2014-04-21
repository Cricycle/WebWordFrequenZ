package analyze;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

import util.Driver;
import util.PageInfo;

/**
 * Page Analysis Driver. It accepts PageInfo objects which indicate a downloaded
 * file from the inboundQueue, and pushes PageInfo objects which have been
 * analyzed to the outboundQueue.
 * 
 * @author Alex
 * 
 */
public class PADriver extends Driver
	implements Runnable
{

	/**
	 * Shared semaphore to allow taking from a queue
	 */
	private final Semaphore executionSemaphore;

	/**
	 * A queue which expects PageInfo about already downloaded webpages
	 */
	private final PriorityBlockingQueue<PageInfo> inboundQueue;

	/**
	 * A queue which indicates that the queued PageInfo has been analyzed
	 */
	private final PriorityBlockingQueue<PageInfo> outboundQueue;

	/**
	 * @param inboundQueue
	 *            Queue with elements indicating files to be analyzed
	 * @param outboundQueue
	 *            Queue with elements indicating files already analyzed
	 */
	public PADriver(PriorityBlockingQueue<PageInfo> inboundQueue,
			Semaphore executionSemaphore,
			PriorityBlockingQueue<PageInfo> outboundQueue)
	{
		this.inboundQueue = inboundQueue;
		this.outboundQueue = outboundQueue;
		this.executionSemaphore = executionSemaphore;
	}

	/**
	 * While there are still things on the inboundQueue, the PageInfo is taken,
	 * passed to a new WorkCountAnalyzer, and finally we write all the analyzed
	 * data to a file.
	 */
	@Override
	public void run()
	{
		ConcurrentHashMap<String, Integer> sharedWordCount = new ConcurrentHashMap<String, Integer>();
		ArrayList<Thread> threads = new ArrayList<Thread>();
		while (true)
		{
			try
			{
				synchronized (inboundQueue)
				{
					while (inboundQueue.isEmpty())
					{
						inboundQueue.wait();
					}
				}
				executionSemaphore.acquire();
				PageInfo pi = inboundQueue.take();
				this.incrementThreadCount();
				executionSemaphore.release();

				Thread t = new Thread(new WordCountAnalyzer(pi, this,
						sharedWordCount, outboundQueue));
				threads.add(t);
				t.start();
			}
			catch (InterruptedException e)
			{
				break;
			}
		}

		while (!inboundQueue.isEmpty())
		{
			outboundQueue.add(inboundQueue.poll());
		}
		synchronized (outboundQueue)
		{
			outboundQueue.notify();
		}

		for (Thread t : threads)
		{
			try
			{
				t.join();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				break;
			}
		}

		WordCountAnalyzer.saveDataToFile("word_counts.txt", sharedWordCount);
		System.out.printf("PageAnalysisDriver has exited.%n");
	}

}
