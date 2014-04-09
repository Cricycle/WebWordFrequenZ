package util;

import java.net.URL;

public class PageInfo
{

	public static final String SLASH_REPLACEMENT = "_&$%";

	public final URL url;
	public final int hopCount;
	public final String fileName;

	public PageInfo(URL url, int hopCount)
	{
		this.url = url;
		this.hopCount = hopCount;
		fileName = makeFileName(url);
	}

	private String makeFileName(URL url)
	{
		// TODO return url string with slash replaced
		return null;
	}

}
