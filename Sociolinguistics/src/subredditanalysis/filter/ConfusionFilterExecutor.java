package subredditanalysis.filter;

import util.data.dsv.UserConfusionCSV;
import util.sys.Executor;

public class ConfusionFilterExecutor extends Executor<ConfusionFilter, UserConfusionCSV, FiltConfusionCSV> {

	public ConfusionFilterExecutor() {
		super("filtermatrix", 4, ConfusionFilter.class, UserConfusionCSV.class, FiltConfusionCSV.class);
	}
	public static void main(String[] args) {
		ConfusionFilterExecutor cfe = new ConfusionFilterExecutor();
		cfe.initializeFromCmdLine(args);
		cfe.run();
	}

}
