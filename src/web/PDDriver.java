package web;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import util.PageInfo;

public class PDDriver
	implements Runnable
{

	private static AtomicInteger webpageCount = new AtomicInteger();

	private final int MAX_NUM_PAGES;
	private final PriorityBlockingQueue<PageInfo> inboundQueue;
	private final PriorityBlockingQueue<PageInfo> linkOutboundQueue, analysisOutboundQueue;

	public PDDriver(int MAX_NUM_PAGES,
			PriorityBlockingQueue<PageInfo> inboundQueue,
			PriorityBlockingQueue<PageInfo> linkOutboundQueue,
			PriorityBlockingQueue<PageInfo> analysisOutboundQueue)
	{
		this.MAX_NUM_PAGES = MAX_NUM_PAGES;
		this.inboundQueue = inboundQueue;
		this.linkOutboundQueue = linkOutboundQueue;
		this.analysisOutboundQueue = analysisOutboundQueue;

		resetPageCount();
	}

	@Override
	public void run()
	{
		while (PDDriver.getPageCount() < MAX_NUM_PAGES)
		{
			try
			{
				PageInfo pageInfo = inboundQueue.take();
				Thread t = new Thread(new PageDownloader(pageInfo, linkOutboundQueue, analysisOutboundQueue));
				t.start();
				Thread.sleep(500); // don't kill websites
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static int getPageCount()
	{
		return webpageCount.get();
	}

	public static int incrementPageCount()
	{
		return webpageCount.incrementAndGet();
	}

	private static void resetPageCount()
	{
		webpageCount.set(0);
	}

}
