package org.jpc.mapping.converter;

import static java.util.Arrays.asList;
import static org.jpc.engine.prolog.PrologConstants.FAIL;
import static org.jpc.engine.prolog.PrologConstants.FALSE;
import static org.jpc.engine.prolog.PrologConstants.TRUE;
import static org.jpc.mapping.converter.catalog.util.OptionalConverter.OPTIONAL_FUNCTOR_NAME;
import static org.jpc.mapping.converter.catalog.util.OptionalConverter.PRESENT_OPTIONAL_VALUE_WRAPPER;
import static org.jpc.mapping.converter.catalog.util.UuidConverter.UUID_FUNCTOR_NAME;
import static org.jpc.term.Atom.atom;
import static org.jpc.term.JRef.jRef;
import static org.jpc.term.ListTerm.listTerm;
import static org.jpc.term.TermConstants.EMPTY_OPTIONAL;
import static org.jpc.term.TermConstants.JAVA_NULL;
import static org.jpc.term.Var.dontCare;
import static org.jpc.term.Var.var;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.jpc.Jpc;
import org.jpc.JpcBuilder;
import org.jpc.mapping.typesolver.catalog.MapTypeSolver;
import org.jpc.term.Atom;
import org.jpc.term.Compound;
import org.jpc.term.Float;
import org.jpc.term.Integer;
import org.jpc.term.JRef;
import org.jpc.term.Term;
import org.jpc.term.TermConstants;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.reflect.TypeToken;

public class DefaultConversionsTest {

	private Jpc jpc = JpcBuilder.create().build();

	@Test
	public void testNonEmptyOptionalConversion() {
		Optional<String> opt = Optional.of("x");
		Term optionalTerm = jpc.toTerm(opt);
		assertEquals(new Compound(OPTIONAL_FUNCTOR_NAME, asList(
				new Compound(PRESENT_OPTIONAL_VALUE_WRAPPER, asList(atom("x"))))), optionalTerm);
		assertEquals(opt, jpc.fromTerm(optionalTerm));
	}

	@Test
	public void testEmptyOptionalConversion() {
		Optional<String> opt = Optional.empty();
		Term optionalTerm = jpc.toTerm(opt);
		assertEquals(EMPTY_OPTIONAL, optionalTerm);
		assertEquals(opt, jpc.fromTerm(optionalTerm));
	}

	@Test
	public void testUuidConverter() {
		UUID uuid = UUID.randomUUID();
		Term uuidTerm = jpc.toTerm(uuid);
		assertEquals(new Compound(UUID_FUNCTOR_NAME, asList(atom(uuid.toString()))), uuidTerm);
		assertEquals(uuid, jpc.fromTerm(uuidTerm));
	}


	// TODO restructure all these tests in a Spock specification
	// *** OBJECT TO TERM TESTS ***
	
	@Test
	public void testNullToTerm() {
		assertTrue(JAVA_NULL.termEquals(jpc.toTerm(null)));
	}



	@Test
	public void testTermToTerm() {
		assertEquals(new Atom("x"), jpc.toTerm(new Atom("x")));
	}
	
	@Test
	public void testBooleanToTerm() {
		assertEquals(TermConstants.TRUE, jpc.toTerm(true));
		assertEquals(TermConstants.FALSE, jpc.toTerm(false));
	}
	
	@Test
	public void testStringToTerm() {
		assertEquals(new Atom("apple"), jpc.toTerm("apple"));
		assertEquals(new Atom("1"), jpc.toTerm("1"));
		assertEquals(new Atom("1"), jpc.toTerm("1", Atom.class));
		assertEquals(new Integer(1), jpc.toTerm("1", Integer.class));
		assertEquals(new Float(1), jpc.toTerm("1", Float.class));
	}
	
	@Test
	public void testCharToTerm() {
		char a = 'a';
		assertEquals(new Atom("a"), jpc.toTerm(a));
	}
	
