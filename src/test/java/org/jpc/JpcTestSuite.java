package org.jpc;

import org.jpc.engine.visitor.TermManipulationTest;
import org.jpc.term.AtomTest;
import org.jpc.term.CompoundTest;
import org.jpc.term.FloatTermTest;
import org.jpc.term.IntegerTermTest;
import org.jpc.term.ListTermTest;
import org.jpc.term.VariableTest;
import org.jpc.util.DefaultConverterTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	VariableTest.class,
	IntegerTermTest.class,
	FloatTermTest.class,
	AtomTest.class,
	CompoundTest.class,
	ListTermTest.class,
	DefaultConverterTest.class,
	TermManipulationTest.class
	})
public class JpcTestSuite {}
