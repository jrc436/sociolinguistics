package instances;

import util.data.maps.KeywordList;
import util.sys.Executor;

public class InstanceExecutor extends Executor<InstanceProcessor, KeywordList, WordInstanceInfo> {

	public InstanceExecutor() {
		super("buildinstances", 25, InstanceProcessor.class, KeywordList.class, WordInstanceInfo.class);
	}
	public static void main(String[] args) {
		InstanceExecutor ie = new InstanceExecutor();
		ie.initializeFromCmdLine(args);
		ie.run();
	}
}
