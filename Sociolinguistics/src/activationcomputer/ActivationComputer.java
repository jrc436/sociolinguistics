package activationcomputer;

import util.sys.Executor;

public class ActivationComputer extends Executor<ActivationProcessor, RedditStream, ActivationEventList> {

	public ActivationComputer() {
		super("activation", 12, ActivationProcessor.class, RedditStream.class, ActivationEventList.class);
	}
	public static void main(String[] args) {
		ActivationComputer ac = new ActivationComputer();
		ac.initializeFromCmdLine(args);
		ac.run();
	}

}
