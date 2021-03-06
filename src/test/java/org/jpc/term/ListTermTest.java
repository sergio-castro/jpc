package org.jpc.term;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ListTermTest {
	private Term empty = new Atom("[]");
	
	private Term nonEmpty = 
			new Compound(".", asList(new Atom("a"), 
				new Compound(".", asList(new Atom("b"), 
						new Compound(".", asList(new Atom("c"), new Atom("[]"))))))
				);

	private Term nonEmpty2 =
			new Compound(".", asList(new Atom("x"),
					new Compound(".", asList(new Atom("y"),
							new Compound(".", asList(new Atom("z"), new Atom("[]"))))))
			);

/*	private Term brokenList1 =
			new Compound(".", asList(new Atom("a"), 
				new Compound(".", asList(new Atom("b"), 
						new Compound(".", asList(new Atom("c"), new Atom("[]"), new Atom("[]"))))))
				);
	
	private Term brokenList2 = 
			new Compound(".", asList(new Atom("a"), 
				new Compound(".", asList(new Atom("b"), 
						new Compound(".", asList(new Atom("c"), new Atom("d"))))))
				);*/
	
	@Test
	public void testEquality() {
		Term nonEmptyClone = new Compound(".", asList(new Atom("a"), 
				new Compound(".", asList(new Atom("b"), 
						new Compound(".", asList(new Atom("c"), new Atom("[]"))))))
				);
		assertEquals(nonEmpty, nonEmptyClone);
		assertEquals(nonEmpty.hashCode(), nonEmptyClone.hashCode());
		assertFalse(nonEmpty.equals(nonEmpty2));
	}
	
	@Test
	public void testIsList() {
		assertTrue(empty.isList());
		assertTrue(nonEmpty.isList());
		/*assertFalse(brokenList1.isList());
		assertFalse(brokenList2.isList());*/
	}
	
	@Test
	public void testEmptyList() {
		assertEquals(empty.asList().size(), 0);
	}
	
	@Test
	public void testNonEmptyList() {
		ListTerm listTerm = nonEmpty.asList();
		assertEquals(listTerm.size(), 3);
		assertEquals(listTerm.get(0), new Atom("a"));
		assertEquals(listTerm.get(1), new Atom("b"));
		assertEquals(listTerm.get(2), new Atom("c"));
		assertEquals(listTerm.asTerm(), nonEmpty);
	}
	
	@Test
	public void testFromTermSequence() {
		Term sequence = new Compound(",", asList(new Atom("a"), new Compound(",", asList(new Atom("b"), new Atom("c")))));
		assertEquals(3, ListTerm.fromTermSequence(sequence).size());
	}

}
