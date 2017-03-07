package worddata;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import subsubreddit.SubCollection;
import subsubreddit.SubsubMap;
import util.generic.data.CSVList;
import util.sys.LineProcessor;
import wordmap.SubredditListCombine;
import wordmap.WordMap;

public class WordCSV extends LineProcessor<WordMap, CSVList> {
	private final SubsubMap relations;
	public WordCSV() {
		super();
		this.relations = null;
	}
	public WordCSV(String input, String output, String[] args, String[] garbArgs, String[] inpArgs) {
		super(input, output, new CSVList(inpArgs));
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
	public void map(WordMap newData, CSVList threadAggregate) {
		for (String word : newData.keySet()) {
			boolean first = true;
			String origin = "";
			Instant firstUseAbsolute = null;
			SubredditListCombine slc =  ((SubredditListCombine)newData.getBy(word, SubredditListCombine.class));
			int adopterNumber = 0;
			String subsupn = "n";
			Set<String> touchedReddits = new HashSet<String>();
			for (String subreddit : slc.produceOrdering()) {
				if (touchedReddits.contains(subreddit)) {
					continue;
				}
				if (first) {
					first = false;
					origin = subreddit;
					firstUseAbsolute = slc.getTime(subreddit);
				}
				
				if (relations.containsKey(origin, subreddit)) {
					//origin sub, sub super
					subsupn = "sup";
				}
				else if (relations.containsKey(subreddit, origin)) {
					subsupn = "sub";
				}
				String[] row = new String[7];
				row[0] = subreddit;
				row[1] = origin;
				row[2] = ""+firstUseAbsolute;
				row[3] = ""+slc.getTime(subreddit);
				row[4] = word;
				row[5] = subsupn;
				row[6] = ""+adopterNumber;
				threadAggregate.addRow(row);
				adopterNumber++;
				touchedReddits.add(subreddit);
			}	
		}
	}
}
