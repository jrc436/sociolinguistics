package wordmap;

import util.Comment;

@FunctionalInterface
public interface InitWord {
	public Combinable initialValue(Comment data);
}
