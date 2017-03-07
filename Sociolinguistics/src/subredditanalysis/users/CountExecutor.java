package subredditanalysis.users;

import util.data.maps.UserList;
import util.sys.Executor;

public class CountExecutor extends Executor<CountProcessor, UserList, UserCountMap> {

	public CountExecutor() {
		super("counting", 2, CountProcessor.class, UserList.class, UserCountMap.class);
	}
	public static void main(String[] args) {
		CountExecutor ce = new CountExecutor();
		ce.initializeFromCmdLine(args);
		ce.run();
	}
}
