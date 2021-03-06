package org.jpc.mapping.converter.catalog.error;

import static java.util.Arrays.asList;
import static org.jpc.engine.prolog.PrologConstants.PROLOG_ERROR_FUNCTOR_NAME;

import org.jconverter.converter.TypeDomain;
import org.jpc.Jpc;
import org.jpc.mapping.converter.ToTermConverter;
import org.jpc.term.Compound;
import org.jpc.term.Term;

public class ThrowableConverter implements ToTermConverter<Throwable, Compound> {

	public static final String JEXCEPTION_FUNCTOR_NAME = "jexception";
	
	@Override
	public Compound toTerm(Throwable ex, TypeDomain target, Jpc jpc) {
		Term throwableClassTerm = jpc.toTerm(ex.getClass());
		Term messageTerm = jpc.toTerm(ex.getMessage());
		Term causeTerm = jpc.toTerm(ex.getCause());
		Term stackTraceTerm = jpc.toTerm(ex.getStackTrace());
		Term formalDescriptiomTerm = new Compound(JEXCEPTION_FUNCTOR_NAME, asList(throwableClassTerm, messageTerm, causeTerm));
		return new Compound(PROLOG_ERROR_FUNCTOR_NAME, asList(formalDescriptiomTerm, stackTraceTerm));
	}

}
