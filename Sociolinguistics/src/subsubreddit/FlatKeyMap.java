package subsubreddit;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import util.collections.DoubleKeyMap;
import util.collections.Pair;
import util.sys.DataType;

public class FlatKeyMap extends DoubleKeyMap<String, String, Integer> implements DataType {

	private static final long serialVersionUID = -8752114372012451355L;
	public FlatKeyMap() {
		super(false);
	}
	public FlatKeyMap(String[] args) {
		super(Boolean.parseBoolean(args[0]));
	}
	public FlatKeyMap(FlatKeyMap other) {
		super(other);
	}
	@Override
	public int getNumFixedArgs() {
		return 0;
	}

	@Override
	public boolean hasNArgs() {
		return false;
	}

	@Override
	public String getConstructionErrorMsg() {
		return "requires true or false on whether it's symmetric";
	}
	@Override
	public String getHeaderLine() {
		String line = "key1,";
		if (isSymmetric()) {
			line += "val,";
		}
		else {
			line += "val12,val21,";
		}
		return line + "key2";
	}
	
	private String entryString(Pair<String, String> ent) {
		String first = ent.one();
		String second = ent.two();
		String val = "";
		if (this.isSymmetric()) {
			if (this.containsKey(first, second) && this.containsKey(second, first) && this.get(first, second) != this.get(second, first)) {
				throw new RuntimeException("Dictionary is in asymmetric state.");
			}
			val = ""+this.get(first, second); //will work, since it'll first put it in an unordered pair
		}
		else {
			int firstVal = this.get(first, second);
			int secondVal = this.containsKey(second, first) ? this.get(second, first) : 0;
			val = firstVal + "," + secondVal;
		}
		return first + "," + val + "," + second;
	}

	@Override
	public Iterator<String> getStringIter() {
		final FlatKeyMap outer = this;
		Iterator<String> iter = new Iterator<String>() {
			Set<String> finishedKeys = new HashSet<String>();
			Iterator<Pair<String, String>> firstKeys = outer.keySet().iterator();
			Pair<String, String> peek = firstKeys.hasNext() ? firstKeys.next() : null;
			//private final static String start = "wkejrhakj-s-h--dfajshdfasd";
			//Set<K> keysetOne = outer.getKeysetOne();
			public boolean hasNext() {
				//checking to make sure it's not the same shit in backwards order.
				return peek != null;
			}
			public String next() {
				Pair<String, String> key = peek;
				finishedKeys.add(key.one());
				finishedKeys.add(key.two());
				updatePeekToNext();
				return entryString(key);			
			}
			//set peek to the next non-duplicate element, or return false if there isn't one
			private boolean updatePeekToNext() {
				if (!duplicate(peek)) {
					return true; //current is not duplicate, so we're done
				}
				else if (firstKeys.hasNext()) {
					peek = firstKeys.next();
					return updatePeekToNext();
				}
				else {
					peek = null;
					return false;
				}
			}
			private boolean duplicate(Pair<String, String> peek) {
				if (peek == null) {
					return false;
				}
				return finishedKeys.contains(peek.one()) && finishedKeys.contains(peek.two());
			}
		};
		return iter;
	}

	@Override
	public DataType deepCopy() {
		return new FlatKeyMap(this);
	}

}
