package subredditanalysis.users;

import util.data.dsv.UserConfusionCSV;
import util.data.maps.UserList;
import util.sys.Executor;

public class SubredditAlignmentExecutor extends Executor<SubredditAlignmentProcessor, UserList, UserConfusionCSV> {
	private static final String name = "subalignment";
	private static final int gbPerThread = 15;
	
	public SubredditAlignmentExecutor() {
		super(name, gbPerThread, SubredditAlignmentProcessor.class, UserList.class, UserConfusionCSV.class);
	}

	public static void main(String args[]) {
		SubredditAlignmentExecutor fm = new SubredditAlignmentExecutor();
		fm.initializeFromCmdLine(args);
		fm.run();
	}
}
