package armedconflicts;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import util.CSVReader;
import util.JsonLayer;
import util.JsonReadable;
import util.SentimentLayer;
import util.StringCleaner;

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
		helpMsg.add(comDirName + ": directory to comment files");
		helpMsg.add(rdataCSV + ": path to CSV input file");
		helpMsg.add(rdataOut + ": output path for the new CSV file");
		helpMsg.add(yeardataCSV
				+ ": the data with the year specific numbers for externalities");
		helpMsg.add(processBool
				+ ": Whether the comment files need to be processed");
		helpMsg.add(processAddendum
				+ ": The addendum to add to files that are processed");
		helpMsg.add(processRegex
				+ ": The regex files must match to be processed");
		helpMsg.add(comDirName + "," + rdataCSV + "," + rdataOut + ","
				+ yeardataCSV + " are always required");
		helpMsg.add(processAddendum + " is required if " + processBool
				+ " is true. Recommended values e.g. '.mod' Default for "
				+ processBool + " is false");
		helpMsg.add(processRegex + " is never required. Its default if "
				+ processBool + "=false is to match all. If " + processBool
				+ "=true, it's to match all with the addendum");
		helpMsg.add("Important Note: If " + processBool + " is false, then "
				+ processAddendum + " must be omitted, or the order is wrong");
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
				System.err.println(processAddendum
						+ " is required but not given");
				System.err.println(help());
				System.exit(1);
			}
			System.out.println("Comment files need processing: " + needsProcess);
		}
		if (needsProcess) {
			String append = args[5];
			System.out.println("Using: " + append + " as the append string");
			JsonLayer.processInPlace(Paths.get(args[0]), append);
			System.out.println("Processing complete. Next run, use "+ processBool + "=false, " + processRegex + "=[.]*"+ append);
			if (args.length > 7) {
				System.err.println("You seem to have specified too many arguments. Please double check you did what you meant.");
				System.err.println(help());
				System.exit(1);
			} else if (args.length == 7) {
				match = args[6];
			} else {
				match = "[.]*" + append;
			}
		} else if (args.length > 6) {
			System.err.println("You should never have this many arguments if you're not processing, please double check");
			System.err.println(help());
			System.exit(1);
		} else if (args.length == 6) {
			match = args[5];
		}

		CSVReader csv = new CSVReader(Paths.get(args[1]), commaRep);		
		
		Map<String, ConflictDated> conflictdates = ConflictDated.getConflictDated(args[3], commaRep);
		String[][] cells = csv.getCleanedCells();
		JsonLayer jl = match == null ? new JsonLayer(Paths.get(args[0])) : new JsonLayer(Paths.get(args[0]), match);	
		FileWriter fw = new FileWriter(args[2]);	
		SentimentLayer sl = new SentimentLayer();
		
		ExecutorService es = Executors.newFixedThreadPool(8);
		
		fw.write("Conflict,Region,Criminal,Foreign,Terrorism,Territorial,Ethnic,Separatist,Age,Intensity,Casualities,IDP,Refugees,"
				+ "Same-Year-Fatalities,Same-Year-Refugees,Same-Year-IDP,Score,Controversy,Sentiment,Author,Subreddit"+System.getProperty("line.separator"));
		fw.flush();
		
		String[] conflictNames = csv.getColumnByTitle("Conflict");
		for (int i = 0; i < conflictNames.length; i++) {
			String fileConflict = StringCleaner.sanitizeForFiles(conflictNames[i]);
			System.out.println("Starting Thread: "+i+" with conflict: "+fileConflict);
			es.execute(new Worker(sl, conflictdates, jl, cells, fw, i, fileConflict));			
		}
		es.shutdown();

	}

}

class Worker implements Runnable {
	private final SentimentLayer sl;
	private final ConflictDated conflictdates;
	private List<JsonReadable> jl;
	private String prefix;
	private final Calendar c;
	private final FileWriter fw;
	public Worker(SentimentLayer sl, Map<String, ConflictDated> conflictdates, JsonLayer j, String[][] cells, FileWriter fw, int conflictNum, String fileConflict) {
		this.conflictdates = conflictdates.get(fileConflict);
		this.sl = sl;
		this.jl = j.getReadableByName(fileConflict);
		c = Calendar.getInstance();
		this.fw = fw;
		prefix = "";
		for (int f = 0; f < 13; f++) {
			prefix += cells[conflictNum + 1][f] + ",";
		}
		if (jl == null) {
			System.err.println("Could not find any readable file containing name: "+ fileConflict);
			jl = new ArrayList<JsonReadable>();
		}
		if (jl.size() == 0) {
			System.err.println("No Jsons found at file path: "+ fileConflict);
		}
	}

	@Override
	public void run() {		
		for (JsonReadable j : jl) {
			try {
				String line = prefix;
				String dat = j.get("created_utc");
				// for whatever reason, created_utc has quotes!
				long timestamp = Long.parseLong(dat.substring(1, dat.length() - 1));
				Date d = new Date(timestamp * 1000);
				c.setTime(d);
				int year = c.get(Calendar.YEAR);
				line += conflictdates.getDataForYear(ConflictExternality.Fatalities, year) + ",";
				line += conflictdates.getDataForYear(ConflictExternality.Refugees, year) + ",";
				line += conflictdates.getDataForYear(ConflictExternality.IDP, year) + ",";
				line += Integer.parseInt(j.get("score")) + ",";
				line += Integer.parseInt(j.get("controversiality")) + ",";
				line += sl.getOutput(j.get("body")) + ",";
				line += j.get("author") + ",";
				line += j.get("subreddit");
				synchronized (fw) {
					fw.write(line+System.getProperty("line.separator"));
					fw.flush();
				}
			}
			catch (NumberFormatException e) {
				System.err.println("Trouble parsing numbers from Json: "+ j.toString());
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		} 
		
	}
}
