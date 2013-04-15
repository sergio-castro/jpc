package org.jpc.engine.logtalk;

import static java.util.Arrays.asList;
import static org.jpc.engine.logtalk.LogtalkConstants.ABOLISH_CATEGORY;
import static org.jpc.engine.logtalk.LogtalkConstants.ABOLISH_EVENTS;
import static org.jpc.engine.logtalk.LogtalkConstants.ABOLISH_OBJECT;
import static org.jpc.engine.logtalk.LogtalkConstants.ABOLISH_PROTOCOL;
import static org.jpc.engine.logtalk.LogtalkConstants.CATEGORY_PROPERTY;
import static org.jpc.engine.logtalk.LogtalkConstants.COMPLEMENTS_OBJECT;
import static org.jpc.engine.logtalk.LogtalkConstants.CONFORMS_TO_PROTOCOL;
import static org.jpc.engine.logtalk.LogtalkConstants.CREATE_CATEGORY;
import static org.jpc.engine.logtalk.LogtalkConstants.CREATE_OBJECT;
import static org.jpc.engine.logtalk.LogtalkConstants.CREATE_PROTOCOL;
import static org.jpc.engine.logtalk.LogtalkConstants.CURRENT_CATEGORY;
import static org.jpc.engine.logtalk.LogtalkConstants.CURRENT_EVENT;
import static org.jpc.engine.logtalk.LogtalkConstants.CURRENT_LOGTALK_FLAG;
import static org.jpc.engine.logtalk.LogtalkConstants.CURRENT_OBJECT;
import static org.jpc.engine.logtalk.LogtalkConstants.CURRENT_PROTOCOL;
import static org.jpc.engine.logtalk.LogtalkConstants.DEFINE_EVENTS;
import static org.jpc.engine.logtalk.LogtalkConstants.EXTENDS_CATEGORY;
import static org.jpc.engine.logtalk.LogtalkConstants.EXTENDS_OBJECTS;
import static org.jpc.engine.logtalk.LogtalkConstants.EXTENDS_PROTOCOL;
import static org.jpc.engine.logtalk.LogtalkConstants.IMPLEMENTS_PROTOCOL;
import static org.jpc.engine.logtalk.LogtalkConstants.IMPORTS_CATEGORY;
import static org.jpc.engine.logtalk.LogtalkConstants.INSTANTIATES_CLASS;
import static org.jpc.engine.logtalk.LogtalkConstants.LOGTALK_LOAD;
import static org.jpc.engine.logtalk.LogtalkConstants.OBJECT_PROPERTY;
import static org.jpc.engine.logtalk.LogtalkConstants.PROTOCOL_PROPERTY;
import static org.jpc.engine.logtalk.LogtalkConstants.SET_LOGTALK_FLAG;
import static org.jpc.engine.logtalk.LogtalkConstants.SPECIALIZES_CLASS;
import static org.jpc.term.ListTerm.listTerm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jpc.Jpc;
import org.jpc.engine.prolog.Flag;
import org.jpc.engine.prolog.PrologEngine;
import org.jpc.query.Query;
import org.jpc.term.Atom;
import org.jpc.term.Compound;
import org.jpc.term.Term;
import org.jpc.term.Variable;

public class LogtalkEngine implements PrologEngine {
	
	private PrologEngine prologEngine;

	public LogtalkEngine(PrologEngine prologEngine) {
		this.prologEngine = prologEngine;
	}
	
	public boolean logtalkLoad(List<? extends Term> terms) {
		return query(new Compound(LOGTALK_LOAD, asList(listTerm(terms)))).hasSolution(); 
	}
	
	public boolean logtalkLoad(Term... terms) {
		return logtalkLoad(asList(terms));
	}
	
	public boolean logtalkLoad(String... resources) {
		return query(new Compound(LOGTALK_LOAD, asList(new Jpc().toTerm(resources)))).hasSolution(); 
	}
	
	public String currentLogtalkFlag(LogtalkFlag flag) {
		String flagValue = null;
		Variable varFlag = new Variable("Var");
		Map<String, Term> solutions = query(new Compound(CURRENT_LOGTALK_FLAG, Arrays.asList(flag.asTerm(), varFlag.asTerm()))).oneSolution();
		if(solutions!=null) {
			Atom flagValueTerm = (Atom) solutions.get(varFlag.getName());
			flagValue = flagValueTerm.getName();
		}
		return flagValue;
	}
	
