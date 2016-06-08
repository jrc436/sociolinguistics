package filter;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import util.Logger;

public class FilterMain {
	public static void main(String args[]) {
		String errMessage = "Required parameters: Input File (Count-Format) and Output File (Count-Format)";
		if (args.length < 3) {
			System.out.println(errMessage);
			System.err.println("Please specify some filters:");
			System.err.println("Valid Filters:");
			System.err.println(Filter.getKnownFilters());
			System.exit(1);
		}
		String[] filters = new String[args.length-2];
		for (int i = 2; i < args.length; i++) {
			filters[i-2] = args[i];
		}
		Filter f = Filter.getFilter(filters);
		BlockingQueue<String> messages = new LinkedBlockingQueue<String>();
                Thread log;
                FileWriter fw = null;
                try {
                        fw = new FileWriter(args[1]);
                        log = new Thread(new Logger(messages, "log-filter.txt"));
                        log.setDaemon(true);
                        log.start();
                } catch (IOException e) {
                        System.err.println("Error initializing output. Check your output paths");
                        System.exit(1);
                }
	
		f.filter(args[0], args[1], messages);
	}
}
