package delete;

import java.io.File;

import util.Driver;
import util.PageInfo;

/**
 * Simple Runnable which will delete a PageInfo instance's downloaded page
 * @author Alex
 *
 */
public class PageDeleter implements Runnable {
	
	/**
	 * The PageInfo instance to be deleted
	 */
	private final PageInfo pi;
	
	/**
	 * Number of times to retry deletion upon failure
	 */
	private int retryCount;
	
	/**
	 * Reference to the parent driver which created this thread
	 */
	private final Driver parentDriver;
	
	/**
	 * Basic constructor
	 * @param pi The PageInfo instance to be deleted
	 * @param parentDriver Reference to the driver which created this thread
	 * @param retryCount The number of times to retry failed deletion
	 */
	public PageDeleter(PageInfo pi, Driver parentDriver,  int retryCount) {
		this.pi = pi;
		this.retryCount = retryCount;
		this.parentDriver = parentDriver;
	}
	
	@Override
	public void run() {
		String filename = pi.getDLFileName();
		File downloaded_page = new File(filename);
		boolean success = false;
		while (retryCount-- >= 0) {
			success = downloaded_page.delete();
			if (success) break;
		}
		if (!success) {
			System.err.println("Failed to delete file: " + filename);
		}
		parentDriver.decrementThreadCount();
	}

}
