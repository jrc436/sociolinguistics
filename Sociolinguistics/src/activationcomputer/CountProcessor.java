package activationcomputer;

import java.io.File;

import util.sys.FileProcessor;

public class CountProcessor extends FileProcessor<ActivationEventList, CountEventList> {

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
	public ActivationEventList getNextData() {
		File f = super.getNextFile();
		if (f == null) {
			return null;
		}
		return ActivationEventList.fromFile(f);
	}

	@Override
	public void map(ActivationEventList newData, CountEventList threadAggregate) {
		for (ActivationEvent ae : newData) {
			threadAggregate.addActEvent(ae);
		}
	}

	@Override
	public void reduce(CountEventList threadAggregate) {
		synchronized(processAggregate) {
			processAggregate.absorb(threadAggregate);
		}
	}

}
