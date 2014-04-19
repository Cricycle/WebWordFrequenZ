package util;

import java.net.URL;

import main.Displayer;

public class PageInfo
	implements Comparable<PageInfo>
{
	public static final PageInfo END = new PageInfo(null, -1);

	public static final String FORWARD_SLASH_REPLACEMENT = "%2F";
	public static final String COLON_REPLACEMENT = "%3A";

	public final URL url;
	public final int remainingHops;
	private final String fileName;

	public PageInfo(URL url, int remainingHops)
	{
		this.url = url;
		this.remainingHops = remainingHops;
		fileName = makeFileName(url);
	}
	
	/**
	 * Gets the file name, with forward slashes and colons replaced by hex.
	 * @return Formatted String form of the web URL
	 */
	public String getFileName() {
		return fileName;
	}
	
	/** 
	 * @return The downloaded file location
	 */
	public String getDLFileName() {
		return Displayer.DOWNLOAD_FOLDER + "/" + fileName;
	}

	private String makeFileName(URL url)
	{
		String part = String.format("%s", url);
		return part.replace("/", FORWARD_SLASH_REPLACEMENT).replace(":",
				COLON_REPLACEMENT);
	}

	@Override
	public int compareTo(PageInfo other)
	{
		return (other.remainingHops - remainingHops);
	}

}
