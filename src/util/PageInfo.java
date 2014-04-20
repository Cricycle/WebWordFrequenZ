package util;

import java.net.MalformedURLException;
import java.net.URL;

import main.Displayer;

public class PageInfo
	implements Comparable<PageInfo>
{
	public static final PageInfo END = new PageInfo(null, Integer.MAX_VALUE);

	public static final String FORWARD_SLASH_REPLACEMENT = "%2F";
	public static final String COLON_REPLACEMENT = "%3A";

	public final URL url;
	public final int remainingHops;
	private final String fileName;

	public PageInfo(URL url, int remainingHops)
	{
		if (url == null) {
			this.url = null;
			fileName = null;
			this.remainingHops = remainingHops;
			return;
		}
		
		String urlstring = url.toExternalForm();
		String querystr = url.getQuery();
		String ref = url.getRef();
		
		if (querystr != null) {
			int idx = urlstring.lastIndexOf(querystr);
			urlstring = urlstring.substring(0, idx-1);
		}
		
		if (ref != null) {
			int idx = urlstring.lastIndexOf(ref);
			urlstring = urlstring.substring(0, idx-1);
		}
		
		try {
			this.url = new URL(urlstring);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
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
	
	@Override
	public int hashCode() {
		return fileName.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof PageInfo)) return false;
		PageInfo pi = (PageInfo)o;
		return pi.fileName.equals(fileName);
	}
}
