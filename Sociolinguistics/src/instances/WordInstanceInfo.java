package instances;

import java.util.ArrayList;
import java.util.Collection;

import util.data.maps.DataCollection;
import util.sys.DataType;

public class WordInstanceInfo extends DataCollection<InstanceInfo> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2920428083691949343L;

	public WordInstanceInfo(WordInstanceInfo other) {
		super(other);
	}
	
	public WordInstanceInfo() {
		super();
	}
	@Override
	public DataType deepCopy() {
		return new WordInstanceInfo(this);
	}

	@Override
	protected Collection<InstanceInfo> getEmptyCollection() {
		return new ArrayList<InstanceInfo>();
	}
	
	public void absorb(WordInstanceInfo other) {
		for (String key : other.keySet()) {
			if (this.containsKey(key)) {
				this.get(key).addAll(other.get(key));
			}
			else {
				this.put(key, other.get(key));
			}
		}
	}

}