	@Test
	public void testNumberToIntegerTerm() {
		byte aByte = 10;
		short aShort = 10;
		int anInt = 10;
		long aLong = 10;
		AtomicInteger ai = new AtomicInteger(10);
		AtomicLong al = new AtomicLong(10);
		BigInteger bi = BigInteger.TEN;
		assertEquals(new Integer(10), jpc.toTerm(aByte));
		assertEquals(new Integer(10), jpc.toTerm(aShort));
		assertEquals(new Integer(10), jpc.toTerm(anInt));
		assertEquals(new Integer(10), jpc.toTerm(aLong));
		assertEquals(new Integer(10), jpc.toTerm(ai));
		assertEquals(new Integer(10), jpc.toTerm(al));
		assertEquals(new Integer(10), jpc.toTerm(bi));
	}
	
	@Test
	public void testNumberToFloatTerm() {
		float aFloat = 10.5f;
		double aDouble = 10.5;
		BigDecimal bd = new BigDecimal(10.5);
		assertEquals(new Float(10), jpc.toTerm(10f));
		assertEquals(new Float(10.5), jpc.toTerm(aFloat));
		assertEquals(new Float(10.5), jpc.toTerm(aDouble));
		assertEquals(new Float(10.5), jpc.toTerm(bd));
	}
	
	@Test
	public void testNumberToAtom() {
		assertEquals(new Atom("1"), jpc.toTerm(1, Atom.class));
		assertEquals(new Atom("1.0"), jpc.toTerm(1D, Atom.class));
	}
	
	@Test
	public void testEntryToTerm() {
		Map.Entry<String, java.lang.Integer> entry = new AbstractMap.SimpleEntry<>("apple", 10);
		assertEquals(new Compound(MapTypeSolver.DEFAULT_MAP_ENTRY_SEPARATOR, asList(new Atom("apple"), new Integer(10))), jpc.toTerm(entry));
	}
	
	@Test
	public void testMapToTerm() {
		Map<String, java.lang.Integer> map = new LinkedHashMap<String, java.lang.Integer>() {{ //LinkedHashMap to preserve insertion order
			put("apple", 10);
			put("orange", 20);
		}};
		Term mapTerm = jpc.toTerm(map);
		List<Term> listTerm = mapTerm.asList();
		assertEquals(2, listTerm.size());
		assertEquals(new Compound(MapTypeSolver.DEFAULT_MAP_ENTRY_SEPARATOR, asList(new Atom("apple"), new Integer(10))), listTerm.get(0));
		assertEquals(new Compound(MapTypeSolver.DEFAULT_MAP_ENTRY_SEPARATOR, asList(new Atom("orange"), new Integer(20))), listTerm.get(1));
	}
	
	@Test
	public void testEmptyArrayToTerm() {
		assertEquals(jpc.toTerm(new Object[]{}), new Atom("[]"));
	}
	
	@Test
	public void testArrayToTerm() {
		Term nonEmptyArray = jpc.toTerm(new Object[]{"apple", 10});
		assertEquals(
				new Compound(".", asList(new Atom("apple"), 
					new Compound(".", asList(new Integer(10),
						new Atom("[]")))))
		, nonEmptyArray);
	}
	
	@Test
	public void testTableToTerm() {
		Term table = jpc.toTerm(
			new Object[][]{
				new Object[]{"apple", 10},
				new Object[]{"pears", 10.5}
			});
		assertEquals(new Compound(".", asList(
				new Compound(".", asList(new Atom("apple"), 
					new Compound(".", asList(new Integer(10),
						new Atom("[]"))))), 
				new Compound(".", asList(
					new Compound(".", asList(new Atom("pears"), 
						new Compound(".", asList(new Float(10.5),
							new Atom("[]"))))), 
				new Atom("[]"))))), table);
	}
	
	@Test
	public void testListToTerm() {
		Term nonEmptyList = jpc.toTerm(asList("apple", 10));
		assertEquals(new Compound(".", asList(new Atom("apple"), 
				new Compound(".", asList(new Integer(10),
				new Atom("[]"))))), nonEmptyList);
		assertEquals(jpc.toTerm(new ArrayList()), new Atom("[]"));
	}

