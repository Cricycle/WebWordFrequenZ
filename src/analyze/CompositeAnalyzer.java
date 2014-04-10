package analyze;

import java.util.concurrent.PriorityBlockingQueue;

import util.PageInfo;

public class CompositeAnalyzer extends PageAnalyzer {
	
	private PageAnalyzer[] list;
	
	public CompositeAnalyzer(PageInfo pi, PriorityBlockingQueue<PageInfo> outboundQueue, PageAnalyzer ... list) {
		super(pi, outboundQueue);
		this.list = new PageAnalyzer[list.length];
		System.arraycopy(list, 0, this.list, 0, list.length);
	}
	
	@Override
	protected void analyze(PageInfo pi) {
		for (int i = 0; i < list.length; ++i) {
			list[i].analyze(pi);
		}
	}

}
