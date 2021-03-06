package subredditanalysis.keywords;

import java.io.File;
import java.util.List;

import util.data.dsv.UserConfusionCSV;
import util.sys.FileProcessor;
import wordmap.SubredditListCombine;
import wordmap.WordMap;

public class OriginDestinationProcessor extends FileProcessor<WordMap, UserConfusionCSV> {
	public OriginDestinationProcessor() {
		super();
	}
	public OriginDestinationProcessor(String inpDir, String outDir) {
		super(inpDir, outDir, new UserConfusionCSV(false));
	}
	@Override
	public int getNumFixedArgs() {
		return 0;
	}

	@Override
	public boolean hasNArgs() {
		return false;
	}

	@Override
	public String getConstructionErrorMsg() {
		return "OriginDestionationProcessor needs no arguments. It also overrides its input and output to not need any.";
	}
	
	@Override
	public int overrideInputArgs() {
		return 0;
	}
	@Override
	public int overrideOutputArgs() {
		return 0;
	}

	@Override
	public WordMap getNextData() {
		File f = super.getNextFile();
		if ( f == null) {
			return null;
		}
		return WordMap.createFromFile(f);
	}

	@Override
	public void map(WordMap newData, UserConfusionCSV threadAggregate) {
		for (String key : newData.keySet()) {
			SubredditListCombine slc = (SubredditListCombine) newData.getBy(key, SubredditListCombine.class);
			List<String> subreddits = slc.produceOrdering();
			String origin = subreddits.remove(0);
			for (String dest : subreddits) {
				int startVal = threadAggregate.containsKey(origin, dest) ? threadAggregate.get(origin, dest) : 0;
				threadAggregate.put(origin, dest, startVal+1);
			}
		}
		//threadAggregate.computeConfusion();
	}

	@Override
	public void reduce(UserConfusionCSV threadAggregate) {
		synchronized(processAggregate) {
			processAggregate.absorb(threadAggregate);
			//processAggregate.computeConfusion();
		}
	}

}
