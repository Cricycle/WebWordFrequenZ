package web;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.concurrent.PriorityBlockingQueue;

import util.Driver;
import util.PageInfo;

public class PageDownloader
	implements Runnable
{

	private final PageInfo pi;
	private final Driver parentDriver;
	private final PriorityBlockingQueue<PageInfo> linkOutboundQueue;
	private final PriorityBlockingQueue<PageInfo> analysisOutboundQueue;

	public PageDownloader(PageInfo pi, Driver parentDriver,
			PriorityBlockingQueue<PageInfo> linkOutboundQueue,
			PriorityBlockingQueue<PageInfo> analysisOutboundQueue)
	{
		this.pi = pi;
		this.parentDriver = parentDriver;
		this.linkOutboundQueue = linkOutboundQueue;
		this.analysisOutboundQueue = analysisOutboundQueue;
	}

	@Override
	public void run()
	{
		URLConnection conn;
		try
		{
			conn = pi.url.openConnection();
		}
		catch (IOException e)
		{
			System.err.println("Could not open connection with URL: " + pi.url);
			e.printStackTrace();
			parentDriver.decrementThreadCount();
			return;
		}
		try (InputStream in = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(pi.getDLFileName(), false))
		{
			byte[] buffer = new byte[1 << 16];
			int len = -1;
			while ((len = in.read(buffer)) > 0)
			{
				fos.write(buffer, 0, len);
				fos.flush();
			}
		}
		catch (IOException e)
		{
			// IO exception from BR, or bad file name
			parentDriver.decrementThreadCount();
			throw new RuntimeException(e);
		}

		// increment page count
		PDDriver.incrementPageCount();

		// add page to Outbound Queues
		linkOutboundQueue.add(pi);
		analysisOutboundQueue.add(pi);
		synchronized (linkOutboundQueue) {
			linkOutboundQueue.notify();
		}
		synchronized (analysisOutboundQueue) {
			analysisOutboundQueue.notify();
		}
		
		parentDriver.decrementThreadCount();
	}

}
