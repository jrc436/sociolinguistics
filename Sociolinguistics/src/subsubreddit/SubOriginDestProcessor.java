package subsubreddit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import subredditanalysis.filter.FiltConfusionCSV;
import util.sys.FileProcessor;

public class SubOriginDestProcessor extends FileProcessor<FiltConfusionCSV, FlatKeyMap> {
	private final SubCollection relations;
	public SubOriginDestProcessor() {
		super();
		this.relations = null;
	}
	public SubOriginDestProcessor(String input, String output, String[] args, String[] outArgs) {
		super(input, output, new FlatKeyMap(outArgs));
		SubCollection sc = null;
		try {
			sc = new SubCollection(Files.readAllLines(Paths.get(args[0])));
		} catch (IOException e) {
			System.err.println("Error reading subcollection");
			e.printStackTrace();
			System.exit(1);
		}
		this.relations = sc;
	}
	public SubOriginDestProcessor(String input, String output, String[] args) {
		super(input, output, new FlatKeyMap());
		SubCollection sc = null;
		try {
			sc = new SubCollection(Files.readAllLines(Paths.get(args[0])));
		} catch (IOException e) {
			System.err.println("Error reading subcollection");
			e.printStackTrace();
			System.exit(1);
		}
		this.relations = sc;
	}
	
	@Override
	public int getNumFixedArgs() {
		return 1;
	}

	@Override
	public boolean hasNArgs() {
		return false;
	}

	@Override
	public String getConstructionErrorMsg() {
		return "needs the subsubs";
	}

	@Override
	public FiltConfusionCSV getNextData() {
		File f = super.getNextFile();
		if (f == null) {
			return null;
		}
		return FiltConfusionCSV.fromFile(f);
	}

	@Override
	public void map(FiltConfusionCSV newData, FlatKeyMap threadAggregate) {
		for (String key : newData.getKeysetOne()) {
			//we only care about first key set at all, because those are our originators.. (i think...)
			//the keys to our subsubs are the subsubs, and the values are the supersubs, so the keys will be our new key1s
			if (relations.containsKey(key)) {
				Collection<String> supers = relations.get(key);
				for (String supe : supers) {
					int val = newData.containsKey(key, supe) ? newData.get(key, supe) : 0;
					if (val != 0) {
						threadAggregate.put(key, supe, val);
					}
				}
				continue; //avoid an else statement indentation?
			}
			//now it's either a value or a nothing. it should be a value since we've hopefully filtered well enough that all rows are meaningful
			//wow what a pain, nwo we need to get all the keys with this value? well ok fine, let's just bruteforce it and get it over with
			Set<String> matchingKeys = new HashSet<String>();
			for (String potentialMatch : relations.keySet()) {
				if (relations.get(potentialMatch).contains(key)) {
					matchingKeys.add(potentialMatch);
				}
			}
			if (matchingKeys.isEmpty()) {
				System.err.println("Check to make sure row with key: "+key+" is actually useful...");
			}
			for (String sub : matchingKeys) {
				int val = newData.containsKey(key, sub) ? newData.get(key, sub) : 0; //only care about origins, so keep data same here.
				if (val != 0) {
					threadAggregate.put(sub, key, val); //flip the order, since in this case, key is the supe and we only care about origins
				}
			}
			
		}
	}

	@Override
	public void reduce(FlatKeyMap threadAggregate) {
		synchronized(processAggregate) {
			processAggregate.putAll(threadAggregate); ///ehh, should work. since we're adding by row, anyway
		}
	}
}
