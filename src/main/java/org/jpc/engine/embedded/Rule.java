package org.jpc.engine.embedded;

import java.util.List;

import org.jpc.term.Term;

/**
 * A rule in the embedded JPC engine.
 * @author sergioc
 *
 */
//Work in progress...
public class Rule extends Clause {

	private final List<Term> body;
	
	public Rule(Term head, List<Term> body) {
		super(head);
		this.body = body;
	}

}