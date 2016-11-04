package subredditanalysis.users;

import util.data.json.JsonList;
import util.data.maps.UserList;
import util.sys.Executor;

public class SubredditUserExecutor extends Executor<SubredditUserProcessor, JsonList, UserList> {
	private static final String name = "useralignment";
	private static final int gbPerThread = 15;
	
	public SubredditUserExecutor() {
		super(name, gbPerThread, SubredditUserProcessor.class, JsonList.class, UserList.class);
	}

	public static void main(String args[]) {
		SubredditUserExecutor fm = new SubredditUserExecutor();
		fm.initializeFromCmdLine(args);
		fm.run();
	}
}

