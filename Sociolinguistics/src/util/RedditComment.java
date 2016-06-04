package util;

import java.time.Instant;

public class RedditComment extends Comment {
	public RedditComment(JsonReadable jr) {
		super(jr.get("body"), jr.get("author"), Instant.ofEpochSecond(Long.parseLong(jr.get("created_utc"))), jr);
	}
	
}