	public boolean setLogtalkFlag(LogtalkFlag flag, String value) {
		return query(new Compound(SET_LOGTALK_FLAG, asList(new Atom(flag.toString()), new Atom(value)))).hasSolution();
	}
	
	public Query currentObject(Term term) {
		return query(new Compound(CURRENT_OBJECT, asList(term)));
	}
	
	public List<LogtalkObject> currentObjects() {
		List<LogtalkObject> currentObjects = new ArrayList<>();
		Variable logtalkObjectVar = new Variable("LogtalkObject");
		Compound compound = new Compound(CURRENT_OBJECT, asList(logtalkObjectVar));
		for(Map<String, Term> solution : query(compound).allSolutions()) {
			Term currentObjectTerm = solution.get(logtalkObjectVar.getName());
			currentObjects.add(new LogtalkObject(currentObjectTerm, this));
		}
		return currentObjects;
	}
	
	/**
	 * 
	 * @param objectName
	 * @return a list of arities of all the Logtalk objects in the logic side having as name the parameter of the Java method
	 */
	//currently assuming that the cardinalities of the objects in the logtalk side are returned ordered from the lowest to the highest
	public List<Integer> objectArities(String objectName) {
		List<LogtalkObject> currentObjects = currentObjects();
		List<Integer> arities = new ArrayList<>();
		for(LogtalkObject currentObject: currentObjects) {
			Term name = currentObject.name();
			if(name instanceof Atom && ((Atom)name).getName().equals(objectName)) {
				arities.add(currentObject.arity());
			}
		}
		return arities;
	}

	public Query createObject(Term object, Term relations, Term directives, Term Clauses) {
		return query(new Compound(CREATE_OBJECT, asList(object, relations, directives, Clauses)));
	}
	
	public Query abolishObject(Term object) {
		return query(new Compound(ABOLISH_OBJECT, asList(object)));
	}
	
	public Query instantiatesClass(Term instance, Term clazz) {
		return query(new Compound(INSTANTIATES_CLASS, asList(instance, clazz)));
	}
	
	public Query instantiatesClass(Term instance, Term clazz, Term scope) {
		return query(new Compound(INSTANTIATES_CLASS, asList(instance, clazz, scope)));
	}
	
	public Query specializesClass(Term clazz, Term superClass) {
		return query(new Compound(SPECIALIZES_CLASS, asList(clazz, superClass)));
	}
	
	public Query specializesClass(Term clazz, Term superClass, Term scope) {
		return query(new Compound(SPECIALIZES_CLASS, asList(clazz, superClass, scope)));
	}
	
	public Query extendsObject(Term child, Term parent) {
		return query(new Compound(EXTENDS_OBJECTS, asList(child, parent)));
	}
	
	public Query extendsObject(Term child, Term parent, Term scope) {
		return query(new Compound(EXTENDS_OBJECTS, asList(child, parent, scope)));
	}
	
	public Query importsCategory(Term object, Term category) {
		return query(new Compound(IMPORTS_CATEGORY, asList(object, category)));
	}
	
	public Query importsCategory(Term object, Term category, Term scope) {
		return query(new Compound(IMPORTS_CATEGORY, asList(object, category, scope)));
	}
	
	public Query implementsProtocol(Term object, Term protocol) {
		return query(new Compound(IMPLEMENTS_PROTOCOL, asList(object, protocol)));
	}
	
	public Query implementsProtocol(Term object, Term protocol, Term scope) {
		return query(new Compound(IMPLEMENTS_PROTOCOL, asList(object, protocol, scope)));
	}
	
	public Query conformsToProtocol(Term object, Term protocol) {
		return query(new Compound(CONFORMS_TO_PROTOCOL, asList(object, protocol)));
	}
	
	public Query conformsToProtocol(Term object, Term protocol, Term scope) {
		return query(new Compound(CONFORMS_TO_PROTOCOL, asList(object, protocol, scope)));
	}
	
	public Query complementsObject(Term category, Term object) {
		return query(new Compound(COMPLEMENTS_OBJECT, asList(category, object)));
	}
	
	public Query objectProperty(Term object, Term property) {
		return query(new Compound(OBJECT_PROPERTY, asList(object, property)));
	}
	
	
	public Query currentProtocol(Term protocol) {
		return query(new Compound(CURRENT_PROTOCOL, asList(protocol)));
	}
	
