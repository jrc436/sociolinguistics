package subsubreddit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import util.collections.OrderedPair;
import util.collections.Pair;
import util.sys.DataType;
import util.sys.FileWritable;

public class PairMap extends HashMap<String, Pair<Integer, Integer>> implements DataType {


	private static final long serialVersionUID = -2891724380535833218L;

	public PairMap() {
		super();
	}
	public PairMap(PairMap other) {
		super(other);
	}
	
	@Override
	public int getNumFixedArgs() {
		return 0;
	}
	public void absorb(PairMap other) {
		for (String key : other.keySet()) {
			if (!this.containsKey(key)) {
				this.put(key, other.get(key));
			}
			else {
				Pair<Integer, Integer> p = other.get(key);
				Pair<Integer, Integer> p2 = this.get(key);
				this.put(key, new OrderedPair<Integer, Integer>(p.one()+p2.one(), p.two()+p2.two()));
			}
		}
	}

	@Override
	public boolean hasNArgs() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static PairMap createFromFile(File f) {
		PairMap pm = new PairMap();
		try {
			List<String> lines = Files.readAllLines(f.toPath());
			boolean first = true;
			for (String line : lines) {
				String[] parts = line.split(",");
				if (parts.length != 3) {
					throw new IllegalArgumentException(line+" does not seem to be from a PairMap");
				}
				if (first) {
					first = false;
					continue;
				}
				pm.put(parts[0], new OrderedPair<Integer, Integer>(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return pm;
	}
	
	@Override
	public String getHeaderLine() {
		return "subreddit,originations,adoptions";
	}

	@Override
	public String getConstructionErrorMsg() {
		return "needs no arguments";
	}
	
	private String getEntryString(Entry<String, Pair<Integer, Integer>> ent) {
		return ent.getKey()+","+ent.getValue().one()+","+ent.getValue().two();
	}

	@Override
	public Iterator<String> getStringIter() {
		return FileWritable.<Entry<String, Pair<Integer, Integer>>, Set<Entry<String, Pair<Integer, Integer>>>>iterBuilder(this.entrySet(), this::getEntryString);
	}

	@Override
	public DataType deepCopy() {
		return new PairMap(this);
	}

}
