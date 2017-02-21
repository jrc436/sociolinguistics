package subsubreddit;

import util.sys.Executor;
import wordmap.WordMap;

public class SODExecutor extends Executor<SubOriginDestProcessor2, WordMap, FlatKeyMap> {

	public SODExecutor() {
		super("sod2", 4, SubOriginDestProcessor2.class, WordMap.class, FlatKeyMap.class);
	}
	public static void main(String[] args) {
		SODExecutor sode = new SODExecutor();
		sode.initializeFromCmdLine(args);
		sode.run();
	}

}
