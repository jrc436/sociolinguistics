package activationcomputer;

import util.sys.Executor;

public class DelayAddExecutor extends Executor<RedditDelayAdder, RedditStream, RedditStream> {

	public DelayAddExecutor() {
		super("delayadds", 30, RedditDelayAdder.class, RedditStream.class, RedditStream.class);
		// TODO Auto-generated constructor stub
	}
	public static void main(String[] args) {
		DelayAddExecutor dae = new DelayAddExecutor();
		dae.initializeFromCmdLine(args);
		dae.run();
	}

}
