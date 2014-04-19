package analyze;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.PriorityBlockingQueue;

import main.MainDriver;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import util.PageInfo;

public class WordCountAnalyzer extends PageAnalyzer {

	public WordCountAnalyzer(PageInfo pi,
			PriorityBlockingQueue<PageInfo> outboundQueue) {
		super(pi, outboundQueue);
		// TODO Auto-generated constructor stub
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
		String[] words = doc.text().split("( |\\.)");
		HashMap<String, Integer> wordCounts = new HashMap<String, Integer>();
		for (int i = 0; i < words.length; ++i) {
			if (words[i].length() == 0) continue;
			
			words[i] = words[i].toLowerCase();
			Integer count = wordCounts.get(words[i]);
			if (count == null)
				count = 0;
			wordCounts.put(words[i], count+1);
		}
		
		ArrayList<WordPair> pairs = new ArrayList<>();
		Iterator<String> keyIter = wordCounts.keySet().iterator();
		while (keyIter.hasNext()) {
			String next = keyIter.next();
			pairs.add(new WordPair(next, wordCounts.get(next)));
		}
		Collections.sort(pairs);
		
		String analysisFile = MainDriver.ANALYSIS_FOLDER + "/" + pi.getFileName();
		System.err.println(analysisFile);
		try (PrintWriter out = new PrintWriter(analysisFile))
		{
			out.println("Beginning of file");
			for (int i = 0; i < pairs.size(); ++i) {
				out.println(pairs.get(i));
			}
			out.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private class WordPair implements Comparable<WordPair> {
		
		private String word;
		private int count;
		
		public WordPair(String word, int count) {
			this.word = word;
			this.count = count;
		}
		
		@Override
		public int compareTo(WordPair o) {
			return o.count - count;
		}
		
		public String toString() {
			return String.format("%s : %d", word, count);
		}
		
	}
	
}

