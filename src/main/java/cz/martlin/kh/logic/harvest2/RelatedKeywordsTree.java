package cz.martlin.kh.logic.harvest2;

import java.util.Collection;

import cz.martlin.kh.StuffProvider;
import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.harvest2.tree.LazyTree;
import cz.martlin.kh.logic.subkeyw.Subkeyworder;

/**
 * Lazy tree which generates child nodes as subkeywords.
 * @author martin
 *
 */
public class RelatedKeywordsTree extends LazyTree {
	private static final long serialVersionUID = 2059512343973943867L;

	private final Config config;

	public RelatedKeywordsTree(Config config, Collection<String> initials) {
		super(initials);
		this.config = config;
	}

	@Override
	public Iterable<String> createChildrenOf(String label) {
		// TODO some better solution?
		Subkeyworder subkeyworder = StuffProvider.getSubkeyworder(config);
		return subkeyworder.subkeyword(label);
	}

}
