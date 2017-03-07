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
	private final SubsubMap relations;
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
		this.relations = new SubsubMap(sc);
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
		this.relations = new SubsubMap(sc);
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
				if (first && !relations.getKeysetOne().contains(subreddit) && !relations.getKeysetTwo().contains(subreddit)) {
					break;
				}
				else if (first) {
					first = false;
					origin = subreddit;
					continue;
				}
				matchingKeys.add(subreddit);
			}
			for (String destination : matchingKeys) {
				//we need to know if origin is sub or super!... keysetone contains subs, keysettwo contains supers... of course, it
				//could contain both... but not for the same key.
				StringState originss = null;
				StringState destss = null;
				if (relations.containsKey(origin, destination)) {
					originss = new StringState(origin, true);
					destss = new StringState(destination, false);
				}
				else if (relations.containsKey(destination, origin)) {
					originss = new StringState(origin, false);
					destss = new StringState(destination, true);
				}
				else {
					continue;
				}
				if (threadAggregate.containsKey(originss, destss)) {
					threadAggregate.put(originss,  destss, threadAggregate.get(originss, destss)+1);
				}
				else {
					threadAggregate.put(originss, destss, 1);
				}
				
				//otherwise, there is no match with this subreddit pair...
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
