package main;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

import links.LFDriver;
import util.PageInfo;
import web.PDDriver;
import analyze.PADriver;
import deleter.DDriver;

public class MainDriver
{

	public void run(String basePageURL, int maxHopCount, int maxNumberOfPages)
	{
		// create buffers between tasks

		URL startURL;
		try
		{
			startURL = new URL(basePageURL);
		}
		catch (MalformedURLException e)
		{
			throw new RuntimeException("Base webpage URL is malformed", e);
		}

		PriorityBlockingQueue<PageInfo> finderToDownloaderQueue = new PriorityBlockingQueue<PageInfo>();
		PriorityBlockingQueue<PageInfo> downloaderToFinderQueue = new PriorityBlockingQueue<PageInfo>();
		PriorityBlockingQueue<PageInfo> downloaderToAnalyzerQueue = new PriorityBlockingQueue<PageInfo>();
		PriorityBlockingQueue<PageInfo> toDeleterQueue = new PriorityBlockingQueue<PageInfo>();

		PageInfo basePageInfo = new PageInfo(startURL, maxHopCount);
		finderToDownloaderQueue.add(basePageInfo);

		// start drivers for each task

		Semaphore executionSemaphore = new Semaphore(4, true);

		PDDriver pdDriver = new PDDriver(maxNumberOfPages,
				finderToDownloaderQueue, downloaderToFinderQueue,
				downloaderToAnalyzerQueue);
		LFDriver lfDriver = new LFDriver(downloaderToFinderQueue,
				finderToDownloaderQueue, toDeleterQueue);
		PADriver analysisDriver = new PADriver(downloaderToAnalyzerQueue,
				toDeleterQueue);
		DDriver deletionDriver = new DDriver(toDeleterQueue, 3); // retry failed deletion 3 times

		Thread downloaderThread = new Thread(pdDriver, "DownloaderThread");
		Thread finderThread = new Thread(lfDriver, "FinderThread");
		Thread analyzerThread = new Thread(analysisDriver, "AnalyzerThread");
		Thread deleterThread = new Thread(deletionDriver, "DeleterThread");

		downloaderThread.start();
		finderThread.start();
		analyzerThread.start();
		deleterThread.start();

		while (true)
		{
			try
			{
				Thread.sleep(1000);

				// block driver threads from executing
				executionSemaphore.acquire(4);

				// check if we are done
				if (pdDriver.allThreadsFinished()
						&& lfDriver.allThreadsFinished()
						&& analysisDriver.allThreadsFinished()
						&& deletionDriver.allThreadsFinished()
						&& finderToDownloaderQueue.isEmpty()
						&& downloaderToFinderQueue.isEmpty()
						&& downloaderToAnalyzerQueue.isEmpty()
						&& toDeleterQueue.isEmpty())
				{
					break;
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		System.err.println("Page count: " + PDDriver.getPageCount());

		try
		{
			downloaderThread.interrupt();
			downloaderThread.join();
			
			finderThread.interrupt();
			finderThread.join();
			
			analyzerThread.interrupt();
			analyzerThread.join();
			
			deleterThread.interrupt();
			deleterThread.join();
		}
		catch (InterruptedException e)
		{
			// very bad things happened
			e.printStackTrace();
		}
	}

}
