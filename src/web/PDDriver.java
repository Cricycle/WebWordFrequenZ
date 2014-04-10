package web;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import util.PageInfo;

public class PDDriver
	implements Runnable
{

	private static AtomicInteger webpageCount = new AtomicInteger();

	private final int MAX_NUM_PAGES;
	private final PriorityBlockingQueue<PageInfo> inboundQueue, outboundQueue;

	public PDDriver(int MAX_NUM_PAGES,
			PriorityBlockingQueue<PageInfo> inboundQueue,
			PriorityBlockingQueue<PageInfo> outboundQueue)
	{
		this.MAX_NUM_PAGES = MAX_NUM_PAGES;
		this.inboundQueue = inboundQueue;
		this.outboundQueue = outboundQueue;
	}

	@Override
	public void run()
	{
		while (PDDriver.getPageCount() < MAX_NUM_PAGES)
		{
			try
			{
				PageInfo pageInfo = inboundQueue.take();
				Thread t = new Thread(new PageDownloader(pageInfo, outboundQueue));
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

}
