package armedconflicts;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import util.CSVReader;
import util.CSVWriter;
import util.JsonLayer;
import util.JsonReadable;
import util.StringCleaner;

public class CommentParser {
	public static void main(String[] args) throws IOException {
		//args[0] = directory where the comment files are
		//args[1] = exact path to the current CSV file
		//args[2] = output path for the new CSV file
		JsonLayer.inPlaceProcess(Paths.get(args[0]),".mod");
		CSVReader csv = new CSVReader(Paths.get(args[1]), '$');
		String[][] cells = csv.getCleanedCells();
		String[] conflictNames = csv.getColumnByTitle("Conflict/Country Name");
		String[] avgScore = new String[conflictNames.length+1];
		String[] avgControversy = new String[conflictNames.length+1];
		JsonLayer jl = new JsonLayer(Paths.get(args[0]));
		for (int i = 0; i < conflictNames.length; i++) {
			String fileConflict = StringCleaner.sanitizeForFiles(conflictNames[i]);
			List<JsonReadable> jsons = jl.getReadableByName(fileConflict);
			int totalScore = 0;
			int totalContro = 0;
			if (jsons == null) {
				System.err.println("Could not find any jsons for Conflict: "+fileConflict);
				continue;
			}
			for (JsonReadable j : jsons) {
				totalContro += Integer.parseInt(j.get("controversiality"));
				totalScore += Integer.parseInt(j.get("score"));
			}
			avgScore[i+1] = jsons.size() == 0 ? null : ""+(totalScore / jsons.size());
			avgControversy[i+1] = jsons.size() == 0 ? null : ""+(totalContro / jsons.size());
		}
		avgScore[0] = "Average Score of Comments";
		avgControversy[0] = "Average Controversy of Comments";
		String[][] newCells = CSVWriter.appendColumnCSV(CSVWriter.appendColumnCSV(cells, avgScore), avgControversy);
		CSVWriter write = new CSVWriter(newCells);
		write.writeCSV(Paths.get(args[2]));	
	}
}
