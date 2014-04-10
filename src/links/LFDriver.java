package links;

import java.util.concurrent.PriorityBlockingQueue;

import util.PageInfo;
import web.PDDriver;

public class LFDriver
	implements Runnable
{

	private final int MAX_NUM_PAGES;
	private final PriorityBlockingQueue<PageInfo> inboundQueue, outboundQueue;

	public LFDriver(int MAX_NUM_PAGES,
			PriorityBlockingQueue<PageInfo> inboundQueue,
			PriorityBlockingQueue<PageInfo> outboundQueue)
	{
		this.MAX_NUM_PAGES = MAX_NUM_PAGES;
		this.inboundQueue = inboundQueue;
		this.outboundQueue = outboundQueue;
	}

	public void run()
	{
		while (PDDriver.getPageCount() < MAX_NUM_PAGES)
		{
			try
			{
				PageInfo pageInfo = inboundQueue.take();
				Thread t = new Thread(new LinkFinder(pageInfo, outboundQueue));
				t.start();
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
