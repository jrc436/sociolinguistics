package worddata;

import util.generic.data.CSVList;
import util.sys.Executor;
import wordmap.WordMap;

public class WordCSVExec extends Executor<WordCSV, WordMap, CSVList> {

	public WordCSVExec() {
		super("wordcsv", 4, WordCSV.class, WordMap.class, CSVList.class);
	}
	public static void main(String[] args) {
		WordCSVExec wse = new WordCSVExec();
		wse.initializeFromCmdLine(args);
		wse.run();
	}
}
