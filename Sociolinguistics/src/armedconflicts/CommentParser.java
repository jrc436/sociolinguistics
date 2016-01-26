package armedconflicts;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import util.CSVReader;
import util.JsonLayer;
import util.JsonReadable;
import util.SentimentLayer;
import util.StringCleaner;
import edu.stanford.nlp.sentiment.SentimentPipeline;

public class CommentParser {
	private static final List<String> helpMsg = new ArrayList<String>();
	private static final String comDirName = "arg0";
	private static final String rdataCSV = "arg1";
	private static final String rdataOut = "arg2";
	private static final String yeardataCSV = "arg3";
	private static final String processBool = "arg4";
	private static final String processAddendum = "arg5";
	private static final String processRegex = "arg6";
	private static final char commaRep = '$';
	private static void initHelp() {	
		helpMsg.add(comDirName+": directory to comment files");
		helpMsg.add(rdataCSV+": path to CSV input file");
		helpMsg.add(rdataOut+": output path for the new CSV file");
		helpMsg.add(yeardataCSV+": the data with the year specific numbers for externalities");
		helpMsg.add(processBool+": Whether the comment files need to be processed");
		helpMsg.add(processAddendum+": The addendum to add to files that are processed");
		helpMsg.add(processRegex+": The regex files must match to be processed");
		helpMsg.add(comDirName+","+rdataCSV+","+rdataOut+","+yeardataCSV+" are always required");
		helpMsg.add(processAddendum+" is required if "+processBool+" is true. Recommended values e.g. '.mod' Default for "+processBool+" is false");
		helpMsg.add(processRegex+" is never required. Its default if "+processBool+"=false is to match all. If "+processBool+"=true, it's to match all with the addendum");
		helpMsg.add("Important Note: If "+processBool+" is false, then "+processAddendum+" must be omitted, or the order is wrong");
	}
	private static String help() {
		String retval = "";
		for (String s : helpMsg) {
			retval += s + System.getProperty("line.separator");
		}
		return retval;
	}
	public static void main(String[] args) throws IOException {
		initHelp();
		
		SentimentPipeline.main(args);
		
		boolean needsProcess = false;
		String match = null;
		if (args.length < 4) {
			System.err.println("Required arguments not given");
			System.err.println(help());
			System.exit(1);
		}
		if (args.length > 4) {
			needsProcess = Boolean.parseBoolean(args[4]);
			if (needsProcess && args.length < 5) {
				System.err.println(processAddendum+" is required but not given");
				System.err.println(help());
				System.exit(1);
			}	
			System.out.println("Comment files need processing: "+needsProcess);
		}
		if (needsProcess) {
			String append = args[5];
			System.out.println("Using: "+append+ " as the append string");
			JsonLayer.processInPlace(Paths.get(args[0]), append);
			System.out.println("Processing complete. Next run, use "+processBool+"=false, "+processRegex+"=[.]*"+append);
			if (args.length > 7) {
				System.err.println("You seem to have specified too many arguments. Please double check you did what you meant.");
				System.err.println(help());
				System.exit(1);
			}
			else if (args.length == 7) {
				match = args[6];
			}
			else {
				match = "[.]*"+append;
			}
		}
		else if (args.length > 6) {
			System.err.println("You should never have this many arguments if you're not processing, please double check");
			System.err.println(help());
			System.exit(1);
		}
		else if (args.length == 6) {
			match = args[5];
		}
		
		
		CSVReader csv = new CSVReader(Paths.get(args[1]), commaRep);
		Map<String, ConflictDated> conflictdates = ConflictDated.getConflictDated(args[3], commaRep);
		String[][] cells = csv.getCleanedCells();
		String[] conflictNames = csv.getColumnByTitle("Conflict");
		List<String> newCSVLines = new ArrayList<String>();
		JsonLayer jl = match == null ? new JsonLayer(Paths.get(args[0])) : new JsonLayer(Paths.get(args[0]), match);
		Calendar c = Calendar.getInstance();

		newCSVLines.add("Conflict,Region,Criminal,Foreign,Terrorism,Territorial,Ethnic,Separatist,Age,Intensity,Casualities,IDP,Refugees,"
			+ "Same-Year-Fatalities,Same-Year-Refugees,Same-Year-IDP,Score,Controversy,Sentiment,Author,Subreddit");
		
		FileWriter fw = new FileWriter(args[2]);
		SentimentLayer sl = new SentimentLayer();
		for (int i = 0; i < conflictNames.length; i++) {
			String fileConflict = StringCleaner.sanitizeForFiles(conflictNames[i]);
			List<JsonReadable> jsons = jl.getReadableByName(fileConflict+".txt");
			if (jsons == null) {
				System.err.println("Could not find any readable file at path: "+fileConflict+".txt");
				continue;
			}
			if (jsons.size() == 0) {
				System.err.println("No Jsons found at file path: "+fileConflict+".txt");
				continue;
			}
			for (JsonReadable j : jsons) {
				try {
					String line = "";
					for (int f = 0; f < 13; f++) {
						line += cells[i+1][f]+",";
					}
					long timestamp = Long.parseLong(j.get("created_utc"));
					Date d = new Date(timestamp * 1000);
					c.setTime(d);
					int year = c.get(Calendar.YEAR);
					ConflictDated con = conflictdates.get(fileConflict);
					line += con.getDataForYear(ConflictExternality.Fatalities, year)+",";
					line += con.getDataForYear(ConflictExternality.Refugees, year)+",";
					line += con.getDataForYear(ConflictExternality.IDP, year)+",";
					line += Integer.parseInt(j.get("score"))+",";
					line += Integer.parseInt(j.get("controversiality"))+",";
					line += sl.getOutput(j.get("body"));
					line += j.get("author")+",";
					line += j.get("subreddit");				
					newCSVLines.add(line);
				}
				catch (NumberFormatException e) {
					System.err.println("Trouble parsing numbers from Json: "+j.toString());
				}
			}
			for (String line : newCSVLines) {
				fw.write(line+System.getProperty("line.separator"));
			}
			fw.flush();
			newCSVLines.clear();
		}
		fw.close();
	}
}
