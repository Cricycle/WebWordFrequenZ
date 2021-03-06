package download;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.concurrent.PriorityBlockingQueue;

import util.PageInfo;

public class PageDownloader
	implements Runnable
{

	private final PageInfo pi;
	private final PDDriver parentDriver;
	private final PriorityBlockingQueue<PageInfo> linkOutboundQueue;
	private final PriorityBlockingQueue<PageInfo> analysisOutboundQueue;

	public PageDownloader(PageInfo pi, PDDriver parentDriver,
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
		// open connection with URL
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

		// download webpage
		try (InputStream in = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(pi.getDLFileName(),
						false))
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
			// IO exception from bad file name
			parentDriver.decrementThreadCount();
			throw new RuntimeException(e);
		}

		// increment page count
		parentDriver.incrementPageCount();

		// add page to outbound queues
		linkOutboundQueue.add(pi);
		analysisOutboundQueue.add(pi);
		synchronized (linkOutboundQueue)
		{
			linkOutboundQueue.notify();
		}
		synchronized (analysisOutboundQueue)
		{
			analysisOutboundQueue.notify();
		}

		// decrement thread count
		parentDriver.decrementThreadCount();
	}

}
