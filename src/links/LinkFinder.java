package links;

import java.util.concurrent.PriorityBlockingQueue;

import util.PageInfo;

public class LinkFinder implements Runnable
{

	private final PriorityBlockingQueue<PageInfo> inboundQueue, outboundQueue;

	public LinkFinder(PriorityBlockingQueue<PageInfo> inboundQueue,
			PriorityBlockingQueue<PageInfo> outboundQueue)
	{
		this.inboundQueue = inboundQueue;
		this.outboundQueue = outboundQueue;
	}

	@Override
	public void run()
	{
		
	}

}
