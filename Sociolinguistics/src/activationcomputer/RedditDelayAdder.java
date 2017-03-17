package activationcomputer;

import java.io.File;

import util.sys.FileProcessor;

public class RedditDelayAdder extends FileProcessor<RedditStream, RedditStream>{
	
	public RedditDelayAdder() {
		super();
	}
	public RedditDelayAdder(String input, String output) {
		super(input, output, new RedditStream());
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
	public RedditStream getNextData() {
		File f = super.getNextFile();
		if (f == null) {
			return null;
		}
		return RedditStream.fromFile(f);
	}

	@Override
	public void map(RedditStream newData, RedditStream threadAggregate) {
		threadAggregate.absorb(newData);
	}

	@Override
	public void reduce(RedditStream threadAggregate) {
		synchronized(processAggregate) {
			processAggregate.absorb(threadAggregate);
		}	
	}
}
