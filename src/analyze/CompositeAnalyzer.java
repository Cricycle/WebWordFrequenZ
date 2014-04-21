package analyze;

import java.util.concurrent.PriorityBlockingQueue;

import util.Driver;
import util.PageInfo;

public class CompositeAnalyzer extends PageAnalyzer
{

	private PageAnalyzer[] list;

	public CompositeAnalyzer(PageInfo pi, Driver parentDriver,
			PriorityBlockingQueue<PageInfo> outboundQueue, PageAnalyzer... list)
	{
		super(pi, parentDriver, outboundQueue);
		this.list = new PageAnalyzer[list.length];
		System.arraycopy(list, 0, this.list, 0, list.length);
	}

	@Override
	protected void analyze(PageInfo pi)
	{
		for (int i = 0; i < list.length; ++i)
		{
			list[i].analyze(pi);
		}
	}

}