	@Test
	public void testEnumerationToTerm() {
		Enumeration enumeration = Collections.enumeration(new ArrayList() {{ 
			add("apple");
			add(10);
		}});
		Term nonEmptyList = jpc.toTerm(enumeration);
		assertEquals(new Compound(".", asList(new Atom("apple"), 
				new Compound(".", asList(new Integer(10),
				new Atom("[]"))))), nonEmptyList);
		assertEquals(jpc.toTerm(new ArrayList()), new Atom("[]"));
	}
	
	
	
	// *** TERM TO OBJECTS TESTS ***

	@Test
	public void testJRefToObject() {
		Object o = new Object();
		JRef jref = jRef(o);
		assertEquals(o, jpc.fromTerm(jref));
	}
	
	@Test
	public void testTermSpecifier() {
		assertEquals(new Atom("a"), jpc.fromTerm(new Compound("term", asList(new Atom("a")))));
	}
	
	@Test
	public void testTermFromTerm() {
		assertEquals(new Atom("apple"), jpc.fromTerm(new Atom("apple"), Term.class));
	}
	
	@Test
	public void testVariableConversion() {
		assertEquals(var("X"), jpc.fromTerm(var("X")));
		assertTrue(dontCare().termEquals(jpc.fromTerm(dontCare())));
	}
	
	@Test
	public void testTermToString() {
		assertEquals("apple", jpc.fromTerm(new Atom("apple")));
		assertFalse("true".equals(jpc.fromTerm(new Atom(TRUE))));
		assertTrue("true".equals(jpc.fromTerm(new Atom(TRUE), String.class)));
		assertEquals("123", jpc.fromTerm(new Atom("123")));
		assertEquals("123", jpc.fromTerm(new Integer(123), String.class));
	}
	
	@Test
	public void testTermToChar() {
		assertEquals(new Character('a'), jpc.fromTerm(new Atom("a"), Character.class));
		assertEquals(new Character('a'), jpc.fromTerm(new Atom("a"), char.class));
		assertEquals(new Character('1'), jpc.fromTerm(new Atom("1"), Character.class));
		assertEquals(new Character('1'), jpc.fromTerm(new Atom("1"), char.class));
		try {
			jpc.fromTerm(new Atom("ab"), Character.class);
			fail();
		} catch(Exception e) {}
		
	}
	
	@Test
	public void testTermToBoolean() {
		assertEquals(true, jpc.fromTerm(new Atom(TRUE)));
		assertEquals(false, jpc.fromTerm(new Atom(FAIL)));
		assertEquals(false, jpc.fromTerm(new Atom(FALSE)));
		assertEquals(true, jpc.fromTerm(new Atom(TRUE), Boolean.class));
		assertEquals(true, jpc.fromTerm(new Atom(TRUE), boolean.class));
		assertEquals(false, jpc.fromTerm(new Atom(FAIL), Boolean.class));
		assertEquals(false, jpc.fromTerm(new Atom(FAIL), boolean.class));
		assertEquals(false, jpc.fromTerm(new Atom(FALSE), Boolean.class));
		assertEquals(false, jpc.fromTerm(new Atom(FALSE), boolean.class));
		assertEquals(true, jpc.fromTerm(new Atom(TRUE), Object.class));
		assertEquals(false, jpc.fromTerm(new Atom(FAIL), Object.class));
		assertEquals(false, jpc.fromTerm(new Atom(FALSE), Object.class));
	}
	
