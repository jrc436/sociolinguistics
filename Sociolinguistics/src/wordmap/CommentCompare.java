package wordmap;

import util.data.corpus.Comment;

@FunctionalInterface
public interface CommentCompare {
	public boolean inCorrectOrder(Comment first, Comment second);
}
