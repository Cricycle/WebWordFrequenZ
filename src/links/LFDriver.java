package links;

import java.util.ArrayList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

import util.Driver;
import util.PageInfo;

public class LFDriver extends Driver
	implements Runnable
{

	/**
	 * Shared semaphore to allow taking from a queue
	 */
	private final Semaphore executionSemaphore;

	private final PriorityBlockingQueue<PageInfo> download_inboundQueue;
	private final PriorityBlockingQueue<PageInfo> download_outboundQueue;
	private final PriorityBlockingQueue<PageInfo> delete_outboundQueue;

	public LFDriver(PriorityBlockingQueue<PageInfo> download_inboundQueue,
			Semaphore executionSemaphore,
			PriorityBlockingQueue<PageInfo> download_outboundQueue,
			PriorityBlockingQueue<PageInfo> delete_outboundQueue)
	{
		this.download_inboundQueue = download_inboundQueue;
		this.download_outboundQueue = download_outboundQueue;
		this.delete_outboundQueue = delete_outboundQueue;
		this.executionSemaphore = executionSemaphore;
	}

	@Override
	public void run()
	{
		ArrayList<Thread> threads = new ArrayList<Thread>();
		while (true)
		{
			try
			{
				synchronized (download_inboundQueue)
				{
					while (download_inboundQueue.isEmpty())
					{
						download_inboundQueue.wait();
					}
				}
				executionSemaphore.acquire();
				PageInfo pageInfo = download_inboundQueue.take();
				incrementThreadCount();
				executionSemaphore.release();

				Thread t = new Thread(new LinkFinder(pageInfo, this,
						download_outboundQueue, delete_outboundQueue));
				threads.add(t);
				t.start();
			}
			catch (InterruptedException e)
			{
				break;
			}
		}

		while (!download_inboundQueue.isEmpty())
		{
			delete_outboundQueue.add(download_inboundQueue.poll());
		}
		synchronized (delete_outboundQueue)
		{
			delete_outboundQueue.notify();
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
		System.out.printf("LinkFinderDriver has exited.%n");
	}

}
