package wordmap.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import filter.Filter;
import util.sys.FileProcessor;
import wordmap.WordMap;


public class WMFilterProcessor extends FileProcessor<WordMap, WordMap> {
	private String createLine;
	private final Filter filter;
	public WMFilterProcessor() {
		super();
		this.filter = null;
	}
	public WMFilterProcessor(String inp, String out, String[] filterArgs) {
		super(inp, out);
		filter = Filter.getFilter(filterArgs);
		try {
			BufferedReader br = new BufferedReader(new FileReader(super.peek()));
			createLine = br.readLine();
			br.close();
		} catch (FileNotFoundException e) {
			System.err.println("This really shouldn't happen because you're using a file that was procedurally generated.");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Some type of error reading the file.");
			e.printStackTrace();
			System.exit(1);
		}
		setInitialValue(new WordMap(createLine));
	}
	@Override
	public int getNumFixedArgs() {
		return 0;
	}
	@Override
	public boolean hasNArgs() {
		return true;
	}
	@Override
	protected int overrideInputArgs() {
		return 0;
	}
	@Override
	protected int overrideOutputArgs() {
		return 0;
	}
	@Override
	public String getConstructionErrorMsg() {
		return "Please specify one or more fully qualified filters or nicknames: "+Filter.getKnownFilters();
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
	public void reduce(WordMap data) {
		synchronized (processAggregate) {
			for (String key : data.keySet()) {
				processAggregate.put(key, data.get(key));
			}
		}
	}

	@Override
	public void map(WordMap dataIn, WordMap workerAgg) {
		filter.filter(dataIn);
		for (String key : dataIn.keySet()) {
			workerAgg.put(key, dataIn.get(key));
		}
	}
	

}
