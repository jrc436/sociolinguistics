package filter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import wordmap.Combinable;
import wordmap.WordMap;

import java.util.Scanner;

public abstract class WordMapFilter extends Filter {
	protected abstract IWordMapFilter createFilter(WordMap wm);
	@Override
	protected void filterCritical(Scanner s, FileWriter fw) {
		WordMap wm = new WordMap(s.nextLine());
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
		applyFilter(wm, createFilter(wm));
		try {
			wm.writeUnsortedToFile(fw);
			s.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Filter: "+this.getClass()+" has failed to write.");
			System.exit(1);
		}
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
