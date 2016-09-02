package util.listdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import util.data.Comment;
import util.data.CommentFormat;
import util.sys.DataType;

public class KeywordList extends DataCollection<Comment> {
	private static final long serialVersionUID = 6441745759936713041L;
	public KeywordList() { // dummy constructor
		super();
	}
	public KeywordList(String[] keywords, CommentFormat cf) {
		super(keywords, cf);
	}
	public KeywordList(KeywordList kl) {
		super(kl);
	}
	public KeywordList(List<String> fileLines, CommentFormat cf) {
		super(fileLines, cf);
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
	public DataType deepCopy() {
		return new KeywordList(this);
	}
	@Override
	protected Collection<Comment> getEmptyCollection() {
		return new ArrayList<Comment>();
	}
	@Override
	protected Comment getValue(Comment c) {
		return c;
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


}
