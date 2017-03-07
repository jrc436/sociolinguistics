package subsubreddit;

import util.sys.Executor;

public class RelativizeExecutor extends Executor<CountRelativizer, FlatKeyMap, RelativizedFlatKey>{

	public RelativizeExecutor() {
		super("relativize", 3, CountRelativizer.class, FlatKeyMap.class, RelativizedFlatKey.class);
	}
	public static void main(String[] args) {
		RelativizeExecutor re = new RelativizeExecutor();
		re.initializeFromCmdLine(args);
		re.run();
	}

}
