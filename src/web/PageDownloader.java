package web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.atomic.AtomicInteger;

import util.PageInfo;

public class PageDownloader implements Runnable {
	
	private static AtomicInteger webpageCount = new AtomicInteger();
	
	private PageInfo pi;
	
	public PageDownloader(PageInfo pi) {
		this.pi = pi;
	}
	
	public void run() {
		InputStream in = null;
		FileOutputStream fos = null;
		try {
			URL url = pi.url;
			URLConnection conn = url.openConnection();
			in = conn.getInputStream();
			// put next 2 lines into driver
			File folder = new File(PageInfo.FOLDER_NAME);
			folder.mkdirs();
			fos = new FileOutputStream(pi.fileName, false);
			byte[] buffer = new byte[1 << 16];
			int len = -1;
			while ((len = in.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
				fos.flush();
			}
			incrementPageCount();
		} catch (MalformedURLException e) {
			// new URL() failed
			e.printStackTrace();
		} catch (IOException e) {
			// openConnection failed, or IO exception from BR, or bad file name
			e.printStackTrace();
		} finally {
			// attempt to close connections, if they were open.
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				if (fos != null)
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static int getPageCount() {
		return webpageCount.get();
	}
	
	private static int incrementPageCount() {
		return webpageCount.incrementAndGet();
	}
}
