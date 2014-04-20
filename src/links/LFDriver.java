package links;

import java.util.ArrayList;
import java.util.concurrent.PriorityBlockingQueue;

import util.PageInfo;
import web.PDDriver;

public class LFDriver
	implements Runnable
{

	private final int MAX_NUM_PAGES;
	private final PriorityBlockingQueue<PageInfo> download_inboundQueue;
	private final PriorityBlockingQueue<PageInfo> download_outboundQueue;
	private final PriorityBlockingQueue<PageInfo> delete_outboundQueue;

	public LFDriver(int MAX_NUM_PAGES,
			PriorityBlockingQueue<PageInfo> download_inboundQueue,
			PriorityBlockingQueue<PageInfo> download_outboundQueue,
			PriorityBlockingQueue<PageInfo> delete_outboundQueue)
	{
		this.MAX_NUM_PAGES = MAX_NUM_PAGES;
		this.download_inboundQueue = download_inboundQueue;
		this.download_outboundQueue = download_outboundQueue;
		this.delete_outboundQueue = delete_outboundQueue;
	}

	public void run()
	{
		ArrayList<Thread> threads = new ArrayList<Thread>();
		while (PDDriver.getPageCount() < MAX_NUM_PAGES)
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
				e.printStackTrace();
				break;
			}
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
