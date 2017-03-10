package instances;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import util.data.corpus.Comment;
import util.data.maps.DataCollection;
import util.sys.DataType;

public class WordInstanceInfo extends DataCollection<InstanceInfo> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2920428083691949343L;

	public WordInstanceInfo(WordInstanceInfo other) {
		super(other);
	}
	
	public WordInstanceInfo() {
		super();
	}
	public WordInstanceInfo(List<String> fileLines) {
		super(fileLines);
	}
	
	@Override
	public DataType deepCopy() {
		return new WordInstanceInfo(this);
	}

	@Override
	protected Collection<InstanceInfo> getEmptyCollection() {
		return new ArrayList<InstanceInfo>();
	}
	
	public void absorb(WordInstanceInfo other) {
		for (String key : other.keySet()) {
			if (this.containsKey(key)) {
				this.get(key).addAll(other.get(key));
			}
			else {
				this.put(key, other.get(key));
			}
		}
	}
	@Override
	public InstanceInfo getValue(Comment c) {
		return new InstanceInfo(c.getTime(), c.getField("subreddit"), c.getAuthor());
	}
	@Override
	public InstanceInfo parseValue(String s) {
		String[] parts = s.split(",");
		return new InstanceInfo(Instant.parse(parts[0]), parts[1], parts[2]);
	}
	

}
