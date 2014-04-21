package main;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

import links.LFDriver;
import util.PageInfo;
import analyze.PADriver;
import delete.DDriver;
import download.PDDriver;

public class MainDriver
{

	public static final String DOWNLOAD_FOLDER = "downloaded_pages";
	public static final String ANALYSIS_FOLDER = "analyzed_data";

	public static void main(String[] args)
	{
		if (args.length != 3)
		{
			System.err.println("Usage: basePageURL maxHopCount maxNumberOfPages");
			System.exit(1);
		}

		String basePageURL = args[0];
		int maxHopCount = Integer.parseInt(args[1]);
		int maxNumberOfPages = Integer.parseInt(args[2]);

		MainDriver main = new MainDriver();
		main.run(basePageURL, maxHopCount, maxNumberOfPages);
	}

	public void run(String basePageURL, int maxHopCount, int maxNumberOfPages)
	{
		System.out.printf("Executing with parameters: %s %d %d%n", basePageURL,
				maxHopCount, maxNumberOfPages);

		// make folder to store downloaded webpages
		File downloadFolder = new File(MainDriver.DOWNLOAD_FOLDER);
		downloadFolder.mkdirs();

		// make folder to store analysis results
		File analysisFolder = new File(MainDriver.ANALYSIS_FOLDER);
		analysisFolder.mkdirs();

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

		PDDriver pdDriver = new PDDriver(maxNumberOfPages, executionSemaphore,
				finderToDownloaderQueue, downloaderToFinderQueue,
				downloaderToAnalyzerQueue);
		LFDriver lfDriver = new LFDriver(downloaderToFinderQueue,
				executionSemaphore, finderToDownloaderQueue, toDeleterQueue);
		PADriver analysisDriver = new PADriver(downloaderToAnalyzerQueue,
				executionSemaphore, toDeleterQueue);
		DDriver deletionDriver = new DDriver(toDeleterQueue, executionSemaphore, 3); // retry failed deletion 3 times

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
					executionSemaphore.release(4);
					System.out.println("Determined that nothing is running.");
					break;
				} else {
					executionSemaphore.release(4);
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		System.out.println("Page count: " + pdDriver.getPageCount());

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

		System.out.println("Execution completed.");
		System.out.println("Results in directory "
				+ analysisFolder.getAbsolutePath());
	}

}
