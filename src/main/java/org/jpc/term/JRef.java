package org.jpc.term;

import org.jpc.engine.prolog.OperatorsContext;
import org.jpc.salt.TermContentHandler;
import org.jpc.term.compiled.CompilationContext;
import org.jpc.term.visitor.TermVisitor;
import org.jpc.util.PrologUtil;

import com.google.common.base.Function;

public final class JRef extends Term {

	private final Object ref;
	
	public JRef(Object ref) {
		this.ref = ref;
	}
	
	public Object getRef() {
		return ref;
	}
	
	@Override
	public boolean hasFunctor(Functor functor) {
		return functor.getArity() == 0 && termEquals(functor.getName());
	}

	@Override
	public boolean isGround() {
		return true;
	}
	
	@Override
	public void accept(TermVisitor termVisitor) {
		termVisitor.visitJRef(this);
	}

	@Override
	protected void basicRead(TermContentHandler contentHandler, Function<Term, Term> termExpander) {
		contentHandler.startJRef(ref);
	}

	@Override
	public String toEscapedString() {
		return PrologUtil.escapeString(ref.toString());
	}

	@Override
	public String toString(OperatorsContext o) {
		return toString();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + ref.toString() + ")";
	}

	@Override
	public boolean equals(Object term) {
		return (this == term || 
				(term.getClass().equals(getClass()) && ref == ((JRef)term).ref));
	}
	
	@Override
	public int hashCode() {
		return ref.hashCode();
	}
	
	public void doUnification(Term term) {
		if(term instanceof AbstractVar)
			term.doUnification(this);
		else {
			if(!equals(term))
				throw new NonUnifiableException(this, term); //TODO implement open-unification
		}
	}
	
	@Override
	public Term compile(int clauseId, CompilationContext context) {
		return this;
	}

	@Override
	public Term prepareForQuery(CompilationContext context) {
		return this;
	}

	@Override
	public Term prepareForFrame(CompilationContext context) {
		return this;
	}
	
}
