package org.jpc.engine.prolog;

import static java.util.Arrays.asList;
import static org.jpc.engine.prolog.PrologConstants.ABOLISH;
import static org.jpc.engine.prolog.PrologConstants.ASSERTA;
import static org.jpc.engine.prolog.PrologConstants.ASSERTZ;
import static org.jpc.engine.prolog.PrologConstants.ATOM_CHARS;
import static org.jpc.engine.prolog.PrologConstants.BAGOF;
import static org.jpc.engine.prolog.PrologConstants.CD;
import static org.jpc.engine.prolog.PrologConstants.CLAUSE;
import static org.jpc.engine.prolog.PrologConstants.CURRENT_OP;
import static org.jpc.engine.prolog.PrologConstants.CURRENT_PROLOG_FLAG;
import static org.jpc.engine.prolog.PrologConstants.ENSURE_LOADED;
import static org.jpc.engine.prolog.PrologConstants.FINDALL;
import static org.jpc.engine.prolog.PrologConstants.FLUSH_OUTPUT;
import static org.jpc.engine.prolog.PrologConstants.FORALL;
import static org.jpc.engine.prolog.PrologConstants.RETRACT;
import static org.jpc.engine.prolog.PrologConstants.RETRACT_ALL;
import static org.jpc.engine.prolog.PrologConstants.SETOF;
import static org.jpc.engine.prolog.PrologConstants.SET_PROLOG_FLAG;
import static org.jpc.term.ListTerm.listTerm;
import static org.jpc.term.Variable.ANONYMOUS_VAR;
import static org.jpc.util.LogicUtil.isResourceAlias;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jpc.exception.ExceptionHandledQuery;
import org.jpc.exception.ExceptionHandler;
import org.jpc.exception.ExceptionHandlerManager;
import org.jpc.exception.RootExceptionHandlerManager;
import org.jpc.query.Query;
import org.jpc.query.QuerySolutionToTermFunction;
import org.jpc.term.AbstractTerm;
import org.jpc.term.Atom;
import org.jpc.term.Compound;
import org.jpc.term.Term;
import org.jpc.term.TermConvertable;
import org.jpc.term.Variable;
import org.jpc.util.LogicUtil;

public abstract class AbstractPrologEngine implements PrologEngine {

	private ExceptionHandlerManager exceptionHandlerManager;
	
	public AbstractPrologEngine() {
		this.exceptionHandlerManager = new RootExceptionHandlerManager();
	}
	
	
//	public LogtalkEngine asLogtalkEngine() {
//		return new LogtalkEngine(this);
//	}
	
	public void registerExceptionHandler(ExceptionHandler exceptionHandler) {
		exceptionHandlerManager.register(exceptionHandler);
	}
	
	/* ********************************************************************************************************************************
	 * CORE METHODS (and overloaded variations of those methods)
     **********************************************************************************************************************************
     */

	public abstract boolean stop();

	/**
	 * escape the given string adding quotes and escaping characters if needed
	 * @param s the string to escape
	 * @return the escaped string
	 */
	public abstract String escape(String s);
	
	protected abstract Query createQuery(TermConvertable termConvertable);
	
	public final Query query(String termString) {
		return query(asTerm(termString));
	}

	public final Query query(TermConvertable... termConvertables) {
		return query(Arrays.asList(termConvertables));
	}
	
	public final Query query(List<? extends TermConvertable> termConvertables) {
		Term termSequence = LogicUtil.termsToSequence(termConvertables);
		return new ExceptionHandledQuery(createQuery(termSequence), exceptionHandlerManager);
	}
	
	/**
	 * 
	 * @param termString
	 * @return the term representation of a String. Variable names should be preserved.
	 */
	public Term asTerm(String termString) {
		return asTerm(termString, false);
	}
	
	public Term asTerm(String termString, boolean force) {
		try {
			return asTerm(termString);
		} catch(Exception e) {
			if(force)
				return new Atom(termString);
			else
				throw e;
		}
	}
	
	public List<Term> asTerms(List<String> termsString) {
		return asTerms(termsString, false);
	}
	
	public List<Term> asTerms(List<String> termsString, boolean force) {
		List<Term> terms = new ArrayList<>();
		for(String s : termsString)
			terms.add(asTerm(s, force));
		return terms;
	}
	
	
	/* ********************************************************************************************************************************
	 * FLAGS
     **********************************************************************************************************************************
     */
	
