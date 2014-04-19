package main;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.PriorityBlockingQueue;

import links.LFDriver;
import util.PageInfo;
import web.PDDriver;
import analyze.PADriver;

public class MainDriver
{

	public static void run(String basePageURL, int maxHopCount, int maxNumberOfPages)
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

		PageInfo basePageInfo = new PageInfo(startURL, maxHopCount);
		PriorityBlockingQueue<PageInfo> finderToDownloaderQueue = new PriorityBlockingQueue<PageInfo>();
		finderToDownloaderQueue.add(basePageInfo);

		PriorityBlockingQueue<PageInfo> downloaderToFinderQueue = new PriorityBlockingQueue<PageInfo>();
		PriorityBlockingQueue<PageInfo> downloaderToAnalyzerQueue = new PriorityBlockingQueue<>();
		PriorityBlockingQueue<PageInfo> toDeleterQueue = new PriorityBlockingQueue<>();

		// start drivers for each task

		PDDriver pdDriver = new PDDriver(maxNumberOfPages,
				finderToDownloaderQueue, downloaderToFinderQueue,
				downloaderToAnalyzerQueue);
		LFDriver lfDriver = new LFDriver(maxNumberOfPages,
				downloaderToFinderQueue, finderToDownloaderQueue);
		PADriver analysisDriver = new PADriver(downloaderToAnalyzerQueue, toDeleterQueue);

		Thread downloaderThread = new Thread(pdDriver, "DownloaderThread");
		Thread finderThread = new Thread(lfDriver, "FinderThread");
		Thread analyzerThread = new Thread(analysisDriver, "AnalyzerThread");

		downloaderThread.setDaemon(true);
		finderThread.setDaemon(true);
		analyzerThread.setDaemon(true);

		downloaderThread.start();
		finderThread.start();
		analyzerThread.start();

		// wait until all pages have been downloaded
		while (PDDriver.getPageCount() < maxNumberOfPages);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (!downloaderToAnalyzerQueue.isEmpty());
		downloaderToAnalyzerQueue.add(PageInfo.END);
		try {
			analyzerThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
