package instances;

import util.sys.Executor;

public class AggInstanceExecutor extends Executor<AggInstanceProcessor, WordInstanceInfo, AggInstanceInfo> {

	public AggInstanceExecutor() {
		super("wordadd", 25, AggInstanceProcessor.class, WordInstanceInfo.class, AggInstanceInfo.class);
		// TODO Auto-generated constructor stub
	}
	public static void main(String[] args) {
		AggInstanceExecutor aie = new AggInstanceExecutor();
		aie.initializeFromCmdLine(args);
		aie.run();
	}
}
