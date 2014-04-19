package analyze;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

import main.MainDriver;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import util.PageInfo;

/**
 * WordCountAnalyzer expects an HTML file, which it will then parse using the
 * Jsoup library (www.jsoup.org).  It gets the text on the page, and counts how
 * often each word occurs.  A word is treated as any sequence of alphanumeric
 * characters.
 *  
 * @author Alex
 *
 */
public class WordCountAnalyzer extends PageAnalyzer {
	
	/**
	 * The shared map containing all the word count data
	 */
	private ConcurrentHashMap<String, Integer> sharedMap;

	public WordCountAnalyzer(PageInfo pi,
			ConcurrentHashMap<String, Integer> sharedMap,
			PriorityBlockingQueue<PageInfo> outboundQueue) {
		super(pi, outboundQueue);
		this.sharedMap = sharedMap;
	}

	@Override
	protected void analyze(PageInfo pi) {
		File savedPage = new File(pi.getDLFileName());
		Document doc = null;
		try {
			doc = Jsoup.parse(savedPage, "UTF-8", pi.url.toString());
		} catch (IOException e) {
			throw new RuntimeException("Failed to parse file: " + pi.getDLFileName(), e);
		}
		// add more punctuation splitting
		String[] words = doc.text().split("\\W");
		HashMap<String, Integer> wordCounts = new HashMap<String, Integer>();
		for (int i = 0; i < words.length; ++i) {
			if (words[i].length() == 0) continue;
			
			words[i] = words[i].toLowerCase();
			Integer count = wordCounts.get(words[i]);
			if (count == null)
				count = 0;
			wordCounts.put(words[i], count+1);
		}
		
		Iterator<String> keyIter = wordCounts.keySet().iterator();
		while (keyIter.hasNext()) {
			String next = keyIter.next();
			Integer total = wordCounts.get(next);
			Integer prev = sharedMap.putIfAbsent(next, total);
			if (prev != null) {
				int milliWait = 20;
				int maxWait = 500;
				while (!sharedMap.replace(next, prev, prev+total)) {
					try {
						Thread.sleep(milliWait);
						milliWait *= 2;
						milliWait = Math.min(milliWait, maxWait);
					} catch (InterruptedException e) {}
					prev = sharedMap.get(next);
				}
			}
		}
	}
	
	public static void saveDataToFile(String filename,
			ConcurrentHashMap<String, Integer> wordCounts) {
		ArrayList<WordPair> pairs = new ArrayList<>();
		Iterator<String> keyIter = wordCounts.keySet().iterator();
		while (keyIter.hasNext()) {
			String next = keyIter.next();
			pairs.add(new WordPair(next, wordCounts.get(next)));
		}
		Collections.sort(pairs);
		
		String analysisFile = MainDriver.ANALYSIS_FOLDER + "/" + filename;
		System.err.println(analysisFile);
		try (PrintWriter out = new PrintWriter(analysisFile))
		{
			for (int i = 0; i < pairs.size(); ++i) {
				out.println(pairs.get(i));
			}
			out.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static class WordPair implements Comparable<WordPair> {
		
		private String word;
		private int count;
		
		public WordPair(String word, int count) {
			this.word = word;
			this.count = count;
		}
		
		@Override
		public int compareTo(WordPair o) {
			int diff = o.count - count;
			if (diff != 0) return diff;
			return word.compareTo(o.word);
		}
		
		public String toString() {
			return String.format("%s : %d", word, count);
		}
		
	}
	
}
