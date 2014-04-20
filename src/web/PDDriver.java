package web;

import java.util.HashSet;
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
	private HashSet<PageInfo> downloadedPages = new HashSet<PageInfo>();

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
				if (downloadedPages.add(pageInfo))
				{
					Thread t = new Thread(new PageDownloader(pageInfo,
							linkOutboundQueue, analysisOutboundQueue));
					t.start();
					Thread.sleep(500); // don't kill websites
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				break;
			}
		}
		System.err.printf("PageDownloadDriver has exited.%n");
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
