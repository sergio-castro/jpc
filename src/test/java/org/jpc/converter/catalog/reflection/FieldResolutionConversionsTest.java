package org.jpc.converter.catalog.reflection;

import static java.util.Arrays.asList;
import static org.jpc.term.JRef.jRef;
import static org.junit.Assert.assertEquals;

import java.util.AbstractMap;
import java.util.HashMap;

import org.jpc.Jpc;
import org.jpc.JpcBuilder;
import org.jpc.converter.catalog.reflection.ReificationFixture.B;
import org.jpc.term.Atom;
import org.jpc.term.Compound;
import org.jpc.term.Term;
import org.junit.Test;
import org.minitoolbox.reflection.StaticClass;

public class FieldResolutionConversionsTest {
	
	private static Jpc jpc = JpcBuilder.create().build();
	
	@Test
	public void testStaticField() {
		Term term = jpc.toTerm(new StaticClass(B.class));
		Term fieldTerm = new Atom("m");
		Term fieldResolutionTerm = new Compound(FieldResolutionConverter.FIELD_RESOLUTION_OPERATOR, asList(term, fieldTerm));
		assertEquals(new Long(10L), jpc.fromTerm(fieldResolutionTerm));
		Term mutatorTerm;
		mutatorTerm = jpc.toTerm(new AbstractMap.SimpleEntry<String, Long>("m", 11L));
		jpc.fromTerm(new Compound(FieldResolutionConverter.FIELD_RESOLUTION_OPERATOR, asList(term, mutatorTerm)));
		assertEquals(11L, B.m);
		mutatorTerm = jpc.toTerm(new HashMap<String, Long>(){{put("m", 12L);}});
		jpc.fromTerm(new Compound(FieldResolutionConverter.FIELD_RESOLUTION_OPERATOR, asList(term, mutatorTerm)));
		assertEquals(12L, B.m);
	}

	@Test
	public void testInstanceField() {
		Object object = new B();
		Term term = jRef(object);
		Term fieldTerm = new Atom("n");
		Term fieldResolutionTerm = new Compound(FieldResolutionConverter.FIELD_RESOLUTION_OPERATOR, asList(term, fieldTerm));
		assertEquals(new Long(10L), jpc.fromTerm(fieldResolutionTerm));
		Term mutatorTerm;
		mutatorTerm = jpc.toTerm(new AbstractMap.SimpleEntry<String, Long>("m", 11L));
		jpc.fromTerm(new Compound(FieldResolutionConverter.FIELD_RESOLUTION_OPERATOR, asList(term, mutatorTerm)));
		assertEquals(11L, B.m);
		mutatorTerm = jpc.toTerm(new HashMap<String, Long>(){{put("m", 12L);}});
		jpc.fromTerm(new Compound(FieldResolutionConverter.FIELD_RESOLUTION_OPERATOR, asList(term, mutatorTerm)));
		assertEquals(12L, B.m);
	}
	
}
