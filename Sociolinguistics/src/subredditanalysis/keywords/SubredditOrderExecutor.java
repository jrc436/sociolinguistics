package subredditanalysis.keywords;

import util.data.maps.KeywordList;
import util.sys.Executor;
import wordmap.WordMap;

public class SubredditOrderExecutor extends Executor<SubredditOrderProcessor, KeywordList, WordMap> {

	public SubredditOrderExecutor() {
		super("srorder", 15, SubredditOrderProcessor.class, KeywordList.class, WordMap.class);
	}
	public static void main(String[] args) {
		SubredditOrderExecutor soe = new SubredditOrderExecutor();
		soe.initializeFromCmdLine(args);
		soe.run();
	}

}
