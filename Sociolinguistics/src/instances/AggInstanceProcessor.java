package instances;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import util.sys.FileProcessor;

public class AggInstanceProcessor extends FileProcessor<WordInstanceInfo, AggInstanceInfo> {
	public AggInstanceProcessor() {
		super();
	}
	public AggInstanceProcessor(String inp, String out) {
		super(inp, out, new AggInstanceInfo());
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
		return "needs no args";
	}

	@Override
	public WordInstanceInfo getNextData() {
		File f = super.getNextFile();
		if (f == null) {
			return null;
		}
		try {
			List<String> fileLines = Files.readAllLines(f.toPath());
			return new WordInstanceInfo(fileLines);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	@Override
	public void map(WordInstanceInfo newData, AggInstanceInfo threadAggregate) {
		for (String word : newData.keySet()) {
			for (InstanceInfo event : newData.get(word)) {
				threadAggregate.addEvent(word, event);
			}
		}
	}

	@Override
	public void reduce(AggInstanceInfo threadAggregate) {
		synchronized(processAggregate) {
			processAggregate.merge(threadAggregate);
		}	
	}

}
