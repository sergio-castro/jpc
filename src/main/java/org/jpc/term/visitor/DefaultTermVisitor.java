package org.jpc.term.visitor;

import org.jpc.term.Atom;
import org.jpc.term.Compound;
import org.jpc.term.FloatTerm;
import org.jpc.term.IntegerTerm;
import org.jpc.term.Variable;

public class DefaultTermVisitor implements TermVisitor {

	@Override
	public void visitInteger(IntegerTerm term) {
	}

	@Override
	public void visitFloat(FloatTerm term) {
	}

	@Override
	public void visitVariable(Variable term) {
	}
	
	@Override
	public void visitAtom(Atom term) {
	}

	@Override
	public boolean visitCompound(Compound term) {
		return true;
	}


}