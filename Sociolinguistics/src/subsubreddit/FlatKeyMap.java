package subsubreddit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import util.collections.DoubleKeyMap;
import util.collections.Pair;
import util.sys.DataType;

public class FlatKeyMap extends DoubleKeyMap<StringState, StringState, Integer> implements DataType {

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
	public static FlatKeyMap fromFile(File f) {
		List<String> lines = null;
		FlatKeyMap toRet = new FlatKeyMap();
		try {
			lines = Files.readAllLines(f.toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		boolean first = true;
		for (String line : lines) {
			if (first) {
				first = false;
				continue;
			}
			if (line.isEmpty()) {
				continue;
			}
			String[] parts = line.split(",");
			if (parts.length != 4) {
				System.err.println(line);
				throw new IllegalArgumentException("File: "+f+" does not encode a flatkeymap");
			}
			toRet.put(new StringState(parts[0], true), new StringState(parts[3], false), Integer.parseInt(parts[1]));
			toRet.put(new StringState(parts[3], false), new StringState(parts[0], true), Integer.parseInt(parts[2]));
		}
		return toRet;
	}
	
	public void absorb(DoubleKeyMap<StringState, StringState, Integer> other) {
		for (Pair<StringState, StringState> p : other.keySet()) {
			if (this.containsKey(p)) {
				this.put(p, this.get(p)+other.get(p));
			}
			else {
				this.put(p, other.get(p));
			}
		}
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
	
	protected String entryString(Pair<StringState, StringState> ent) {
		StringState first = ent.one();
		StringState second = ent.two();
		String val = "";
		boolean filt = false;
		if (this.isSymmetric()) {
			if (this.containsKey(first, second) && this.containsKey(second, first) && this.get(first, second) != this.get(second, first)) {
				throw new RuntimeException("Dictionary is in asymmetric state.");
			}
			val = ""+this.get(first, second); //will work, since it'll first put it in an unordered pair
		}
		else {
			int firstVal = this.get(first, second);
			int secondVal = this.containsKey(second, first) ? this.get(second, first) : 0;
			if ((first.isSub() && second.isSub()) || (!first.isSub() && !second.isSub())) {
				throw new RuntimeException("Pair is double sub");
			}
			if (first.isSub()) {
				val = firstVal + "," + secondVal;
			}
			else {
				val = secondVal + "," + firstVal;
				StringState tmp = second;
				second = first;
				first = tmp;
			}
			if (firstVal == 0 && secondVal == 0) {
				filt = true;
			}
		}
		if (filt) {
			return "";
		}
		return first + "," + val + "," + second;
	}

	@Override
	public Iterator<String> getStringIter() {
		final FlatKeyMap outer = this;
		Iterator<String> iter = new Iterator<String>() {
			Set<StringState> finishedKeys = new HashSet<StringState>();
			Iterator<Pair<StringState, StringState>> firstKeys = outer.keySet().iterator();
			Pair<StringState, StringState> peek = firstKeys.hasNext() ? firstKeys.next() : null;
			//private final static String start = "wkejrhakj-s-h--dfajshdfasd";
			//Set<K> keysetOne = outer.getKeysetOne();
			public boolean hasNext() {
				//checking to make sure it's not the same shit in backwards order.
				return peek != null;
			}
			public String next() {
				Pair<StringState, StringState> key = peek;
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
			private boolean duplicate(Pair<StringState, StringState> peek) {
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
