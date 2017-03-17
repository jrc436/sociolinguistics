package activationcomputer;

import java.io.File;

import actr.DeclarativeMemory;
import util.sys.LineProcessor;

public class ActivationProcessor extends LineProcessor<RedditStream, ActivationEventList> {
	
	private final double negD;
	
	public ActivationProcessor() {
		super();
		this.negD = -0.5;

	}
	public ActivationProcessor(String input, String output, String[] negD) {
		super(input, output, new ActivationEventList());
		this.negD = Double.parseDouble(negD[0]);
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
	public void map(RedditStream newData, ActivationEventList threadAggregate) {
		//k is one, so we can compute it with just one occurrence! huzzah!
		
		//delay = t_k
		//negD = negD
		//time since origination = t_d
		//totalP = presentationNumber-1
		for (RedditEvent rev : newData) {
			int totalP = rev.getUsageNumber(); 
			double t_k = rev.getElapsedTime() + 0.05;
			double t_n = rev.timeSinceOrigination() + 0.1;
			double activation = DeclarativeMemory.getNaiveBaseActivation(negD, t_n, t_k, totalP);
			ActivationEvent actev = new ActivationEvent(activation, rev);
			threadAggregate.add(actev);
		}
	}
}
