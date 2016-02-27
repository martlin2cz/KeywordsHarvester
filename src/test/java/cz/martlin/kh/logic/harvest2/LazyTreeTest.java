package cz.martlin.kh.logic.harvest2;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import cz.martlin.kh.logic.harvest2.tree.BFSLazyTree;
import cz.martlin.kh.logic.harvest2.tree.BFSLazyTreeIterator;
import cz.martlin.kh.logic.harvest2.tree.ChildrenGenerator;

public class LazyTreeTest {

	public class EnumeratingTree extends BFSLazyTree {
		private static final long serialVersionUID = 1183744971996253844L;

		public EnumeratingTree(List<String> initials) {
			super(initials);
		}

		@Override
		public ChildrenGenerator getGenerator() {
			return new ChildrenGenerator() {

				@Override
				public Set<String> generate(String label) {
					System.out.println("creating children of: " + label);
					int count = label.length();

					Set<String> children = new LinkedHashSet<>(count);

					for (int i = 0; i < count; i++) {
						String child = label + i;
						children.add(child);
					}

					return children;
				}
			};
		}

	}

	@Test
	public void testIterator() {

		// create tree
		List<String> initials = Arrays.asList(new String[] { "x", "foo",
				"Lorem" });
		BFSLazyTree t = new EnumeratingTree(initials);

		BFSLazyTreeIterator iter = (BFSLazyTreeIterator) t.iterator();
		assertEquals("x", iter.next());
		assertEquals("foo", iter.next());
		assertEquals("Lorem", iter.next());

		assertEquals("x0", iter.next());
		assertEquals("foo0", iter.next());
		assertEquals("foo1", iter.next());
		assertEquals("foo2", iter.next());
		assertEquals("Lorem0", iter.next());
		assertEquals("Lorem1", iter.next());
		assertEquals("Lorem2", iter.next());
		assertEquals("Lorem3", iter.next());
		assertEquals("Lorem4", iter.next());

		assertEquals("x00", iter.next());
		assertEquals("x01", iter.next());
		assertEquals("foo00", iter.next());
		assertEquals("foo01", iter.next());

		iter.next(9);
		// foo02, foo03, foo10, foo11, foo12, foo13, foo20, foo21, foo22,

		assertEquals("foo23", iter.next());
		assertEquals("Lorem00", iter.next());

		// ...
	}

}
