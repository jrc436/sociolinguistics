package subredditanalysis.filter;

import util.data.dsv.UserConfusionCSV;
import util.sys.Executor;

public class ConfusionFilterExecutor extends Executor<ConfusionFilter, UserConfusionCSV, UserConfusionCSV> {

	public ConfusionFilterExecutor() {
		super("filtermatrix", 6, ConfusionFilter.class, UserConfusionCSV.class, UserConfusionCSV.class);
	}
	public static void main(String[] args) {
		ConfusionFilterExecutor cfe = new ConfusionFilterExecutor();
		cfe.initializeFromCmdLine(args);
		cfe.run();
	}

}
