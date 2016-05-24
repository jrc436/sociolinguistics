package util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class WordMap extends HashMap<String, Integer> {
	private static final long serialVersionUID = 6844547921098526441L;
	public static final String splitter = ":";
	public synchronized void combine(WordMap other) {
		for (String word : other.keySet()) {
			if (this.containsKey(word)) {
				this.put(word, this.get(word)+1);
			}
			else {
				this.put(word, 1);
			}
		}
	}
	public synchronized void addSentence(String s) {
		String[] words = s.split(" ");
		for (String word : words) {
			word = StringCleaner.cleanWord(word);
			if (word.isEmpty()) {
				continue;
			}
			if (this.containsKey(word)) {
				this.put(word, this.get(word)+1);
			}
			else {
				this.put(word, 1);
			}
		}
	}

	public synchronized void addFromString(String s) {
		try {
			int splitdex = s.lastIndexOf(splitter); 
			String str = s.substring(0, splitdex);
			int number = Integer.parseInt(s.substring(splitdex + 1));
			this.put(str, number);
		}
		catch (Exception e) {
			System.out.println("Trouble adding to WordMap:"+s);
		}
	}
	
	public List<String> getStringLines() {
		List<String> lines = new ArrayList<String>();
		List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(this.entrySet());
		for (Entry<String, Integer> entry : list) {
			lines.add(entryString(entry));
		}
		return lines;
	}
	public String unsortedString() {
		String aggregate = "";
		for (Entry<String, Integer> entry : this.entrySet()) {
			aggregate += entryString(entry) + System.getProperty("line.separator");
		}
		return aggregate;
	}
	public void writeUnsortedToFile(FileWriter fw) throws IOException {
		Set<Entry<String, Integer>> entries = this.entrySet();
		for (Entry<String, Integer> entry : entries) {
			fw.write(entryString(entry)+System.getProperty("line.separator"));
		}
	}
//	public Set<Entry<String, Integer>> getEntrySet() {
//		return this.entrySet();
//	}
	
	public String toString() {
		String aggregate = "";
		List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(this.entrySet());
		Collections.sort(list, new wordMapSorter(false));
		for (Entry<String, Integer> entry : list) {
			aggregate += entryString(entry) + System.getProperty("line.separator");
		}
		return aggregate;
	}
	private String entryString(Entry<String, Integer> entry) {
		 return entry.getKey()+splitter+entry.getValue();
	}
	class wordMapSorter implements Comparator<Entry<String, Integer>> {
		private final boolean ascending;
		public wordMapSorter(boolean ascending) {
			this.ascending = ascending;
		}
		 public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2)
         {
             if (ascending)
             {
                 return o1.getValue().compareTo(o2.getValue());
             }
             else
             {
                 return o2.getValue().compareTo(o1.getValue());

             }
         }
	}
}
