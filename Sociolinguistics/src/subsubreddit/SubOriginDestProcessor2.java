package subsubreddit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import util.sys.FileProcessor;
import wordmap.SubredditListCombine;
import wordmap.WordMap;

public class SubOriginDestProcessor2 extends FileProcessor<WordMap, FlatKeyMap> {
	private final SubCollection relations;
	public SubOriginDestProcessor2() {
		super();
		this.relations = null;
	}
	public SubOriginDestProcessor2(String input, String output, String[] args, String[] outArgs) {
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
	public SubOriginDestProcessor2(String input, String output, String[] args) {
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
	public WordMap getNextData() {
		File f = super.getNextFile();
		if (f == null) {
			return null;
		}
		return WordMap.createFromFile(f);
		//ddreturn FiltConfusionCSV.fromFile(f);
	}

	@Override
	public void map(WordMap newData, FlatKeyMap threadAggregate) {
		for (String word : newData.keySet()) {
			//we only care about first key set at all, because those are our originators.. (i think...)
			//the keys to our subsubs are the subsubs, and the values are the supersubs, so the keys will be our new key1s
			boolean first = true;
			Set<String> matchingKeys = new HashSet<String>();
			String origin = null;
			for (String subreddit : ((SubredditListCombine)newData.getBy(word, SubredditListCombine.class)).produceOrdering()) {
				if (first && !relations.containsKey(subreddit)) {
					break;
				}
				else if (first) {
					first = false;
					origin = subreddit;
					continue;
				}
				if (relations.get(origin).contains(subreddit)) {
					matchingKeys.add(subreddit);
				}
			}
			for (String destination : matchingKeys) {
				if (threadAggregate.containsKey(origin, destination)) {
					threadAggregate.put(origin,  destination, threadAggregate.get(origin, destination)+1);
				}
				else {
					threadAggregate.put(origin, destination, 1);
				}
			}
		}
	}

	@Override
	public void reduce(FlatKeyMap threadAggregate) {
		synchronized(processAggregate) {
			processAggregate.absorb(threadAggregate);
		}
	}
}
