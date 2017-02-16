package subredditanalysis.filter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import subsubreddit.SubCollection;
import util.data.dsv.UserConfusionCSV;
import util.sys.FileProcessor;

public class ConfusionFilter extends FileProcessor<UserConfusionCSV, UserConfusionCSV> {

	private final Set<String> subreddits;
	public ConfusionFilter() {
		super();
		subreddits = null;
	}
	public ConfusionFilter(String input, String output, String[] args) {
		super(input, output, new UserConfusionCSV(false));
		Set<String> subreddits = new HashSet<String>();
		try {
			SubCollection sc = new SubCollection(Files.readAllLines(Paths.get(args[0])));
			for (String key : sc.keySet()) {
				subreddits.add(key);
				subreddits.addAll(sc.get(key));
			}
		} catch (IOException e) {
			System.err.println("Error reading subcollection");
			e.printStackTrace();
			System.exit(1);
		}
		this.subreddits = subreddits;
	}
	
	@Override
	public int getNumFixedArgs() {
		return 1;
	}

	@Override
	public boolean hasNArgs() {
		return false;
	}
	//all we're going to use is the full set. all we really care about are the keys where both are present... but that's a bit more complicated?
	@Override
	public String getConstructionErrorMsg() {
		return "Needs a path to a data collection specifying subreddits to keep";
	}

	@Override
	public UserConfusionCSV getNextData() {
		File f = super.getNextFile();
		if (f == null) {
			return null;
		}
		return UserConfusionCSV.fromFile(f);
	}
	
	@Override
	public void map(UserConfusionCSV newData, UserConfusionCSV threadAggregate) {
		for (String key : newData.getKeysetOne()) {
			if (!subreddits.contains(key)) {
				continue;
			}
			for (String key2 : newData.getPairedKeys(key)) {
				if (!subreddits.contains(key)) {
					continue;
				}
				threadAggregate.put(key, key2, newData.get(key, key2));
			}
		}
	}

	@Override
	public void reduce(UserConfusionCSV threadAggregate) {
		synchronized(processAggregate) {
			processAggregate.absorb(threadAggregate);
		}	
	}

}
