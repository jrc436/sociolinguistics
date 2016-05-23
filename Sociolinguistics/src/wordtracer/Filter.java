package wordtracer;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import util.WordMap;

public class Filter {
	public static void main(String args[]) {
		String errMessage = "Required parameters: Input File (Count-Format) and Output File (Count-Format)";
		if (args.length != 2) {
			System.out.println(errMessage);
			System.exit(1);
		}
		FileInputStream fr = null;
		FileWriter fw = null;
		try {
			fr = new FileInputStream(args[0]);
			fw = new FileWriter(args[1]);
		}
		catch (IOException ie) {
			System.err.println("Error with input");
			ie.printStackTrace();
			System.exit(1);
		}
		Scanner scan = new Scanner(fr);
		WordMap wm = new WordMap();
		while (scan.hasNextLine()) {
			wm.addFromString(scan.nextLine());
		}
		//wm.cleanNonWords();
		//Set<Entry<String, Integer>> entries = wm.getEntrySet();
		System.out.println("Reading Complete");
		List<String> lines = wm.getStringLines();
		System.out.println("Sorting Complete");
		try {
			for (String line : lines) {
				fw.write(line+System.getProperty("line.separator"));
				fw.flush();
			}
			System.out.println("Writing Complete");
			fr.close();
			scan.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
