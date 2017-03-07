package subsubreddit;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import util.collections.OrderedPair;
import util.collections.Pair;
import util.sys.FileProcessor;
import wordmap.SubredditListCombine;
import wordmap.WordMap;

public class SubOriginDestProcessor3 extends FileProcessor<WordMap, PairMap> {
	public SubOriginDestProcessor3() {
		super();
	}
	public SubOriginDestProcessor3(String input, String output, String[] hack) {
		super(input, output, new PairMap());
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
		return "doesn't need anything";
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
	public void map(WordMap newData, PairMap threadAggregate) {
		for (String word : newData.keySet()) {
			//we only care about first key set at all, because those are our originators.. (i think...)
			//the keys to our subsubs are the subsubs, and the values are the supersubs, so the keys will be our new key1s
			boolean first = true;
			Set<String> touchedReddits = new HashSet<String>();
			for (String subreddit : ((SubredditListCombine)newData.getBy(word, SubredditListCombine.class)).produceOrdering()) {
				if (touchedReddits.contains(subreddit)) {
					continue;
				}
				if (!threadAggregate.containsKey(subreddit)) {
					threadAggregate.put(subreddit, new OrderedPair<Integer, Integer>(0, 0));
				}
				Pair<Integer, Integer> p = threadAggregate.get(subreddit);
				if (first) {
					threadAggregate.put(subreddit, new OrderedPair<Integer, Integer>(p.one()+1, p.two()));
					first = false;
				}
				else {
					threadAggregate.put(subreddit, new OrderedPair<Integer, Integer>(p.one(), p.two()+1));
				}
				touchedReddits.add(subreddit);
			}
		}
	}

	@Override
	public void reduce(PairMap threadAggregate) {
		synchronized(processAggregate) {
			processAggregate.absorb(threadAggregate);
		}
	}
}
