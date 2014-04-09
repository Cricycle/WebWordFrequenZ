package links;

import java.util.concurrent.PriorityBlockingQueue;

import util.PageInfo;

public class LinkFinder
	implements Runnable
{

	private final PageInfo pageInfo;
	private final PriorityBlockingQueue<PageInfo> outboundQueue;

	public LinkFinder(PageInfo pageInfo,
			PriorityBlockingQueue<PageInfo> outboundQueue)
	{
		this.pageInfo = pageInfo;
		this.outboundQueue = outboundQueue;
	}

	@Override
	public void run()
	{
		if (pageInfo.remainingHops > 0)
		{
			// TODO get file from pageInfo.fileName
			// TODO for each link in file, add link to outboundQueue
		}

		// TODO let the delete task know that we are done finding links in file
	}

}
