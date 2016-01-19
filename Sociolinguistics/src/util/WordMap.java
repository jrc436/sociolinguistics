package util;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class WordMap extends HashMap<String, Integer>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6844547921098526441L;
	public void combine(WordMap other) {
		for (String word : other.keySet()) {
			if (this.containsKey(word)) {
				this.put(word, this.get(word)+1);
			}
			else {
				this.put(word, 1);
			}
		}
	}
	public void addSentence(String s) {
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
	
	public String toString() {
		String aggregate = "";
		List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(this.entrySet());
		Collections.sort(list, new wordMapSorter(false));
		for (Entry<String, Integer> entry : list) {
			aggregate += entry.getKey()+" : "+entry.getValue() + System.getProperty("line.separator");
		}
		return aggregate;
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
