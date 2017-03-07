package subredditanalysis.comments;

import subredditanalysis.users.UserCountMap;
import util.data.json.JsonList;
import util.sys.Executor;

public class CommentCountExecutor extends Executor<CommentCounter, JsonList, UserCountMap> {

	public CommentCountExecutor() {
		super("countcomments", 12, CommentCounter.class, JsonList.class, UserCountMap.class);
	}
	public static void main(String[] args) {
		CommentCountExecutor cce = new CommentCountExecutor();
		cce.initializeFromCmdLine(args);
		cce.run();
	}

}
