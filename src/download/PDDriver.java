package download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import main.MainDriver;
import util.Driver;
import util.PageInfo;

public class PDDriver extends Driver
	implements Runnable
{

	private AtomicInteger webpageCount = new AtomicInteger(0);
	
	/**
	 * Shared semaphore to allow taking from a queue
	 */
	private final Semaphore executionSemaphore;
	
	private final int MAX_NUM_PAGES;
	private final PriorityBlockingQueue<PageInfo> inboundQueue;
	private final PriorityBlockingQueue<PageInfo> linkOutboundQueue, analysisOutboundQueue;
	private HashSet<PageInfo> downloadedPages = new HashSet<PageInfo>();
	private PrintWriter pageListWriter;

	public PDDriver(int MAX_NUM_PAGES, Semaphore executionSemaphore,
			PriorityBlockingQueue<PageInfo> inboundQueue,
			PriorityBlockingQueue<PageInfo> linkOutboundQueue,
			PriorityBlockingQueue<PageInfo> analysisOutboundQueue)
	{
		this.MAX_NUM_PAGES = MAX_NUM_PAGES;
		this.inboundQueue = inboundQueue;
		this.linkOutboundQueue = linkOutboundQueue;
		this.analysisOutboundQueue = analysisOutboundQueue;
		this.executionSemaphore = executionSemaphore;

		try
		{
			pageListWriter = new PrintWriter(new File(MainDriver.ANALYSIS_FOLDER
					+ "/page_list.txt"));
		}
		catch (FileNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void run()
	{
		ArrayList<Thread> threads = new ArrayList<Thread>();
		while (getPageCount() < MAX_NUM_PAGES)
		{
			try
			{
				synchronized (inboundQueue) {
					while (inboundQueue.isEmpty()) { inboundQueue.wait(); }
				}
				executionSemaphore.acquire();
				PageInfo pageInfo = inboundQueue.take();
				
				if (downloadedPages.add(pageInfo))
				{
					incrementThreadCount();
					// start a downloader thread
					Thread t = new Thread(new PageDownloader(pageInfo, this,
							linkOutboundQueue, analysisOutboundQueue));
					threads.add(t);
					t.start();

					// write webpage to file
					pageListWriter.println(pageInfo.url.toExternalForm());

					executionSemaphore.release();
					
					Thread.sleep(200); // don't kill websites
				} else {
					executionSemaphore.release();
				}
			}
			catch (InterruptedException e)
			{
				break;
			}
		}

		pageListWriter.close();
		
		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
		
		while (getPageCount() >= MAX_NUM_PAGES) {
			try {
				inboundQueue.take();
			} catch (InterruptedException e) {
				break;
			}
		}
		
		System.out.printf("PageDownloadDriver has exited.%n");
	}

	public int getPageCount()
	{
		return webpageCount.get();
	}

	public int incrementPageCount()
	{
		return webpageCount.incrementAndGet();
	}

}
