package armedconflicts;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import util.CSVReader;
import util.StringCleaner;

public class ConflictDated {
	private final HashMap<ConflictExternality, Integer[]> data;
	public ConflictDated() {
		this.data = new HashMap<ConflictExternality, Integer[]>();
	}
	public static Map<String, ConflictDated> getConflictDated(String pathToCSV, char commaRep) {
		CSVReader csv = new CSVReader(Paths.get(pathToCSV), commaRep);
		String[] names = csv.getColumnByTitle("Conflict");
		Map<String, ConflictDated> conflicts = new HashMap<String, ConflictDated>();
		for (String name : names) {
			String n = StringCleaner.sanitizeForFiles(name);
			conflicts.put(n, new ConflictDated());
		}
		
		for (int i = 2012; i < 2015; i++) {
			for (ConflictExternality c : ConflictExternality.values()) {
				String[] data = csv.getColumnByTitle(i+" "+c.toString());
				for (int j = 0; j < conflicts.size(); j++) {
					//conflicts should be in the same order as data and names
					if (data[j].equals("NULL")) {
						conflicts.get(j).appendData(c, i, -1);
					}
					else {
						conflicts.get(j).appendData(c, i, Integer.parseInt(data[j]));
					}
				}
			}
		}
		return conflicts;
	}
	public int getDataForYear(ConflictExternality c, int year) {
		return data.get(c)[year-2012];
	}
	private void appendData(ConflictExternality c, Integer year, Integer value) {
		if (!data.containsKey(c)) {
			data.put(c, new Integer[3]);
		}
		data.get(c)[year-2012] = value;
	}
}
enum ConflictExternality {
	Refugees,
	Fatalities,
	IDP;
}