	@Test
	public void testTermToInt() {
		assertTrue(jpc.fromTerm(new Integer(10L)).equals(10L));
		assertFalse(jpc.fromTerm(new Integer(10)).equals(10)); //values in Prolog Integer terms are stored as a Java Long
		assertEquals(new Long(10L), jpc.fromTerm(new Integer(10)));
		assertEquals(new java.lang.Integer(10), jpc.fromTerm(new Integer(10), java.lang.Integer.class));
		assertEquals(new java.lang.Integer(10), jpc.fromTerm(new Integer(10), int.class));
		assertEquals(new java.lang.Integer(10), jpc.fromTerm(new Atom("10"), java.lang.Integer.class));
		assertEquals(new java.lang.Integer(10), jpc.fromTerm(new Atom("10"), int.class));
		try{
			jpc.fromTerm(new Atom(TRUE), java.lang.Integer.class);
			fail();
		} catch(Exception e){}
	}
	
	@Test
	public void testTermToDouble() {
		assertTrue(jpc.fromTerm(new Float(1D)).equals(1D));
		assertFalse(jpc.fromTerm(new Float(1F)).equals(1F)); //values in Prolog Float terms are stored as a Java Double
		assertTrue(jpc.fromTerm(new Float(1F)).equals(1D));
		assertEquals(new Double(10.5), jpc.fromTerm(new Float(10.5)));
	}

	@Test
	public void testTermToEntry() {
		Map.Entry<String, Long> entry = new AbstractMap.SimpleEntry<>("apple", 10L);
		Term entryTerm = new Compound("=", asList(new Atom("apple"), new Integer(10)));
		assertEquals(entry, jpc.fromTerm(entryTerm));
		entryTerm = new Compound("-", asList(new Atom("apple"), new Integer(10)));
		assertEquals(entry, jpc.fromTerm(entryTerm));
		entryTerm = new Compound("$", asList(new Atom("apple"), new Integer(10)));
		try {
			assertEquals(entry, jpc.fromTerm(entryTerm, Entry.class));
			fail();
		} catch(Exception e){}
	}

	@Test
	public void testTermToMap() {
		Compound c1 = new Compound("-", asList(new Atom("apple"), new Integer(10)));
		Compound c2 = new Compound("-", asList(new Atom("orange"), new Integer(20)));
		Term listTerm = listTerm(c1, c2);
		Map map = jpc.fromTerm(listTerm);
		assertEquals(2, map.size());
		assertEquals(map.get("apple"), 10L);
		assertEquals(map.get("orange"), 20L);
	}
	
	@Test
	public void testTermToMap2() {
		Compound c1 = new Compound("=", asList(new Atom("apple"), new Integer(10)));
		Compound c2 = new Compound("=", asList(new Atom("orange"), new Integer(20)));
		Term listTerm = listTerm(c1, c2);
		Map map = (Map) jpc.fromTerm(listTerm);
		assertEquals(2, map.size());
		assertEquals(map.get("apple"), 10L);
		assertEquals(map.get("orange"), 20L);
	}
	
	@Test
	public void testTermToMap3() {
		Compound c1 = new Compound("#", asList(new Atom("apple"), new Integer(10))); //the symbol # is not a valid entry separator
		Compound c2 = new Compound("#", asList(new Atom("orange"), new Integer(20)));
		Term listTerm = listTerm(c1, c2);
		try {
			jpc.fromTerm(listTerm);
			fail();
		} catch(Exception e) {}
	}
	
	@Test
	public void testTermToEmptyList() {
		List<?> list = jpc.fromTerm(TermConstants.NIL);
		assertEquals(0, list.size());
	}
	
	@Test
	public void testTermToList() {
		Term listTerm = listTerm(new Atom("apple"), JAVA_NULL);
		List list = (List) jpc.fromTerm(listTerm);
		assertEquals(2, list.size());
		assertEquals(list.get(0), "apple");
		assertEquals(list.get(1), null);
	}
	
	@Test
	public void testTermToList2() {
		Compound c1 = new Compound("-", asList(new Atom("apple"), new Integer(10)));
		Compound c2 = new Compound("-", asList(new Atom("orange"), new Integer(20)));
		Term listTerm = listTerm(c1, c2);
		List list = jpc.fromTerm(listTerm, List.class);
		assertEquals(2, list.size());
		
		assertEquals("apple", ((Entry)list.get(0)).getKey());
		assertEquals(10L, ((Entry)list.get(0)).getValue());
		assertEquals("orange", ((Entry)list.get(1)).getKey());
		assertEquals(20L, ((Entry)list.get(1)).getValue());
	}
	
