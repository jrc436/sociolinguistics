package filter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;

import wordmap.Combinable;
import wordmap.WordMap;

import java.util.Scanner;

public abstract class WordMapFilter extends Filter {
	protected abstract IWordMapFilter createFilter(WordMap wm);
	@Override
	protected void filterCritical(Scanner s, FileWriter fw, BlockingQueue<String> log) {
		WordMap wm = new WordMap(s.nextLine());
		log.offer("Wordmap Created");
		int i = 0;
		while (s.hasNextLine()) {
			i++;
			//System.out.println(s.nextLine());
			String ln = s.nextLine();
			if (ln.contains(WordMap.splitter)) {
				wm.addFromString(ln);
			}
			else {
				System.out.println(i);
				System.out.println(ln);
				System.exit(1);
			}
		}
		log.offer("Wordmap has been read");
		applyFilter(wm, createFilter(wm));
		log.offer("Filter has been applied");
		try {
			wm.writeUnsortedToFile(fw);
			s.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Filter: "+this.getClass()+" has failed to write.");
			System.exit(1);
		}
		log.offer("Writing complete.");
	}
	private static void applyFilter(WordMap wm, IWordMapFilter filt) {
		List<String> mark = new ArrayList<String>();
		for (Entry<String, Combinable> word : wm.entrySet()) {
			if (!filt.goodEntry(word)) {
				mark.add(word.getKey());
			}
		}
		for (String w : mark) {
			wm.remove(w);
		}
	}

}
