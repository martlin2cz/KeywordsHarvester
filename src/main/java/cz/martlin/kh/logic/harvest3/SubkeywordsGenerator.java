package cz.martlin.kh.logic.harvest3;

import java.util.Set;

import cz.martlin.kh.StuffProvider;
import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.harvest2.tree.ChildrenGenerator;
import cz.martlin.kh.logic.subkeyw.Subkeyworder;

public class SubkeywordsGenerator implements ChildrenGenerator {

	private final Subkeyworder subkeyworder;
	private final TreeHarvestProcessData data;

	public SubkeywordsGenerator(Config config, TreeHarvestProcessData data) {
		this.subkeyworder = StuffProvider.getSubkeyworder(config);
		this.data = data;
	}

	@Override
	public Set<String> generate(String keyword) {
		Set<String> subs = subkeyworder.subkeyword(keyword);
		subs = data.filterDone(subs);
		return subs;
	}

}
