package activationcomputer;

public class CountEvent extends ActivationEvent {
	private final int count;
	private final double avgDailyAct;
	public CountEvent(ActivationEvent e, int count, double avgAct) {
		super(e);
		this.count = count;
		this.avgDailyAct = avgAct;
	}
	public String toString() {
		return super.toString()+","+count+","+avgDailyAct;
	}

}
