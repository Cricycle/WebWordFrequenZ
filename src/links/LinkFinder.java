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

import util.Driver;
import util.PageInfo;

public class LinkFinder
	implements Runnable
{

	private final PageInfo pageInfo;
	private final Driver parentDriver;
	private final PriorityBlockingQueue<PageInfo> download_outboundQueue;
	private final PriorityBlockingQueue<PageInfo> delete_outboundQueue;

	public LinkFinder(PageInfo pageInfo, Driver parentDriver,
			PriorityBlockingQueue<PageInfo> download_outboundQueue,
			PriorityBlockingQueue<PageInfo> delete_outboundQueue)
	{
		this.pageInfo = pageInfo;
		this.parentDriver = parentDriver;
		this.delete_outboundQueue = delete_outboundQueue;
		this.download_outboundQueue = download_outboundQueue;
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

			Elements aElements = doc.getElementsByTag("a");
			Elements navElements = doc.getElementsByTag("nav");
			Elements linkElements = doc.getElementsByTag("link");

			Elements allElements = new Elements();
			allElements.addAll(aElements); // <a> links
			allElements.addAll(navElements); // <nav> links
			allElements.addAll(linkElements); // <link> links

			// for each link in file, add link to outboundQueue
			for (int i = 0; i < allElements.size(); i++)
			{
				Element e = allElements.get(i);
				String linkString = e.absUrl("href");
				if (linkString.equals(""))
					continue;
				boolean okayToLink = true;
				PageInfo pi = null;

				try
				{
					URL url = new URL(linkString);
					String urlpath = url.getPath();
					// check if we care about the file
					int idx = urlpath.lastIndexOf('.');
					if (idx != -1)
					{
						// it has a file type
						String fileType = urlpath.substring(idx + 1);
						fileType = fileType.toLowerCase();
						okayToLink = (fileType.equals("html")
								|| fileType.equals("htm") || fileType
								.equals("txt"));
					}
					if (okayToLink)
					{
						pi = new PageInfo(url, pageInfo.remainingHops - 1);
					}
				}
				catch (MalformedURLException e1)
				{
					System.err.println("Bad URL: <" + linkString + ">");
				}

				if (okayToLink)
				{
					download_outboundQueue.add(pi);
					synchronized (download_outboundQueue)
					{
						download_outboundQueue.notify();
					}
				}
			}
		}

		delete_outboundQueue.add(pageInfo);
		synchronized (delete_outboundQueue)
		{
			delete_outboundQueue.notify();
		}
		parentDriver.decrementThreadCount();
	}

}
