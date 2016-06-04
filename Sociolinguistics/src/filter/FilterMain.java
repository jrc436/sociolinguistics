package filter;

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
		f.filter(args[0], args[1]);
	}
}
