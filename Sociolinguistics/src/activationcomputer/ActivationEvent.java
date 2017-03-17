package activationcomputer;

public class ActivationEvent extends RedditEvent {
	private final double activation;
	public ActivationEvent(double activation, RedditEvent revent) {
		super(revent);
		this.activation = activation;
	}
	
	public String toString() {
		return super.toString()+","+activation;
	}
}
