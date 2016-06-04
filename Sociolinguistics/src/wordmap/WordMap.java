package wordmap;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import util.Comment;
import util.StringCleaner;



public class WordMap extends HashMap<String, Combinable> {
	private static final long serialVersionUID = 6844547921098526441L;
	//the splitter should be something very unlikely to be in the string
	public static final String splitter = "::-:--:";
	private final Set<Class<? extends Combinable>> classes;
	public WordMap(Set<Class<? extends Combinable>> classes) {
		super();
		this.classes = classes;
	}
	public WordMap(String headerLine) {
		this.classes = Combinable.fromString(headerLine);
	}
	@SafeVarargs
	public static WordMap initializeWordMap(Class<? extends Combinable>...cls) {
		Set<Class<? extends Combinable>> classes = new HashSet<Class<? extends Combinable>>();
		for (Class<? extends Combinable> c : cls) {
			classes.add(c);
		}
		return new WordMap(classes);
	}
	public Combinable getBy(String key, Class<? extends Combinable> comb) {
		if (!classes.contains(comb)) {
			return null;
		}
		Combinable c = super.get(key);
		if (c.getClass().equals(comb)) {
			return c;
		}
		else if (c instanceof CombineSet) {
			CombineSet cs = (CombineSet)c;
			return cs.get(comb);
		}
		throw new IllegalStateException("The class isn't a combine set, or the class, but it does contain it. Something is mismatched");
	}
	public synchronized void combine(WordMap other) {
		for (String word : other.keySet()) {
			if (this.containsKey(word)) {
				try {
					this.put(word, this.get(word).combine(other.get(word)));
				} catch (CombineException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				this.put(word, other.get(word));
			}
		}
	}
	public synchronized void addComment(Comment s) {
		String[] words = s.getText().split("\\s+");
		for (String word : words) {
			word = StringCleaner.cleanWord(word);
			Combinable initial = Combinable.populate(classes, s);
			if (word.isEmpty()) {
				continue;
			}
			if (this.containsKey(word)) {
				try {
					this.put(word, this.get(word).combine(initial));
				} catch (CombineException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
			else {
				this.put(word, initial);
			}
		}
	}
	//have to update this to use class set
	public synchronized void addFromString(String s) {
		try {
			int splitdex = s.lastIndexOf(splitter); 
			String str = s.substring(0, splitdex);
			String comb = s.substring(splitdex + splitter.length());
			this.put(str, Combinable.recreate(classes, comb));
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Trouble adding to WordMap:"+s);
			System.exit(1);
		}
		//System.out.println("Successful add");
	}
	private String forceClsString() {
		return CombineSet.mockClsString(this.classes);
	}
	public void writeUnsortedToFile(FileWriter fw) throws IOException {
		Set<Entry<String, Combinable>> entries = this.entrySet();
		int i = 0;
		if (entries.size() == 0) {
			fw.write(this.forceClsString() + System.getProperty("line.separator"));
			return;
		}
		for (Entry<String, Combinable> entry : entries) {
			if (i == 0) {
				fw.write(entry.getValue().clsString() + System.getProperty("line.separator"));
			}
			fw.write(entryString(entry));
			i++;
			if (i < entries.size()) {
				fw.write(System.getProperty("line.separator"));
			}
		}
	}
	//have to update the toString of CombineSet
	private String entryString(Entry<String, Combinable> entry) {
		 return entry.getKey()+splitter+entry.getValue().toString();
	}
//	class wordMapSorter implements Comparator<Entry<String, Combinable>> {
//		private final boolean ascending;
//		public wordMapSorter(boolean ascending) {
//			this.ascending = ascending;
//		}
//		 public int compare(Entry<String, Combinable> o1, Entry<String, Combinable> o2)
//         {
//             if (ascending)
//             {
//                 return o1.getValue().compareTo(o2.getValue());
//             }
//             else
//             {
//                 return o2.getValue().compareTo(o1.getValue());
//
//             }
//         }
//	}
}
