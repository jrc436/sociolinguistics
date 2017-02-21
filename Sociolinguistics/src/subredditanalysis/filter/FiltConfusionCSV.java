package subredditanalysis.filter;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import util.data.dsv.UserConfusionCSV;
import util.sys.DataType;

public class FiltConfusionCSV extends UserConfusionCSV {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3787146983042145359L;
	public FiltConfusionCSV() {
		super();
	}
	public FiltConfusionCSV(boolean sym) {
		super(sym);
	}
	public FiltConfusionCSV(UserConfusionCSV ucc) {
		super(ucc);
	}
	@Override
	public DataType deepCopy() {
		return new FiltConfusionCSV(this);
	}
	public static FiltConfusionCSV fromFile(File f) {
		return new FiltConfusionCSV(UserConfusionCSV.fromFile(f));
	}
	//doesn't seem to work
	@Override
	public Iterator<String> getStringIter() {
		final FiltConfusionCSV outer = this;
		Iterator<String> iter = new Iterator<String>() {
			Iterator<String> vertLabels = outer.getKeysetOne().iterator();
			//Set<K> keysetOne = outer.getKeysetOne();
			public boolean hasNext() {
				return vertLabels.hasNext();
			}
			public String next() {
				String key2 = vertLabels.next();
				String line = key2.toString()+",";
				for (String key1 : outer.getFullKeySet()) {
					int shared = outer.containsKey(key1,key2) ? outer.get(key1, key2) : 0;
					line += shared + ",";
				}
				return line.substring(0, line.length()-1);
			}
		};
		return iter;
	}
	public void purgeEmptyRows() {
		Set<String> toPurge = new HashSet<String>();
		for (String s : this.getKeysetOne()) {
			boolean rowIsEmpty = true;
			for (String key : this.getPairedKeys(s)) {
				if (this.get(s, key) != 0) {
					rowIsEmpty = false;
				}
			}
			if (rowIsEmpty) {
				toPurge.add(s);
			}
		}
		for (String purge : toPurge) {
			this.purgeKey(purge);
		}
	}
	public void purgeEmptyColumns() {
		Set<String> toPurge = new HashSet<String>();
		for (String s : this.getKeysetTwo()) {
			boolean colIsEmpty = true;
			for (String key : this.getPairedKeys2(s)) {
				if (this.get(key, s) != 0) {
					colIsEmpty = false;
				}
			}
			if (colIsEmpty) {
				toPurge.add(s);
			}
		}
		for (String purge : toPurge) {
			this.purgeKey2(purge);
		}
	}
//	@Override
//	public String getHeaderLine() {
////		String line = "";
////		for (K key : super.getKeysetOne()) {
////			line += key.toString() + ",";
////		}
////		return line.substring(0, line.length()-1);
//		String line = "";
//		Set<String> keyset = super.getKeysetTwo();
//		for (String key : keyset) {
//			line += key.toString() + ",";
//		}
//		if (keyset.size() == 0) {
//			System.err.println("Error: no elements found.");
//			System.err.println("Collection should have: "+super.size()+" elements.");
//		}
//		return ","+line.substring(0, line.length()-1);
//	}
}
