package util;

import java.io.File;
import java.nio.file.Paths;

/**
 * The Reddit's default format is a list of Jsons, but JsonLayer requires a slightly different format. This changes the
 * format to what's required by JsonLayer.
 * @author jrc
 *
 */
public class JsonFormatter {
	public static void main(String[] args) {
		String flag = "-i";
		String helpMsg = "Two arguments are required, first, the input directory, then either the \""+flag+"\" flag or the output directory. The "+flag+" flag means to process in place, it should be followed by the string to append to the file names. Both directories should exist. ";
		if (args.length < 2 || args.length > 3) {
			System.err.println(helpMsg);
			System.exit(1);
		}
		File inpDirectory = Paths.get(args[0]).toFile();
		if (!inpDirectory.isDirectory()) {
			System.err.println(helpMsg);
			System.exit(1);
		}
		if (args[1].equals(flag)) {
			if (args[2].equals("")) {
				System.err.println(helpMsg);
				System.exit(1);
			}
			String append = args[2];
			System.out.println("Using: " + append + " as the append string");
			JsonLayer.processInPlace(Paths.get(args[0]), append);
		}
		else {
			File outDirectory = Paths.get(args[1]).toFile();
			if (!outDirectory.isDirectory()) {
				System.err.println(helpMsg);
				System.exit(1);
			}
			JsonLayer.processAndSplit(inpDirectory.toPath(), outDirectory.toPath());
		} 
	}
}
