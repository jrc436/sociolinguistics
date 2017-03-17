package activationcomputer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import util.collections.DoubleKeyMap;
import util.collections.Pair;
import util.generic.data.GenericList;
import util.sys.DataType;
import util.sys.FileWritable;

public class RedditStream extends GenericList<RedditEvent> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5806511058830333785L;
	private final DoubleKeyMap<String, Integer, RedditEvent> delays; //uncomputed, partially ordered
	private final Map<String, List<RedditEvent>> events;//computed, ordered
	
	public RedditStream() {
		super();
		this.delays = new DoubleKeyMap<String, Integer, RedditEvent>();
		this.events = new HashMap<String, List<RedditEvent>>();
	}
	public RedditStream(RedditStream other) {
		super();
		this.delays = new DoubleKeyMap<String, Integer, RedditEvent>(other.delays);
		this.events = new HashMap<String, List<RedditEvent>>(other.events);
	}
	
	@Override
	public DataType deepCopy() {
		return new RedditStream(this);
	}
	public static RedditStream fromFile(File f) {
		List<String> lines = null;
		RedditStream retval = new RedditStream();
		try {
			lines = Files.readAllLines(f.toPath());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		for (String line : lines) {
			RedditEvent rev = RedditEvent.fromString(line);
			if (!retval.events.containsKey(rev.getWord())) {
				retval.events.put(rev.getWord(), new ArrayList<RedditEvent>());
			}
			retval.delays.put(rev.getWord(), rev.getAdopterNumber(), rev);
		}
		retval.mergeAll();
		return retval;
	}
	public void absorb(RedditStream other) {
		for (String key : other.events.keySet()) {
			if (this.events.containsKey(key)) {
				//this is a double zero problem....
				throw new RuntimeException("Both streams claim to contain the zero of a word...");
			}
			this.events.put(key, other.events.get(key));
		}
		for (Pair<String, Integer> key : other.delays.keySet()) {
			if (this.delays.containsKey(key)) {
				throw new RuntimeException("Both streams claim to contain the same event of a word...");
			}
			this.delays.put(key, other.delays.get(key));
		}
		mergeAll();
	}
	private void mergeAll() {
		for (String key : delays.getKeysetOne()) {
			tryMerge(key);
		}
	}
	private void tryMerge(String word) {
		List<RedditEvent> relEvents = events.get(word);
		while (delays.containsKey(word, relEvents.size())) { 
			RedditEvent prevEvent = relEvents.size() == 0 ? null : relEvents.get(relEvents.size()-1);
			RedditEvent thisEvent = delays.get(word, relEvents.size());
			relEvents.add(thisEvent);
			thisEvent.setDelay(prevEvent);
			delays.remove(word, relEvents.size()-1); //since we just added
		}
	}
	public void addAllDelays() {
		for (Pair<String, Integer> key : delays.keySet()) {
			if (delays.containsKey(key.one(), key.two()-1)) {
				delays.get(key).setDelay(delays.get(key.one(), key.two()-1));
			}
		}
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
		return "requires no args";
	}
	@Override
	public String getHeaderLine() {
		return "word,origination_time,origination_subreddit,origination_user,adoption_time,adoption_subreddit,adoption_user,adoption_number,usage_time,usage_subreddit,usage_user,usage_number";
	}

	@Override
	public Iterator<String> getStringIter() {
		return FileWritable.iterBuilder(delays.values());
	}

}
