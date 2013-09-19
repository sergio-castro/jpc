package org.jpc.term;

import org.jpc.salt.TermContentHandler;
import org.jpc.term.expansion.TermExpander;
import org.jpc.term.visitor.TermVisitor;


/**
 * A class reifying a logic float term
 * @author scastro
 *
 */
public final class FloatTerm extends NumberTerm {

	/**
	 * @param   value  This FloatTerm's (double) value
	 */
	public FloatTerm(double value) {
		super(value);
	}

	@Override
	public void accept(TermVisitor termVisitor) {
		termVisitor.visitFloat(this);
	}

	@Override
	protected void basicRead(TermContentHandler contentHandler, TermExpander termExpander) {
		contentHandler.startFloatTerm((Double)value);
	}


}
