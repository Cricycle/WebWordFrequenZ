package util;

import java.net.URL;

public class PageInfo
{

	public static final String SLASH_REPLACEMENT = "_&$%";

	public final URL url;
	public final int remainingHops;
	public final String fileName;

	public PageInfo(URL url, int remainingHops)
	{
		this.url = url;
		this.remainingHops = remainingHops;
		fileName = makeFileName(url);
	}

	private String makeFileName(URL url)
	{
		// TODO return url string with slash replaced
		return null;
	}

}
