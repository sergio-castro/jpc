package org.jpc.engine.prolog;

import static java.util.Arrays.asList;
import static org.jpc.engine.prolog.PrologEngines.defaultPrologEngine;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.jpc.query.Solution;
import org.jpc.term.Atom;
import org.jpc.term.Compound;
import org.jpc.term.Float;
import org.jpc.term.Integer;
import org.jpc.term.Term;
import org.jpc.term.Var;
import org.junit.Test;

/**
 * This class tests the minimum required Prolog functionality
 * (to be completed)
 * @author sergioc
 *
 */
public class PrologEngineTest {

	@Test
	public void testDataTypes() {
		Solution querySolution = defaultPrologEngine().query("I=1, F=1.0, A=true, C=x(x), L=[1], EL=[], V=Var").oneSolutionOrThrow();
		assertEquals(new Integer(1), querySolution.get("I"));
		assertEquals(new Float(1.0), querySolution.get("F"));
		assertEquals(new Atom("true"), querySolution.get("A"));
		assertEquals(new Compound("x", asList(new Atom("x"))), querySolution.get("C"));
		assertEquals(new Compound(".", asList(new Integer(1), new Atom("[]"))), querySolution.get("L"));
		assertEquals(new Atom("[]"), querySolution.get("EL"));
		assertTrue(querySolution.get("V") instanceof Var);
	}
	
	@Test
	public void testVariables() {
		Term t = defaultPrologEngine().asTerm("x(Var)");
		assertTrue(t.hasVariable("Var"));
	}
	
	@Test
	public void testList() {
		Term t1 = defaultPrologEngine().asTerm(".(1,(.(2,[])))");
		Term t2 = defaultPrologEngine().asTerm("[1,2]");
		assertEquals(t1,t2);
	}
	
	@Test
	public void testSequence() {
		Term t1 = defaultPrologEngine().asTerm("','(a,(','(b,c)))");
		Term t2 = defaultPrologEngine().asTerm("(a,b,c)");
		assertEquals(t1,t2);
	}

	@Test
	public void testOperatorsContext() {
		assertFalse(defaultPrologEngine().getOperatorsContext().getOperators("+").isEmpty());
		OperatorsContext oc = defaultPrologEngine().query("true").oneSolutionOrThrow().getOperatorsContext();
		assertNotNull(oc);
		Collection<Operator> plusOp;
		plusOp = oc.getOperators("+");
		assertNotNull(plusOp);
		assertFalse(plusOp.isEmpty());
		
		oc = defaultPrologEngine().query("true").allSolutions().get(0).getOperatorsContext();
		assertNotNull(oc);
	}
	
}