	public boolean setPrologFlag(TermConvertable flag, TermConvertable flagValue) {
		return query(new Compound(SET_PROLOG_FLAG, asList(flag, flagValue))).hasSolution();
	}
	
	public boolean setPrologFlag(TermConvertable flag, String flagValue) {
		return setPrologFlag(flag, new Atom(flagValue));
	}
	
	public Query currentPrologFlag(TermConvertable flag, TermConvertable flagValue) {
		return query(new Compound(CURRENT_PROLOG_FLAG, asList(flag, flagValue)));
	}
	
	public String currentPrologFlag(TermConvertable flag) {
		String flagValue = null;
		Variable varFlagValue = new Variable("Var");
		Map<String, Term> solutions = currentPrologFlag(flag, varFlagValue).oneSolution();
		if(solutions!=null) {
			Atom flagValueTerm = (Atom) solutions.get(varFlagValue.name());
			flagValue = flagValueTerm.getName();
		}
		return flagValue;
	}
	
	public String prologDialect() {
		return currentPrologFlag(PrologFlag.DIALECT);
	}
	
	
	/* ********************************************************************************************************************************
	 * OPERATORS
     **********************************************************************************************************************************
     */
	
	public Query currentOp(TermConvertable priority, TermConvertable specifier, TermConvertable operator) {
		return query(new Compound(CURRENT_OP, asList(priority, specifier, operator)));
	}

	public boolean isBinaryOperator(String op) {
		return query(new Compound(CURRENT_OP, asList(ANONYMOUS_VAR, new Variable("Type"), new Atom(op))), new Compound(ATOM_CHARS, asList(new Variable("Type"), listTerm(ANONYMOUS_VAR, new Atom("f"), ANONYMOUS_VAR)))).hasSolution();
		//return createQuery("current_op(_, Type, '" + op + "'), atom_chars(Type, [_, f, _])").hasSolution();
	}
	
	public boolean isUnaryOperator(String op) {
		return query(new Compound(CURRENT_OP, asList(ANONYMOUS_VAR, new Variable("Type"), new Atom(op))), new Compound(ATOM_CHARS, asList(new Variable("Type"), listTerm(new Atom("f"), ANONYMOUS_VAR)))).hasSolution();
		//return createQuery("current_op(_, Type, '" + op + "'), atom_chars(Type, [f, _])").hasSolution();
	}
	
	
	/* ********************************************************************************************************************************
	 * FILE SYSTEM
     **********************************************************************************************************************************
     */
	
	public boolean cd(TermConvertable path) {
		Compound compound = new Compound(CD, asList(path));
		return query(compound).hasSolution();
	}
	
	public boolean cd(String path) {
		return cd(new Atom(path));
	}
	
	
	/* ********************************************************************************************************************************
	 * DATABASE PREDICATES
     **********************************************************************************************************************************
     */
	
	/**
	 * Assert a clause in the logic database. Term is asserted as the first fact or rule of the corresponding predicate.
	 * @param terms the terms to assert
	 * @return
	 */
	@Override
	public boolean asserta(TermConvertable termConvertable) {
		return query(new Compound(ASSERTA, asList(termConvertable))).hasSolution();
	}
	
	/**
	 * Assert a clause in the logic database. Term is asserted as the last fact or rule of the corresponding predicate.
	 * @param terms the terms to assert
	 * @return
	 */
	@Override
	public boolean assertz(TermConvertable termConvertable) {
		return query(new Compound(ASSERTZ, asList(termConvertable))).hasSolution();
	}

	@Override
	public Query retract(TermConvertable termConvertable)  {
		return query(new Compound(RETRACT, asList(termConvertable)));
	}
	
	@Override
	public boolean retractAll(TermConvertable termConvertable)  {
		return query(new Compound(RETRACT_ALL, asList(termConvertable))).hasSolution();
	}

	@Override
	public boolean abolish(TermConvertable termConvertable)  {
		return query(new Compound(ABOLISH, asList(termConvertable))).hasSolution();
	}
	
