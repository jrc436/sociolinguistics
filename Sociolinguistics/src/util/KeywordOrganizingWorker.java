package util;

import java.util.ArrayList;
import java.util.List;

public class KeywordOrganizingWorker implements Runnable {
	private final JsonLayer jl;
	private final int threadNum;
	private final List<KeywordOrganizer> copyOfComments;
	public KeywordOrganizingWorker(int threadNum, JsonLayer jl, List<KeywordOrganizer> copyOfComments) {
		this.jl = jl;
		this.copyOfComments = new ArrayList<KeywordOrganizer>(copyOfComments);
		this.threadNum = threadNum;
	}
	@Override
	public void run() {
		List<JsonReadable> comments = jl.getNextReadable();
		while (comments != null) {
			System.out.println("Thread"+threadNum+" has acquired another set of comments");
			System.out.println("There are "+jl.numReadableRemaining()+" files remaining");
			for (JsonReadable comment : comments) {
				for (KeywordOrganizer c : copyOfComments) {
					if (c.relevant(comment.get("body"))) {
						c.writeComment(comment.toString());
					}
				}
			}
			comments = jl.getNextReadable();
		}
		System.out.println("Thread"+threadNum+ " will close now, as there are no more files to process");
	}
}