package subsubreddit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;

import util.collections.DoubleKeyMap;
import util.sys.DataType;

public class SubsubMap extends DoubleKeyMap<String, String, Boolean> implements DataType {
	private final SubCollection internal;
	private static final long serialVersionUID = 3706979651269425309L;
	public SubsubMap() {
		super(false);
		this.internal = null;
	}
	//saving time....!
	public SubsubMap(SubCollection sc) {
		this.internal = sc;
		for (String key : sc.keySet()) {
			for (String val : sc.get(key)) {
				this.put(key, val, true);
			}
		}
	}
	public static SubsubMap fromFile(File f) {
		SubCollection s = null;
		try {
			s = new SubCollection(Files.readAllLines(f.toPath()));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return new SubsubMap(s);
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
	@Override
	public Iterator<String> getStringIter() {
		return internal.getStringIter();
	}
	@Override
	public DataType deepCopy() {
		return new SubsubMap(internal);
	}
}
