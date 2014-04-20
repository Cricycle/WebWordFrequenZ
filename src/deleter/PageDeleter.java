package deleter;

import java.io.File;

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
	private PageInfo pi;
	
	/**
	 * Number of times to retry deletion upon failure
	 */
	private int retryCount;
	
	/**
	 * Basic constructor
	 * @param pi The PageInfo instance to be deleted
	 * @param retryCount The number of times to retry failed deletion
	 */
	public PageDeleter(PageInfo pi, int retryCount) {
		this.pi = pi;
		this.retryCount = retryCount;
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
	}

}
