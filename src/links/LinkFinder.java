package links;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.PriorityBlockingQueue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import util.PageInfo;

public class LinkFinder implements Runnable
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
			// get file from pageInfo.fileName
			File webpage = new File(pageInfo.getDLFileName());
			Document doc;
			try
			{
				doc = Jsoup.parse(webpage, "UTF-8", pageInfo.url.toString());
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}

			// for each link in file, add link to outboundQueue

			Elements aElements = doc.getElementsByTag("a");
			Elements navElements = doc.getElementsByTag("nav");
			Elements linkElements = doc.getElementsByTag("link");

			Elements allElements = new Elements();
			allElements.addAll(aElements); // <a> links
			allElements.addAll(navElements); // <nav> links
			allElements.addAll(linkElements); // <link> links

			for (int i = 0; i < allElements.size(); i++)
			{
				Element e = allElements.get(i);
				String linkString = e.attr("href");
				PageInfo pi;
				try
				{
					pi = new PageInfo(new URL(linkString),
							pageInfo.remainingHops - 1);
				}
				catch (MalformedURLException e1)
				{
					throw new RuntimeException(e1);
				}

				outboundQueue.add(pi);
			}
		}

		// TODO let the delete task know that we are done finding links in file
	}

}
