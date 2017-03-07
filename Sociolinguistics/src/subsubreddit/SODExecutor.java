package subsubreddit;

import util.sys.Executor;
import wordmap.WordMap;

public class SODExecutor extends Executor<SubOriginDestProcessor4, PairMap, FlatKeyMap> {

	public SODExecutor() {
		super("sod4-fix", 4, SubOriginDestProcessor4.class, PairMap.class, FlatKeyMap.class);
	}
	public static void main(String[] args) {
		SODExecutor sode = new SODExecutor();
		sode.initializeFromCmdLine(args);
		sode.run();
	}

}
