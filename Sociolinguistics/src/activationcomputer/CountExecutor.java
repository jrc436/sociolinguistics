package activationcomputer;

import util.sys.Executor;

public class CountExecutor extends Executor<CountProcessor, ActivationEventList, CountEventList> {

	public CountExecutor() {
		super("counter", 10, CountProcessor.class, ActivationEventList.class, CountEventList.class);
	}
	public static void main(String[] args) {
		CountExecutor ce = new CountExecutor();
		ce.initializeFromCmdLine(args);
		ce.run();
	}
	
}
