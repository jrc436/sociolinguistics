package counterfilter;

public class Filter {
	public static void main(String args[]) {
		 String errMessage = "Required parameters: Input File (Count-Format) and Output File (Count-Format)";
	        if (args.length != 2) {
	                System.out.println(errMessage);
	                System.exit(1);
	        }
	}
}
