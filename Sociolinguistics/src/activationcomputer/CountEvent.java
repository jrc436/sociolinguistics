package activationcomputer;

public class CountEvent extends ActivationEvent {
	private final int count;
	public CountEvent(ActivationEvent e, int count) {
		super(e);
		this.count = count;
	}
	public String toString() {
		return super.toString()+","+count;
	}

}
