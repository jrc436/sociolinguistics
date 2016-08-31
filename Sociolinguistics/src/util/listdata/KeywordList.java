package util.listdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import util.data.Comment;
import util.data.CommentFormat;
import util.json.JsonReadable;
import util.sys.DataType;

public class KeywordList extends HashMap<String, List<Comment>> implements DataType {
	private static final long serialVersionUID = 6441745759936713041L;
	private final CommentFormat cf;
	public KeywordList() { // dummy constructor
		super();
		this.cf = null;
	}
	public KeywordList(String[] keywords, CommentFormat cf) {
		super();
		this.cf = cf;
		for (String key : keywords) {
			super.put(key, new ArrayList<Comment>());
		}
	}
	public KeywordList(KeywordList kl) {
		super(kl);
		this.cf = kl.cf;
	}
	public KeywordList(List<String> fileLines, CommentFormat cf) {
		super();
		this.cf = cf;
		if (fileLines.size() == 0 || !isKeyLine(fileLines.get(0))) {
			throw new IllegalArgumentException("There needs to be at least one key line starting the file.");
		}
		String currentKeyLine = null;
		for (String s : fileLines) {
			currentKeyLine = addLine(s, currentKeyLine);
		}
	}
	public List<String> getRelevantKeywords(String text) {		
		List<String> retval = new ArrayList<String>();
		if (text.isEmpty()) {
			return retval;
		}
		for (String key : this.keySet()) {
			if (text.contains(key)) {
				retval.add(key);
			}
		}
		return retval;
	}
	
	@Override
	public int getNumFixedArgs() {
		return 0;
	}

	@Override
	public boolean hasNArgs() {
		return true;
	}

	@Override
	public String getConstructionErrorMsg() {
		return "KeywordList requires one or more keywords";
	}

	@Override
	public String getFileExt() {
		return ".klist";
	}
	public void add(String keyword, Comment datum) {
		if (!this.containsKey(keyword)) {
			this.put(keyword, new ArrayList<Comment>());
		}
		this.get(keyword).add(datum);
	}
	
	private static final String init = "?-? ";
	private static final String out = " !-!";
	
	private String getKeyMarker(String origLine) {
		return init + origLine + out;
	}
	private boolean isKeyLine(String line) {
		return line.substring(0, init.length()).equals(init) && line.substring(line.length()-out.length()).equals(out);
	}
	private String returnFromMarker(String line) {
		return line.substring(init.length(), line.length()-out.length());
	}
	private String addLine(String s, String currentKeyLine) {
		if (isKeyLine(s)) {
			super.put(returnFromMarker(s), new ArrayList<Comment>());
			currentKeyLine = returnFromMarker(s);
		}
		else {
			JsonReadable jr = JsonReadable.fromString(s);
			Comment c = cf.getComment(jr);
			super.get(currentKeyLine).add(c);
		}
		return currentKeyLine;
	}

	@Override
	public ArrayList<String> getDataWriteLines() {
		ArrayList<String> lines = new ArrayList<String>();
		for (String key : this.keySet()) {
			lines.add(getKeyMarker(key));
			for (Comment c : this.get(key)) {
				lines.add(c.toString());
			}
		}
		return lines;
	}

	@Override
	public String getHeaderLine() {
		return null;
	}

	@Override
	public String getFooterLine() {
		return null;
	}

	@Override
	public DataType deepCopy() {
		return new KeywordList(this);
	}

	@Override
	public boolean isFull(int gbAllocated) {
		return this.size() > 10000*gbAllocated;
	}
	@Override
	public Iterator<String> getStringIter() {
		Iterator<String> keys = this.keySet().iterator();
		final KeywordList outer = this;
		Iterator<String> iter = new Iterator<String>() {
			private Iterator<Comment> vals;
			
			public boolean hasNext() {
				return keys.hasNext() || (vals != null && vals.hasNext());
			}
			public String next() {
				if ((vals == null || !vals.hasNext()) && keys.hasNext()) {
					String key = keys.next();
					vals = outer.get(key).iterator();
					return getKeyMarker(key);
				}
				else if (vals != null && vals.hasNext()) { //we're going to return, so yeah!
					return vals.next().toString();
				} //neither keys nor vals have a next
				throw new NoSuchElementException();
			}
		};
		return iter;
	}

}
