package subredditanalysis.users;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import util.generic.data.GenericMap;
import util.sys.DataType;

public class UserCountMap extends GenericMap<String, Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4208321971837603750L;
	public UserCountMap() {
		super();
	}
	public UserCountMap(UserCountMap m) {
		super(m);
	}
	@Override
	public DataType deepCopy() {
		return new UserCountMap(this);
	}
	
	
	public static UserCountMap fromFile(File f) {
		List<String> lines = null;
		UserCountMap ucm = new UserCountMap();
		try {
			lines = Files.readAllLines(f.toPath());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		for (String line : lines) {
			String[] parts = line.split(ucm.separator());
			if (parts.length != 2) {
				throw new IllegalArgumentException("File: "+f+" does not encode a UserCountMap");
			}
			ucm.put(parts[0], Integer.parseInt(parts[1]));
		}
		return ucm;
	}
	
	public void absorb(GenericMap<String, Integer> other) {
		for (String key : other.keySet()) {
			if (this.containsKey(key)) {
				this.put(key, this.get(key)+other.get(key));
			}
			else {
				this.put(key, other.get(key));
			}
		}
	}
	
}