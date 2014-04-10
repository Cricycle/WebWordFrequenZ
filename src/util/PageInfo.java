package util;

import java.net.URL;

import main.Main;

public class PageInfo
	implements Comparable<PageInfo>
{

	public static final String FORWARD_SLASH_REPLACEMENT = "%2F";
	public static final String COLON_REPLACEMENT = "%3A";

	public final URL url;
	public final int remainingHops;
	public final String fileName;

	public PageInfo(URL url, int remainingHops)
	{
		this.url = url;
		this.remainingHops = remainingHops;
		fileName = Main.FOLDER_NAME + "/" + makeFileName(url);
	}

	private String makeFileName(URL url)
	{
		String part = url.toExternalForm();
		return part.replace("/", FORWARD_SLASH_REPLACEMENT).replace(":",
				COLON_REPLACEMENT);
	}

	@Override
	public int compareTo(PageInfo other)
	{
		return (this.remainingHops - other.remainingHops);
	}

}