	public Query createProtocol(Term protocol, Term relations, Term directives) {
		return query(new Compound(CREATE_PROTOCOL, asList(protocol, relations, directives)));
	}
	
	public Query abolishProtocol(Term protocol) {
		return query(new Compound(ABOLISH_PROTOCOL, asList(protocol)));
	}
	
	public Query extendsProtocol(Term child, Term parent) {
		return query(new Compound(EXTENDS_PROTOCOL, asList(child, parent)));
	}
	
	public Query extendsProtocol(Term child, Term parent, Term scope) {
		return query(new Compound(EXTENDS_PROTOCOL, asList(child, parent, scope)));
	}
	
	public Query protocolProperty(Term protocol, Term property) {
		return query(new Compound(PROTOCOL_PROPERTY, asList(protocol, property)));
	}
	
	
	public Query currentCategory(Term category) {
		return query(new Compound(CURRENT_CATEGORY, asList(category)));
	}
	
	public Query createCategory(Term category, Term relations, Term directives, Term Clauses) {
		return query(new Compound(CREATE_CATEGORY, asList(category, relations, directives, Clauses)));
	}
	
	public Query abolishCategory(Term category) {
		return query(new Compound(ABOLISH_CATEGORY, asList(category)));
	}

	public Query extendsCategory(Term child, Term parent) {
		return query(new Compound(EXTENDS_CATEGORY, asList(child, parent)));
	}
	
	public Query extendsCategory(Term child, Term parent, Term scope) {
		return query(new Compound(EXTENDS_CATEGORY, asList(child, parent, scope)));
	}
	
	public Query categoryProperty(Term category, Term property) {
		return query(new Compound(CATEGORY_PROPERTY, asList(category, property)));
	}
	
	public Query currentEvent(Term event, Term object, Term message, Term sender, Term monitor) {
		return query(new Compound(CURRENT_EVENT, asList(event, object, message, sender, monitor)));
	}
	
	public Query defineEvents(Term event, Term object, Term message, Term sender, Term monitor) {
		return query(new Compound(DEFINE_EVENTS, asList(event, object, message, sender, monitor)));
	}
	
	public Query abolishEvents(Term event, Term object, Term message, Term sender, Term monitor) {
		return query(new Compound(ABOLISH_EVENTS, asList(event, object, message, sender, monitor)));
	}
	
	
	/* ********************************************************************************************************************************
	 * PROXY METHODS IMPLEMENTED IN LogtalkEngine
     **********************************************************************************************************************************
     */

//	public LogtalkEngine asLogtalkEngine() {
//		return this;
//	}

	@Override
	public String getName() {
		return prologEngine.getName();
	}
	
	@Override
	public void setName(String name) {
		prologEngine.setName(name);
	}
	
	@Override
	public boolean interrupt() {
		return prologEngine.interrupt();
	}
	
	@Override
	public boolean close() {
		return prologEngine.close();
	}

	@Override
	public boolean isCloseable() {
		return prologEngine.isCloseable();
	}
	
	@Override
	public boolean command(String termString) {
		return prologEngine.command(termString);
	}

	@Override
	public boolean command(String termString, boolean errorHandledQuery) {
		return prologEngine.command(termString, errorHandledQuery);
	}
	
	@Override
	public boolean command(String termString, Jpc context) {
		return prologEngine.command(termString, context);
	}
	
	@Override
	public Query query(String termString) {
		return prologEngine.query(termString);
	}

	@Override
	public Query query(Term term) {
		return prologEngine.query(term);
	}
	
	@Override
	public Query query(String termString, boolean errorHandledQuery) {
		return prologEngine.query(termString, errorHandledQuery);
	}
	
	@Override
	public Query query(Term term, boolean errorHandledQuery) {
		return prologEngine.query(term, errorHandledQuery);
	}
	
	@Override
	public Query query(String termString, Jpc context) {
		return prologEngine.query(termString, context);
	}

	@Override
	public Query query(Term term, Jpc context) {
		return prologEngine.query(term, context);
	}
	
	@Override
	public Query query(String termString, boolean errorHandledQuery, Jpc context) {
		return prologEngine.query(termString, errorHandledQuery, context);
	}
	
	@Override
	public Query query(Term term, boolean errorHandledQuery, Jpc context) {
		return prologEngine.query(term, errorHandledQuery, context);
	}
	
