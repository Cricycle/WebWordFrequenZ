package test;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

import main.Displayer;
import util.PageInfo;
import analyze.PADriver;

public class WCATest {
	
	public static void main(String[] args) throws Exception {
		URL fakeurl = new URL("http://www.fakeurl.fake");
		PageInfo pi = new PageInfo(fakeurl, 0);
		pi.getDLFileName();
		File dir = new File(Displayer.DOWNLOAD_FOLDER);
		dir.mkdirs();
		dir = new File(Displayer.ANALYSIS_FOLDER);
		dir.mkdirs();
		PrintWriter out = new PrintWriter(pi.getDLFileName());
		for (int i = 1; i <= 50000; ++i) {
			String conv = "";
			int t = i;
			while (t >= 0) {
				int rem = t % 36;
				t /= 36;
				if (rem < 26) conv += (char)('a' + rem);
				else	conv += '0' + (rem - 26);
				if (t == 0) break;
			}
			out.write(conv + ' ');
		}
		out.flush();
		out.close();
		System.out.println("reached here");
		PriorityBlockingQueue<PageInfo> fakeinqueue = new PriorityBlockingQueue<>();
		PriorityBlockingQueue<PageInfo> fakeoutqueue = new PriorityBlockingQueue<>();
		Semaphore fake = new Semaphore(1);
		PADriver pad = new PADriver(fakeinqueue, fake, fakeoutqueue);
		// tested with 5000, new thread every 20ms, worked fine
		int testSize = 500;
		Thread padthread = null;
		for (int i = 0; i < testSize; ++i)
			fakeinqueue.add(pi);
		(padthread = new Thread(pad)).start();
		
		while (!fakeinqueue.isEmpty()) {
			System.out.printf("Queue still has %d items\n", fakeinqueue.size());
			Thread.sleep(500);
		}
		System.out.printf("Queue still has %d items\n", fakeinqueue.size());
		fakeinqueue.offer(PageInfo.END);
		padthread.join();
	}
	
}
