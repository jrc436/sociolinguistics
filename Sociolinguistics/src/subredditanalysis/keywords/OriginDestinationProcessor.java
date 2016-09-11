package subredditanalysis.keywords;

import java.io.File;

import util.data.CommentFormat;
import util.listdata.KeywordList;
import util.sys.FileProcessor;
import util.wordmap.WordMap;

public class OriginDestinationProcessor extends FileProcessor<KeywordList, WordMap> {
	private final CommentFormat cf;
	public OriginDestinationProcessor() {
		super();
		this.cf = null;
	}
	public OriginDestinationProcessor(String[] cf) {
		super();
		this.cf = CommentFormat.fromString(cf[0]);
	}
	@Override
	public int getNumFixedArgs() {
		return 1;
	}

	@Override
	public boolean hasNArgs() {
		return false;
	}

	@Override
	public String getConstructionErrorMsg() {
		return "OriginDestionationProcessor needs the CommentFormat.";
	}

	@Override
	public KeywordList getNextData() {
		File f = super.getNextFile();
		if ( f == null) {
			return null;
		}
		return KeywordList.createFromFile(f, cf);
	}

	@Override
	public void map(KeywordList newData, WordMap threadAggregate) {
		for (String subreddit : newData.keySet()) {
			for (String othersubreddit : newData.keySet()) {
				for (String user : newData.get(subreddit)) {
					if (newData.get(othersubreddit).contains(user)) {
						//append to their confusion
						threadAggregate.put(subreddit, othersubreddit, threadAggregate.get(subreddit, othersubreddit)+1);
					}
				}
			}
		}
	}

	@Override
	public void reduce(WordMap threadAggregate) {
	}

}
