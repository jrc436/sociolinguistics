package activationcomputer;

import util.generic.data.GenericList;
import util.sys.DataType;

public class ActivationEventList extends GenericList<ActivationEvent> {

	private static final long serialVersionUID = -4914666959885542901L;

	public ActivationEventList() {
		super();
	}
	public ActivationEventList(ActivationEventList o) {
		super(o);
	}
	
	@Override
	public DataType deepCopy() {
		return new ActivationEventList(this);
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
		return "requires nothing";
	}

}