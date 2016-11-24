package subredditanalysis.keywords;

import util.data.dsv.UserConfusionCSV;
import util.sys.Executor;
import wordmap.WordMap;

public class OriginDestinationExecutor extends Executor<OriginDestinationProcessor, WordMap, UserConfusionCSV> {

	public OriginDestinationExecutor() {
		super("origdest", 15, OriginDestinationProcessor.class, WordMap.class, UserConfusionCSV.class);
	}
	public static void main(String[] args) {
		OriginDestinationExecutor ode = new OriginDestinationExecutor();
		ode.initializeFromCmdLine(args);
		ode.run();
	}
	
}
