package subsubreddit;

import subredditanalysis.filter.FiltConfusionCSV;
import util.sys.Executor;

public class SODExecutor extends Executor<SubOriginDestProcessor, FiltConfusionCSV, FlatKeyMap> {

	public SODExecutor() {
		super("sod", 4, SubOriginDestProcessor.class, FiltConfusionCSV.class, FlatKeyMap.class);
	}
	public static void main(String[] args) {
		SODExecutor sode = new SODExecutor();
		sode.initializeFromCmdLine(args);
		sode.run();
	}

}
