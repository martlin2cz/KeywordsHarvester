package cz.martlin.kh.logic.harvest2;

import java.util.Collection;

import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.harvest2.tree.BFSLazyTree;
import cz.martlin.kh.logic.harvest2.tree.ChildrenGenerator;

/**
 * Lazy tree which generates child nodes as subkeywords.
 * 
 * @author martin
 * 
 */
public class RelatedKeywordsTree extends BFSLazyTree {
	private static final long serialVersionUID = 2059512343973943867L;

	private final TreeHarvestProcessData data;
	private final Config config;

	public RelatedKeywordsTree(Config config, Collection<String> initials,
			TreeHarvestProcessData data) {
		super(initials);
		this.config = config;
		this.data = data;
	}

	@Override
	public ChildrenGenerator getGenerator() {	//TODO OOOO FIXME EEE !!!!
		return new SubkeywordsGenerator(config, data);
	}

}
