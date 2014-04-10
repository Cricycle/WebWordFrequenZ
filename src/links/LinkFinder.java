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
			// get file from pageInfo.fileName
			File webpage = new File(pageInfo.fileName);
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

			// <a> links
			Elements aElements = doc.getElementsByTag("a");
			for (int i = 0; i < aElements.size(); i++)
			{
				Element e = aElements.get(i);
				String linkString = e.attr("href");
				PageInfo pi;
				try
				{
					pi = new PageInfo(new URL(linkString), pageInfo.remainingHops - 1);
				}
				catch (MalformedURLException e1)
				{
					throw new RuntimeException(e1);
				}
				outboundQueue.add(pi);
			}

			// <nav> links
			Elements navElements = doc.getElementsByTag("nav");
			for (int i = 0; i < navElements.size(); i++)
			{
				Element e = navElements.get(i);
				String linkString = e.attr("href");
				PageInfo pi;
				try
				{
					pi = new PageInfo(new URL(linkString), pageInfo.remainingHops - 1);
				}
				catch (MalformedURLException e1)
				{
					throw new RuntimeException(e1);
				}
				outboundQueue.add(pi);
			}

			// <link> links
			Elements linkElements = doc.getElementsByTag("link");
			for (int i = 0; i < linkElements.size(); i++)
			{
				Element e = linkElements.get(i);
				String linkString = e.attr("href");
				PageInfo pi;
				try
				{
					pi = new PageInfo(new URL(linkString), pageInfo.remainingHops - 1);
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
