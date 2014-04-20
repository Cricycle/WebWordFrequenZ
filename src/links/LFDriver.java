package links;

import java.util.ArrayList;
import java.util.concurrent.PriorityBlockingQueue;

import util.Driver;
import util.PageInfo;

public class LFDriver extends Driver
	implements Runnable
{

	private final PriorityBlockingQueue<PageInfo> download_inboundQueue;
	private final PriorityBlockingQueue<PageInfo> download_outboundQueue;
	private final PriorityBlockingQueue<PageInfo> delete_outboundQueue;

	public LFDriver(PriorityBlockingQueue<PageInfo> download_inboundQueue,
			PriorityBlockingQueue<PageInfo> download_outboundQueue,
			PriorityBlockingQueue<PageInfo> delete_outboundQueue)
	{
		this.download_inboundQueue = download_inboundQueue;
		this.download_outboundQueue = download_outboundQueue;
		this.delete_outboundQueue = delete_outboundQueue;
	}

	public void run()
	{
		ArrayList<Thread> threads = new ArrayList<Thread>();
		while (true)
		{
			try
			{
				PageInfo pageInfo = download_inboundQueue.take();
				Thread t = new Thread(new LinkFinder(pageInfo, download_outboundQueue, delete_outboundQueue));
				threads.add(t);
				t.start();
			}
			catch (InterruptedException e)
			{
				break;
			}
		}

		while (!download_inboundQueue.isEmpty()) {
			delete_outboundQueue.add(download_inboundQueue.poll());
		}

		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
		System.err.printf("LinkFinderDriver has exited.%n");
	}

}
