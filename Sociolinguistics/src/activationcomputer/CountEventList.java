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

	
	private final DoubleKeyMap<String, LocalDate, ActCount> wordDays;
	private final ArrayList<ActivationEvent> store;
	
	private class ActCount {
		private final int count;
		private final double act;
		public ActCount(int count, double act) {
			this.count = count;
			this.act = act;
		}
		public ActCount(ActCount first, ActCount other) {
			this.count = first.count + other.count;
			this.act = first.act + other.act;
		}
	}
	
	public CountEventList() {
		super();
		this.wordDays = new DoubleKeyMap<String, LocalDate, ActCount>();
		this.store = new ArrayList<ActivationEvent>();
	}
	public CountEventList(CountEventList other) {
		super(other);
		this.wordDays = new DoubleKeyMap<String, LocalDate, ActCount>(other.wordDays);
		this.store = new ArrayList<ActivationEvent>();
	}
	
	public void addActEvent(ActivationEvent ae) {
		LocalDate p = fromInstant(ae.getUsageTime());
		if (!wordDays.containsKey(ae.getWord(), p)) {
			wordDays.put(ae.getWord(), p,  new ActCount(0,0));
		}
		ActCount cur = wordDays.get(ae.getWord(), p);
		ActCount newAC = new ActCount(cur.count+1, cur.act + ae.getActivation());
		wordDays.put(ae.getWord(), p, newAC);
		store.add(ae);
	}
	private static LocalDate fromInstant(Instant in) {
		LocalDate ld = in.atZone(ZoneId.systemDefault()).toLocalDate();
		return ld;
	}
	private void reduce() {
		for (ActivationEvent ae : store) {
			LocalDate p = fromInstant(ae.getUsageTime());
			ActCount ac = wordDays.get(ae.getWord(), p);
			double act = ac.act / (double)ac.count;
			this.add(new CountEvent(ae, ac.count, act));
		}
		store.clear();
		wordDays.clear();
	}
	public void absorb(CountEventList other) {
		this.store.addAll(other.store);
		for (Pair<String, LocalDate> key : other.wordDays.keySet()) {
			if (this.wordDays.containsKey(key)) {
				this.wordDays.put(key, new ActCount(this.wordDays.get(key),other.wordDays.get(key)));
			}
			else {
				this.wordDays.put(key, other.wordDays.get(key));
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