	@Override
	public Term asTerm(String termString) {
		return prologEngine.asTerm(termString);
	}

//	public Term asTerm(String termString, boolean force) {
//		return prologEngine.asTerm(termString, force);
//	}

	@Override
	public List<Term> asTerms(List<String> termsString) {
		return prologEngine.asTerms(termsString);
	}

//	public List<Term> asTerms(List<String> termsString, boolean force) {
//		return prologEngine.asTerms(termsString, force);
//	}

	@Override
	public boolean setPrologFlag(Term flag, Term flagValue) {
		return prologEngine.setPrologFlag(flag, flagValue);
	}

	@Override
	public boolean setPrologFlag(Flag flag, String flagValue) {
		return prologEngine.setPrologFlag(flag, flagValue);
	}

	@Override
	public Query currentPrologFlag(Term flag, Term flagValue) {
		return prologEngine.currentPrologFlag(flag, flagValue);
	}

	@Override
	public Query currentPrologFlag(Flag flag, String flagValue) {
		return prologEngine.currentPrologFlag(flag, flagValue);
	}
	
	@Override
	public String currentPrologFlag(Flag flag) {
		return prologEngine.currentPrologFlag(flag);
	}

	@Override
	public String prologDialect() {
		return prologEngine.prologDialect();
	}

	@Override
	public Query currentOp(Term priority, Term specifier, Term operator) {
		return prologEngine.currentOp(priority, specifier, operator);
	}

	@Override
	public boolean isBinaryOperator(String op) {
		return prologEngine.isBinaryOperator(op);
	}

	@Override
	public boolean isUnaryOperator(String op) {
		return prologEngine.isUnaryOperator(op);
	}

	@Override
	public boolean cd(Term path) {
		return prologEngine.cd(path);
	}

	@Override
	public boolean cd(String path) {
		return prologEngine.cd(path);
	}

	@Override
	public boolean asserta(Term term) {
		return prologEngine.asserta(term);
	}

	@Override
	public boolean assertz(Term term) {
		return prologEngine.assertz(term);
	}

	@Override
	public Query retract(Term term) {
		return prologEngine.retract(term);
	}

	@Override
	public boolean retractAll(Term term) {
		return prologEngine.retractAll(term);
	}

	@Override
	public boolean abolish(Term term) {
		return prologEngine.abolish(term);
	}

	@Override
	public Query clause(Term head, Term body) {
		return prologEngine.clause(head, body);
	}

	@Override
	public boolean asserta(List<? extends Term> terms) {
		return prologEngine.asserta(terms);
	}

	@Override
	public boolean assertz(List<? extends Term> terms) {
		return prologEngine.assertz(terms);
	}

	@Override
	public boolean ensureLoaded(List<? extends Term> terms) {
		return prologEngine.ensureLoaded(terms);
	}

	@Override
	public boolean ensureLoaded(Term... terms) {
		return prologEngine.ensureLoaded(terms);
	}

	@Override
	public boolean ensureLoaded(String... resources) {
		return prologEngine.ensureLoaded(resources);
	}

	@Override
	public Query bagof(Term select, Term exp, Term all) {
		return prologEngine.bagof(select, exp, all);
	}

	@Override
	public Term bagof(Term select, Term exp) {
		return prologEngine.bagof(select, exp);
	}
	
	@Override
	public Query findall(Term select, Term exp, Term all) {
		return prologEngine.findall(select, exp, all);
	}

	@Override
	public Term findall(Term select, Term exp) {
		return prologEngine.findall(select, exp);
	}
	
	@Override
	public Query setof(Term select, Term exp, Term all) {
		return prologEngine.setof(select, exp, all);
	}

	@Override
	public Term setof(Term select, Term exp) {
		return prologEngine.setof(select, exp);
	}
	
	@Override
	public Query forall(Term generator, Term test) {
		return prologEngine.forall(generator, test);
	}

	@Override
	public boolean flushOutput() {
		return prologEngine.flushOutput();
	}

	@Override
	public Term unify(Term... terms) {
		return prologEngine.unify(terms);
	}
	
	@Override
	public Term unify(List<? extends Term> terms) {
		return prologEngine.unify(terms);
	}

	@Override
	public boolean allSucceed(List<? extends Term> Terms) {
		return prologEngine.allSucceed(Terms);
	}

}
