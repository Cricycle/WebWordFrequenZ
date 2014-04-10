package main;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.PriorityBlockingQueue;

import links.LFDriver;
import util.PageInfo;
import web.PDDriver;

public class Main
{

	public static final String FOLDER_NAME = "downloaded_pages";

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
		File folder = new File(Main.FOLDER_NAME);
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

		// start drivers for each task

		PDDriver pdDriver = new PDDriver(maxNumberOfPages,
				finderToDownloaderQueue, downloaderToFinderQueue);
		LFDriver lfDriver = new LFDriver(maxNumberOfPages,
				downloaderToFinderQueue, finderToDownloaderQueue);

		Thread downloaderThread = new Thread(pdDriver, "DownloaderThread");
		Thread finderThread = new Thread(lfDriver, "FinderThread");

		downloaderThread.setDaemon(true);
		finderThread.setDaemon(true);

		downloaderThread.start();
		finderThread.start();

		// wait until all pages have been downloaded
		while (PDDriver.getPageCount() < maxNumberOfPages);
	}

}
