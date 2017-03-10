package instances;

import java.io.File;

import util.data.corpus.Comment;
import util.data.corpus.CommentFormat;
import util.data.maps.KeywordList;
import util.sys.FileProcessor;

public class InstanceProcessor extends FileProcessor<KeywordList, WordInstanceInfo> {

	private final CommentFormat cf;
	public InstanceProcessor() {
		super();
		this.cf = null;
	}
	public InstanceProcessor(String input, String output, String[] cf) {
		super(input, output, new WordInstanceInfo());
		this.cf = CommentFormat.fromString(cf[0]);
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
		return "doesn't require additional args";
	}

	@Override
	public KeywordList getNextData() {
		File f = super.getNextFile();
		if (f == null) {
			return null;
		}
		return KeywordList.createFromFile(f, cf);
	}

	@Override
	public void map(KeywordList newData, WordInstanceInfo threadAggregate) {
		for (String key : newData.keySet()) {
			String word = key;
			for (Comment c : newData.get(key)) {
				threadAggregate.get(word).add(new InstanceInfo(c.getTime(), c.getAuthor(), c.getField("subreddit")));
			}
		}
	}

	@Override
	public void reduce(WordInstanceInfo threadAggregate) {
		synchronized(processAggregate) {
			processAggregate.absorb(threadAggregate);
		}	
	}

}
