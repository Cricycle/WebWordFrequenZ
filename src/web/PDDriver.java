package web;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import main.Displayer;
import util.PageInfo;

public class PDDriver
	implements Runnable
{

	private static AtomicInteger webpageCount = new AtomicInteger();

	private final int MAX_NUM_PAGES;
	private final PriorityBlockingQueue<PageInfo> inboundQueue;
	private final PriorityBlockingQueue<PageInfo> linkOutboundQueue, analysisOutboundQueue;
	private HashSet<PageInfo> downloadedPages = new HashSet<PageInfo>();
	private PrintWriter pageListWriter;

	public PDDriver(int MAX_NUM_PAGES,
			PriorityBlockingQueue<PageInfo> inboundQueue,
			PriorityBlockingQueue<PageInfo> linkOutboundQueue,
			PriorityBlockingQueue<PageInfo> analysisOutboundQueue)
	{
		this.MAX_NUM_PAGES = MAX_NUM_PAGES;
		this.inboundQueue = inboundQueue;
		this.linkOutboundQueue = linkOutboundQueue;
		this.analysisOutboundQueue = analysisOutboundQueue;

		try
		{
			pageListWriter = new PrintWriter(new File(Displayer.ANALYSIS_FOLDER
					+ "/page_list.txt"));
		}
		catch (FileNotFoundException e)
		{
			throw new RuntimeException(e);
		}

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
					// start a downloader thread
					Thread t = new Thread(new PageDownloader(pageInfo,
							linkOutboundQueue, analysisOutboundQueue));
					t.start();
					
					// write webpage to file
					pageListWriter.println(pageInfo.url.toExternalForm());
					
					Thread.sleep(200); // don't kill websites
				}
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		pageListWriter.close();
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
