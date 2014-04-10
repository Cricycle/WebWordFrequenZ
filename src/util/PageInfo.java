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
		return Main.DOWNLOAD_FOLDER + "/" + fileName;
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