	@Test
	public void testTermToGenericList() {
		Term listTerm = listTerm(new Atom("1"), new Atom("2")); //['1','2']
		List list = jpc.fromTerm(listTerm); //no type specified
		assertEquals(list.get(0), "1");
		assertEquals(list.get(1), "2");
		Type type = new TypeToken<List<String>>(){}.getType();
		list = jpc.fromTerm(listTerm, type); //redundant specification of the type
		assertEquals(list.get(0), "1");
		assertEquals(list.get(1), "2");
		type = new TypeToken<List<java.lang.Integer>>(){}.getType();
		list = jpc.fromTerm(listTerm, type); //indicating that the elements of the list should be integers
		assertEquals(list.get(0), 1);
		assertEquals(list.get(1), 2);
		type = new TypeToken<List<Long>>(){}.getType();
		list = jpc.fromTerm(listTerm, type); //indicating that the elements of the list should be longs
		assertEquals(list.get(0), 1L);
		assertEquals(list.get(1), 2L);
	}
	
	@Test
	public void testTermToObjectArray() {
		Term listTerm = listTerm(new Atom("apple"), JAVA_NULL);
		Object[] array = jpc.fromTerm(listTerm, Object[].class);
		assertEquals(2, array.length);
		assertEquals(array[0], "apple");
		assertEquals(array[1], null);
	}
	
	@Test
	public void testTermToStringArray() {
		Term listTerm = listTerm(new Atom("apple"), JAVA_NULL);
		String[] array = jpc.fromTerm(listTerm, String[].class);
		assertEquals(2, array.length);
		assertEquals(array[0], "apple");
		assertEquals(array[1], null);
	}
	
	@Test
	public void testTermToObjectTable() {
		Term term = new Compound(".", asList(
				new Compound(".", asList(new Atom("apple"), 
						new Compound(".", asList(JAVA_NULL,
							new Atom("[]"))))), 
					new Compound(".", asList(
						new Compound(".", asList(new Atom("pears"), 
							new Compound(".", asList(JAVA_NULL,
								new Atom("[]"))))), 
					new Atom("[]")))));
		Object[][] table = jpc.fromTerm(term, Object[][].class);
		Assert.assertArrayEquals(table, new String[][]{new String[]{"apple", null}, new String[]{"pears", null}});
	}

	@Test
	public void testTermToStringTable() {
		Term term = new Compound(".", asList(
				new Compound(".", asList(new Atom("apple"), 
						new Compound(".", asList(JAVA_NULL,
							new Atom("[]"))))), 
					new Compound(".", asList(
						new Compound(".", asList(new Atom("pears"), 
							new Compound(".", asList(JAVA_NULL,
								new Atom("[]"))))), 
					new Atom("[]")))));
		String[][] table = jpc.fromTerm(term, String[][].class);
		Assert.assertArrayEquals(table, new String[][]{new String[]{"apple", null}, new String[]{"pears", null}});
	}

	@Test
	public void testTermToListOfStringArray() {
		Term term = new Compound(".", asList(
				new Compound(".", asList(atom("apple"),
						new Compound(".", asList(JAVA_NULL,
							new Atom("[]"))))), 
					new Compound(".", asList(
						new Compound(".", asList(atom("pears"),
							new Compound(".", asList(JAVA_NULL,
								new Atom("[]"))))), 
					new Atom("[]")))));
		Type type = new TypeToken<List<String[]>>(){}.getType();
		List<String[]> list = jpc.fromTerm(term, type);
		Assert.assertArrayEquals(list.get(0), new String[]{"apple", null});
		Assert.assertArrayEquals(list.get(1), new String[]{"pears", null});
	}
	
}
