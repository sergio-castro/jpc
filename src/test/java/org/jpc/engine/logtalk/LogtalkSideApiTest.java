package org.jpc.engine.logtalk;

import static org.jpc.engine.prolog.PrologEngines.defaultPrologEngine;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.jpc.engine.prolog.PrologEngine;
import org.jpc.term.Atom;
import org.jpc.term.IntegerTerm;
import org.jpc.term.Term;
import org.junit.Test;

public class LogtalkSideApiTest {

	@Test
	public void testEval() {
		Term term = defaultPrologEngine().query("java::eval(abc::toUpperCase,term(V))").oneSolutionOrThrow().get("V");
		assertEquals(new Atom("ABC"), term);
	}
	
	@Test
	public void testReturningTerm() {
		Term term;
		term = defaultPrologEngine().query("java([a:1,b:2], term(X))::invoke(get('a'))").oneSolutionOrThrow().get("X");
		assertEquals(new IntegerTerm(1), term);
		term = defaultPrologEngine().query("java([a:1,b:2], term(X))::get('a')").oneSolutionOrThrow().get("X");
		assertEquals(new IntegerTerm(1), term);
		term = defaultPrologEngine().query("[a:1,b:2]::get('a') returns term(X)").oneSolutionOrThrow().get("X");
		assertEquals(new IntegerTerm(1), term);
	}

	public static class Fixture {
		public static String x;
		public static String y;
	}
	
	@Test
	public void setStaticField() {
		defaultPrologEngine().query("java::eval(class([org,jpc,engine,logtalk],['LogtalkSideApiTest','Fixture'])@([x:hello,y:bye]))").oneSolutionOrThrow();
		assertEquals("hello", Fixture.x);
		assertEquals("bye", Fixture.y);
	}
	
	@Test
	public void testCurrentEngine() {
		PrologEngine prologEngine = defaultPrologEngine().query("prolog_engines::this_engine(E)").<PrologEngine>selectObject("E").oneSolutionOrThrow();
		assertNotNull(prologEngine);
	}
	
}
