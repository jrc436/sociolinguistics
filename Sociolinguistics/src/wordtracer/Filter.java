package wordtracer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class Filter {
	public void filter(String fileIn, String fileOut) {
		FileInputStream fr = null;
		FileWriter fw = null;
		try {
			fr = new FileInputStream(fileIn);
			fw = new FileWriter(fileOut);
		}
		catch (IOException ie) {
			System.err.println("Error with input");
			ie.printStackTrace();
			System.exit(1);
		}
		Scanner scan = new Scanner(fr);
		filterCritical(scan, fw);
	}
	protected abstract void filterCritical(Scanner s, FileWriter fw);
	private static Filter getFilterByName(String name) {
		return FilterEnum.instantiate(name);
	}
	public static Filter getFilter(String[] filters) {
		if (filters.length == 1) {
			return getFilterByName(filters[0]);
		}
		List<Filter> cat = new ArrayList<Filter>();
		for (String s : filters) {
			Filter f = getFilterByName(s);
			if (f != null) {
				cat.add(f);
			}
		}
		return new Filter() {
			@Override
			public void filterCritical(Scanner s, FileWriter fw) {
				//String curFileIn = fileIn;
				//String curFileOut = fileOut;
				FileWriter curFileWriter = null;
				File tmpFile = null;
				try {
					for (int i = 0; i < cat.size(); i++) {
						if (i != cat.size()-1) {
							//ok need to write to an intermediary fileout
							tmpFile = File.createTempFile("filter"+i, ".tmp");
							curFileWriter = new FileWriter(tmpFile);
						}
						else {
							//okay, time to write to the final fileout
							curFileWriter = fw;
						}
						cat.get(i).filterCritical(s, curFileWriter);
						s = new Scanner(new FileInputStream(tmpFile));
					}
				}
				catch (IOException ie) {
					System.err.println("Error in chaining filters. This is a bug that shouldn't ever happen.");
					ie.printStackTrace();
					System.exit(1);
				}
			}
		};
	}
	public static String getKnownFilters() {
		String toReturn = "";
		for (FilterEnum fe : FilterEnum.values()) {
			toReturn += fe.toString() + "; ";
		}
		return toReturn + " Any fully qualified class name extending wordtracer.Filter";
	}
	enum FilterEnum {
		lex,
		freq;
		public String toString() {
			switch (this) {
				case freq:
					return "Frequency";
				case lex:
					return "Lexical";	
				default:
					return "NULL";		
			}
		}
		static FilterEnum getEnumFromName(String name) {
			for (FilterEnum fe : FilterEnum.values()) {
				if (name.equals(fe.toString())) {
					return fe;
				}
			}
			return null;
		}
		static Filter instantiate(String name) {
			Filter f = getEnumFromName(name).instantiate();
			if (f != null) {
				return f;
			}
			try {
				Class<?> cls = Class.forName(name);
				if (cls.isAssignableFrom(Filter.class)) {
					System.err.println(name + " is a class, but is not a Filter");
			//		System.exit(1);
				}
				Class<? extends Filter> clsl = cls.asSubclass(Filter.class);
				return clsl.newInstance();
			}
			catch (ClassNotFoundException c) {
				System.err.println(name+" does not refer to a loaded class");
				c.printStackTrace();
			//	System.exit(1);
			} catch (InstantiationException e) {
				e.printStackTrace();
				System.err.println("Error creating instance of Filter: "+name+". Perhaps no default constructor is provided?");
			//	System.exit(1);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				System.err.println("Error creating instance of Filter: "+name+". Perhaps no public or protected constructor is provided?");
			//	System.exit(1);
			}
			return null;
		}
		Filter instantiate() {
			switch (this) {
				case freq:
					return new FreqFilter();
				case lex:
					return new LexFilter();
				default:
					return null;
				}
		}
	}
}