	public Query clause(TermConvertable head, TermConvertable body)  {
		return query(new Compound(CLAUSE, asList(head, body)));
	}
	/**
	 * Assert a list of clauses in the logic database. Terms are asserted as the first facts or rules of the corresponding predicate.
	 * @param terms the terms to assert
	 * @return
	 */
	public boolean asserta(List<? extends TermConvertable> termConvertables) {
		return allSucceed(LogicUtil.forEachApplyFunctor(ASSERTA, termConvertables));
	}
	
	/**
	 * Assert a list of clauses in the logic database. Term are asserted as the last facts or rules of the corresponding predicate.
	 * @param terms the terms to assert
	 * @return
	 */
	public boolean assertz(List<? extends TermConvertable> termConvertables) {
		return allSucceed(LogicUtil.forEachApplyFunctor(ASSERTZ, termConvertables));
	}	
	
	
	/* ********************************************************************************************************************************
	 * FILE LOADER PREDICATES
     **********************************************************************************************************************************
     */
	
	public boolean ensureLoaded(List<? extends TermConvertable> termConvertables) {
		return query(new Compound(ENSURE_LOADED, asList(listTerm(termConvertables)))).hasSolution();
	}
	
	public boolean ensureLoaded(TermConvertable... termConvertables) {
		return ensureLoaded(asList(termConvertables));
	}

	public boolean ensureLoaded(String... resources) {
		return ensureLoaded(asResourceTerms(asList(resources)));
	}
	

	/* ********************************************************************************************************************************
	 * HIGH ORDER PREDICATES
     **********************************************************************************************************************************
     */
	public Query bagof(TermConvertable select, TermConvertable exp, TermConvertable all) {
		return query(new Compound(BAGOF, asList(select, exp, all)));
	}
	
	public Query findall(TermConvertable select, TermConvertable exp, TermConvertable all) {
		return query(new Compound(FINDALL, asList(select, exp, all)));
	}
	
	public Query setof(TermConvertable select, TermConvertable exp, TermConvertable all) {
		return query(new Compound(SETOF, asList(select, exp, all)));
	}
	
	public Query forall(TermConvertable generator, TermConvertable test) {
		return query(new Compound(FORALL, asList(generator, test)));
	}


	/* ********************************************************************************************************************************
	 * OTHER PREDICATES
     **********************************************************************************************************************************
     */
	
	public boolean flushOutput() {
		return query(new Atom(FLUSH_OUTPUT)).hasSolution();
	}
	
	/* ********************************************************************************************************************************
	 * UNIFICATION
     **********************************************************************************************************************************
     */
	
	public Term unify(TermConvertable... terms) {
		return unify(asList(terms));
	}
	
	public Term unify(List<? extends TermConvertable> terms) {
		if(terms.isEmpty())
			throw new RuntimeException("The list of terms to unify cannot be empty");
		if(terms.size() == 1)
			return terms.get(0).asTerm();
		List<Term> unifications = new ArrayList<>();
		for(int i=0; i<terms.size()-1; i++) {
			unifications.add(new Compound("=", asList(terms.get(i), terms.get(i+1))));
		}
		List<Map<String, Term>> solutions = query(unifications).allSolutions();
		if(solutions.isEmpty())
			return null;
		Map<String, Term> solution = solutions.get(0);
		return new QuerySolutionToTermFunction(terms.get(0)).apply(solution);
	}


	
	/* ********************************************************************************************************************************
	 * UTILITY METHODS
     **********************************************************************************************************************************
     */
	
	
	public boolean allSucceed(List<? extends TermConvertable> termConvertables) {
		boolean success = true;
		for(TermConvertable termConvertable: termConvertables) {
			if(!query(termConvertable).hasSolution())
				success = false;
		}
		return success;
	}
	
	public List<Term> asResourceTerms(List<String> resourceNames) {
		List<Term> terms = new ArrayList<>();
		for(String s : resourceNames)
			terms.add(asResourceTerm(s));
		return terms;
	}
	
	public Term asResourceTerm(String resourceName) {
		if(isResourceAlias(resourceName)) //it is a resource alias of the form library(lib_name)
			return asTerm(resourceName);
		else
			return new Atom(resourceName);
	}
	
	public String termSequenceToString(TermConvertable sequenceTermConvertable) {
		List<Term> terms = LogicUtil.sequenceAsTerms(sequenceTermConvertable);
		return AbstractTerm.toString(this, terms);
	}
}
