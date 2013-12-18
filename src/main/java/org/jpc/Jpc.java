package org.jpc;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.util.List;

import org.jconverter.JConverter;
import org.jconverter.converter.ConverterManager;
import org.jconverter.instantiation.InstantiationManager;
import org.jpc.converter.typesolver.TypeSolverManager;
import org.jpc.term.Compound;
import org.jpc.term.ListTerm;
import org.jpc.term.Term;
import org.jpc.term.jterm.JTermManager;
import org.minitoolbox.commons.Version;

/**
 * A class providing an interface for the main JPC functionality (such as converting between terms and Java objects)
 * @author sergioc
 *
 */
public abstract class Jpc extends JConverter {

	public static final Version version = new Version(0,0,1,"alpha");
	
	/*
	public Jpc() {
		this(new JGum());
	}


	protected Jpc(JGum categorizationContext) {
		super(categorizationContext);
	}
	*/
	
	/**
	 * @param converterManager a converter manager responsible of converting objects.
	 * @param instantiationManager an instance creator manager responsible of instantiating objects.
	 * @param typeSolverManager a type solver manager responsible of recommending types for the result of a conversion.
	 */
	public Jpc(ConverterManager converterManager, InstantiationManager instantiationManager, TypeSolverManager typeSolverManager) {
		super(converterManager, instantiationManager);
	}

	public final <T> T fromTerm(Term term) {
		return fromTerm(term, Object.class);
	}
	
	public abstract <T> T fromTerm(Term term, Type type);
	
	public final Term toTerm(Object object) {
		return toTerm(object, Term.class);
	}
	
	public abstract <T extends Term> T toTerm(Object object, Class<T> termClass);
	
	public final Compound toTerm(Object name, List<?> args) {
		return new Compound(toTerm(name), listTerm(args));
	}
	
	public final ListTerm listTerm(Object ...objects) {
		return listTerm(asList(objects));
	}
	
	public final ListTerm listTerm(List<?> objects) {
		ListTerm listTerm = new ListTerm();
		for(Object o : objects) {
			listTerm.add(toTerm(o));
		}
		return listTerm;
	}

	/**
	 * 
	 * @param object the object which conversion target type to recommend.
	 * @return the recommended type.
	 */
	public Type getType(Object object) {
		return getType(TypeSolverManager.DEFAULT_KEY, object);
	}
	
	/**
	 * 
	 * @param key constrains the type solvers that will be looked up in this operation.
	 * @param object the object which conversion target type to recommend.
	 * @return the recommended type.
	 */
	protected abstract Type getType(Object key, Object object);
	
	public abstract JTermManager getJTermManager();
	
	public abstract boolean handleError(Term errorTerm, Term goal);

}
