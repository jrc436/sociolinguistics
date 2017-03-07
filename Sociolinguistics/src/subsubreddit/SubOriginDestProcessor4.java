package subsubreddit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import util.collections.Pair;
import util.sys.FileProcessor;

public class SubOriginDestProcessor4 extends FileProcessor<PairMap, FlatKeyMap> {
	private final SubsubMap relations;
	public SubOriginDestProcessor4() {
		super();
		this.relations = null;
	}
	public SubOriginDestProcessor4(String input, String output, String[] args, String[] outArgs) {
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
	public SubOriginDestProcessor4(String input, String output, String[] args) {
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
	public PairMap getNextData() {
		File f = super.getNextFile();
		if (f == null) {
			return null;
		}
		return PairMap.createFromFile(f);
		//ddreturn FiltConfusionCSV.fromFile(f);
	}

	@Override
	public void map(PairMap newData, FlatKeyMap threadAggregate) {
		Set<String> keys1 = this.relations.getKeysetOne();
		Set<String> keys2 = this.relations.getKeysetTwo();
		for (String subreddit : newData.keySet()) {
			if (keys1.contains(subreddit)) {	
				Pair<Integer, Integer> origDests = newData.get(subreddit);
				for (String supe : this.relations.getPairedKeys(subreddit)) {
					threadAggregate.put(new StringState(subreddit, true), new StringState(supe, false), origDests.two());
					//Pair<Integer, Integer> origDestSupe = newData.get(supe);
					//threadAggregate.put(supe, subreddit, origDestSupe.one());
				}
			}
			else if (keys2.contains(subreddit)) {
				String supe = subreddit;
				Pair<Integer, Integer> origDests = newData.get(supe);
				for (String subsub : this.relations.getPairedKeys2(supe)) {
					threadAggregate.put(new StringState(supe, false), new StringState(subsub, true), origDests.two());
					//Pair<Integer, Integer> origDestSupe = newData.get(supe);
					//threadAggregate.put(supe, subreddit, origDestSupe.one());
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
