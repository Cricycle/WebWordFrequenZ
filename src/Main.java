import java.net.URL;

import util.PageInfo;
import web.PageDownloader;

public class Main
{

	public static void main(String[] args)
	{
		if (args.length != 3)
		{
			System.err.println("Usage: basePageURL maxHopCount maxNumberOfPages");
			System.exit(1);
		}

		String basePageURL = args[0];
		int maxHopCount = Integer.parseInt(args[1]);
		int maxNumberOfPages = Integer.parseInt(args[2]);

		// TODO create buffers between tasks
		// TODO start drivers for each task
		
		try {
			// start PageDownloader here
			PageInfo pi = new PageInfo(new URL(basePageURL), maxHopCount);
			Thread t = new Thread(new PageDownloader(pi));
			System.out.printf("%s, %s\n", pi.url, pi.fileName);
			t.start();
			System.out.println("started");
			t.join();
			System.out.println("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
