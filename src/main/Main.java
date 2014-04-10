package main;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.PriorityBlockingQueue;

import analyze.PADriver;
import links.LFDriver;
import util.PageInfo;
import web.PDDriver;

public class Main
{

	public static final String DOWNLOAD_FOLDER = "downloaded_pages";
	public static final String ANALYSIS_FOLDER = "analyzed_data";

	public static void main(String[] args)
	{
		if (args.length != 3)
		{
			System.err
					.println("Usage: basePageURL maxHopCount maxNumberOfPages");
			System.exit(1);
		}

		String basePageURL = args[0];
		int maxHopCount = Integer.parseInt(args[1]);
		int maxNumberOfPages = Integer.parseInt(args[2]);

		// make folder to store downloaded webpages
		File folder = new File(Main.DOWNLOAD_FOLDER);
		folder.mkdirs();
		folder = new File(Main.ANALYSIS_FOLDER);
		folder.mkdirs();

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
