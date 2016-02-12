package cz.martlin.kh.logic.harvest2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import cz.martlin.kh.logic.harvest2.tree.LazyTree;
import cz.martlin.kh.logic.harvest2.tree.LazyTreeIterator;

public class LazyTreeTest {

	public class EnumeratingTree extends LazyTree {
		private static final long serialVersionUID = 1183744971996253844L;

		public EnumeratingTree(List<String> initials) {
			super(initials);
		}

		@Override
		public Iterable<String> createChildrenOf(String label) {
			System.out.println("creating children of: " + label);
			int count = label.length();

			List<String> children = new ArrayList<>(count);

			for (int i = 0; i < count; i++) {
				String child = label + i;
				children.add(child);
			}

			return children;
		}

	}

	@Test
	public void testTree() {

		// create tree
		List<String> initials = Arrays.asList(new String[] { //
				"XXX", "x", "foo", "Lorem", "mama" });
		LazyTree t = new EnumeratingTree(initials);

		// test sizes of children
		assertEquals(1, t.getChildren("x").size());
		assertEquals(3, t.getChildren("foo").size());
		assertEquals(5, t.getChildren("Lorem").size());
		assertEquals(4, t.getChildren("mama").size());

		// test some basic cotainment of children
		assertTrue(t.getChildren("x").contains("x0"));
		assertFalse(t.getChildren("x").contains("x1"));
		assertTrue(t.getChildren("foo").contains("foo0"));
		assertTrue(t.getChildren("foo").contains("foo1"));
		assertTrue(t.getChildren("foo").contains("foo2"));
		assertFalse(t.getChildren("foo").contains("foo3"));

		// now - to next layer!
		t.toNextLayer();
		// test sizes of children
		assertEquals(2, t.getChildren("x0").size());
		assertEquals(4, t.getChildren("foo0").size());
		assertEquals(4, t.getChildren("foo1").size());
		assertEquals(4, t.getChildren("foo2").size());
		assertEquals(6, t.getChildren("Lorem3").size());

		// test some basic cotainment of children
		assertTrue(t.getChildren("x0").contains("x00"));
		assertTrue(t.getChildren("x0").contains("x01"));
		assertFalse(t.getChildren("x0").contains("x02"));
		assertTrue(t.getChildren("foo0").contains("foo00"));
		assertTrue(t.getChildren("foo0").contains("foo01"));
		assertTrue(t.getChildren("foo1").contains("foo10"));
		assertTrue(t.getChildren("foo1").contains("foo11"));
		assertTrue(t.getChildren("foo1").contains("foo12"));

	}

	@Test
	public void testIterator() {
/*
		// create tree
		List<String> initials = Arrays.asList(new String[] { //
				 "X", "foo", "Lorem" });
		LazyTree t = new EnumeratingTree(initials);

		LazyTreeIterator iter = (LazyTreeIterator) t.iterator();
		assertEquals("X", iter.next());
		assertEquals(null, iter.next());
		
		assertEquals("X0", iter.next());
		assertEquals("foo", iter.next());
		assertEquals("Lorem", iter.next());
		assertEquals(null, iter.next());
		

		assertEquals("X00", iter.next());
		assertEquals("X01", iter.next());
		assertEquals("foo0", iter.next());
		assertEquals("foo1", iter.next());
		assertEquals("foo2", iter.next());
		assertEquals("Lorem0", iter.next());
		assertEquals("Lorem1", iter.next());
		assertEquals("Lorem2", iter.next());
		assertEquals("Lorem3", iter.next());
		assertEquals("Lorem4", iter.next());
		assertEquals(null, iter.next());
		
		assertEquals("X000", iter.next());
		assertEquals("X001", iter.next());
		assertEquals("X002", iter.next());
		assertEquals("X010", iter.next());
		assertEquals("X011", iter.next());
		assertEquals("X012", iter.next());
		assertEquals("foo00", iter.next());
		assertEquals("foo01", iter.next());
		// ...
*/
	}

}
