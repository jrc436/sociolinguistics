package activationcomputer;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;

import util.collections.DoubleKeyMap;
import util.collections.Pair;
import util.generic.data.GenericList;
import util.sys.DataType;

public class CountEventList extends GenericList<CountEvent> {

	private static final long serialVersionUID = -5879822200172647270L;

	
	private final DoubleKeyMap<String, LocalDate, Integer> wordsPerDay;
	private final ArrayList<ActivationEvent> store;
	
	public CountEventList() {
		super();
		this.wordsPerDay = new DoubleKeyMap<String, LocalDate, Integer>();
		this.store = new ArrayList<ActivationEvent>();
	}
	public CountEventList(CountEventList other) {
		super(other);
		this.wordsPerDay = new DoubleKeyMap<String, LocalDate, Integer>(other.wordsPerDay);
		this.store = new ArrayList<ActivationEvent>();
	}
	
	public void addActEvent(ActivationEvent ae) {
		LocalDate p = fromInstant(ae.getUsageTime());
		if (!wordsPerDay.containsKey(ae.getWord(), p)) {
			wordsPerDay.put(ae.getWord(), p,  0);
		}
		wordsPerDay.put(ae.getWord(), p,  wordsPerDay.get(ae.getWord(), p)+1);
		store.add(ae);
	}
	private static LocalDate fromInstant(Instant in) {
		LocalDate ld = in.atZone(ZoneId.systemDefault()).toLocalDate();
		return ld;
	}
	private void reduce() {
		for (ActivationEvent ae : store) {
			LocalDate p = fromInstant(ae.getUsageTime());
			int count = wordsPerDay.get(ae.getWord(), p);
			this.add(new CountEvent(ae, count));
		}
		store.clear();
		wordsPerDay.clear();
	}
	public void absorb(CountEventList other) {
		this.store.addAll(other.store);
		for (Pair<String, LocalDate> key : other.wordsPerDay.keySet()) {
			if (this.wordsPerDay.containsKey(key)) {
				this.wordsPerDay.put(key, this.wordsPerDay.get(key)+other.wordsPerDay.get(key));
			}
			else {
				this.wordsPerDay.put(key, other.wordsPerDay.get(key));
			}
		}
	}
	@Override
	public Iterator<String> getStringIter() {
		reduce();
		return super.getStringIter();
	}
	
	
	@Override
	public DataType deepCopy() {
		return new CountEventList(this);
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
		return "needs nothing";
	}

}
