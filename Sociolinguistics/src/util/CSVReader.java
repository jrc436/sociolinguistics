package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CSVReader {
	private final String[][] cells;
	public CSVReader(Path p, char commaRep) {
		List<String> lines = null;
		try {
			lines = Files.readAllLines(p);
		} catch (IOException e) {
			System.err.println("Failed to read from Path: "+p.toString());
			e.printStackTrace();
			System.exit(1);
		}
		cells = new String[lines.get(0).split(",").length][lines.size()];
		for (int i = 0; i < lines.size(); i++) {
			String[] lineCells = lines.get(i).split(",");
			for (int j = 0; j < lineCells.length; j++) {
				cells[i][j] = lineCells[j].replace(commaRep, ',');
			}	
		}
	}
	public String[] getVectorByTitle(String title) {
		int titleVec = -1;
		for (int i = 0; i < cells[0].length; i++) {
			if (cells[0][i].contains(title)) {
				titleVec = i;
				break;
			}
		}
		String[] vec = new String[cells[0].length-1];
		for (int i = 1; i < vec.length; i++) {
			vec[i-1] = cells[titleVec][i];
		}
		return vec;
	}
	
}
