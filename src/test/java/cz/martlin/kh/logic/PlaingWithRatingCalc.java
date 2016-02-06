package cz.martlin.kh.logic;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cz.martlin.kh.logic.exception.NetworkException;
import cz.martlin.kh.logic.picwf.PicworkflowQuery;
import cz.martlin.kh.logic.picwf.PicworkflowWrapper;

public class PlaingWithRatingCalc {
	private static final Config CONFIG = new Config();

	public static void main(String[] args) {
		System.out.println("Runnin':");

		doTest1();

		System.out.println("Done.");
	}

	private static void doTest1() {
		Set<String> keywords = new HashSet<>();
		keywords.add("unfocused"); // 6.9912
		keywords.add("engineers"); // 67.29
		keywords.add("david cameron"); // 320.111
		keywords.add("Final Piece of the Jigsaw"); // 701.366
		keywords.add("Diving Into Water"); // 1.364
		// keywords.add(""); //

		keywords.addAll(Testing.testKeywords1());
		keywords.addAll(Testing.testKeywords2());
		// keywords.addAll(Testing.testKeywords3());

		queryAndTabelize(keywords);
	}

	private static void queryAndTabelize(Set<String> keywords) {
		PicworkflowWrapper picw = new PicworkflowWrapper(CONFIG);
		try {
			picw.initialize();
			PicworkflowQuery query = picw.createQuerry(keywords);
			Set<Keyword> result = query.runQuery();
			picw.finish();

			double max = tableCompletelly(result, 0.05);	//TODO tady si vybrat vhodnou delta
			System.out.println("Max relative error found: " + max);
		} catch (NetworkException e) {
			System.err.println(e);
			return;
		}

	}

	private static double tableCompletelly(Set<Keyword> keywords,
			double maxRelEpsilon) {

		double maxRelFound = 0.0;
		for (Keyword keyword : keywords) {
			Map<String, Double> fields = keywordToFields(keyword);

			System.out.printf("%30s | %8.2f | ", //
					keyword.getKeyword(), keyword.getRating());

			for (Entry<String, Double> left : fields.entrySet()) {
				for (Entry<String, Double> right : fields.entrySet()) {
					if (left.getKey().equals(right.getKey())) {
						continue; // zbytečný, žejo ..
					}

					Map<String, Entry<Double, Double>> exprs = calculateExprs(
							keyword, left, right);

					for (Entry<String, Entry<Double, Double>> expr : exprs
							.entrySet()) {
						String line;
						double val = expr.getValue().getKey();
						double rel = expr.getValue().getValue();

						if (rel < maxRelEpsilon) {
							line = String.format(expr.getKey()
									+ ": %1$12.2f (%2$4.3f)",//
									val, rel);
							if (rel > maxRelFound) {
								maxRelFound = rel;
							}
						} else {
							line = String.format("%1$29s", "--");
						}

						System.out.print(line + " | ");
					}
				}
			}
			System.out.println();
		}
		return maxRelFound;
	}

	private static Map<String, Entry<Double, Double>> calculateExprs(
			Keyword keyword, Entry<String, Double> left,
			Entry<String, Double> right) {

		double mulVal = left.getValue() * right.getValue();
		double div1Val = left.getValue() / right.getValue();
		double div2Val = right.getValue() / left.getValue();

		double mulRel = Math.abs((keyword.getRating() - mulVal) / mulVal);
		double div1Rel = Math.abs((keyword.getRating() - div1Val) / div1Val);
		double div2Rel = Math.abs((keyword.getRating() - div2Val) / div2Val);

		Entry<Double, Double> mul = new AbstractMap.SimpleImmutableEntry<>(
				mulVal, mulRel);
		Entry<Double, Double> div1 = new AbstractMap.SimpleImmutableEntry<>(
				div1Val, div1Rel);
		Entry<Double, Double> div2 = new AbstractMap.SimpleImmutableEntry<>(
				div2Val, div2Rel);

		String mulName = String.format("<%1$2s*%2$2s>", left.getKey(),
				right.getKey());
		String div1Name = String.format("<%1$2s/%2$2s>", left.getKey(),
				right.getKey());
		String div2Name = String.format("<%2$2s/%1$2s>", left.getKey(),
				right.getKey());

		Map<String, Entry<Double, Double>> result = new HashMap<>();
		result.put(mulName, mul);
		result.put(div1Name, div1);
		result.put(div2Name, div2);

		return result;
	}

	private static Map<String, Double> keywordToFields(Keyword keyword) {
		Map<String, Double> result = new HashMap<>();

		result.put("Df", (double) keyword.getDownloadsPerFile());
		result.put("Vf", (double) keyword.getViewsPerFile());
		result.put("C", (double) keyword.getCount());
		result.put("D", (double) keyword.getDownloads());
		result.put("L", (double) keyword.getLang());
		result.put("V", (double) keyword.getViews());

		return result;
	}

	private static void tableIt(Set<Keyword> keywords) {
		for (Keyword keyword : keywords) {
			System.out
					.printf("%30s | %12.2f | %12.2f | \n", //
							keyword.getKeyword(), //
							keyword.getRating(),//
							1000.0 * keyword.getDownloadsPerFile()
									/ keyword.getCount(),

							null);
		}
	}
}
