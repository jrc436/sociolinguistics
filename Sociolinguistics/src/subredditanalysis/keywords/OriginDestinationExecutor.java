package subredditanalysis.keywords;

import util.data.dsv.SConfusionCSV;
import util.sys.Executor;
import wordmap.WordMap;

public class OriginDestinationExecutor extends Executor<OriginDestinationProcessor, WordMap, SConfusionCSV> {

	public OriginDestinationExecutor() {
		super("origdest", 15, OriginDestinationProcessor.class, WordMap.class, SConfusionCSV.class);
	}
	public static void main(String[] args) {
		OriginDestinationExecutor ode = new OriginDestinationExecutor();
		ode.initializeFromCmdLine(args);
		ode.run();
	}
	
}
