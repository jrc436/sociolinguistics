package subsubreddit;

import util.data.dsv.UserConfusionCSV;
import util.sys.Executor;

public class SubsetExecutor extends Executor<SubsetProcessor, UserConfusionCSV, SubCollection> {

	public SubsetExecutor() {
		super("subse", 7, SubsetProcessor.class, UserConfusionCSV.class, SubCollection.class);
		// TODO Auto-generated constructor stub
	}
	public static void main(String[] args) {
		SubsetExecutor sse = new SubsetExecutor();
		sse.initializeFromCmdLine(args);
		sse.run();
	}

}
