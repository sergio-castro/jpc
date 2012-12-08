package org.jpc.engine.logtalk;

import org.jpc.engine.prolog.PrologEngine;
import org.jpc.term.Atom;

/**
 * The built-in pseudo-object 'user'
 * @author sergioc
 *
 */
public class User extends LogtalkObject {

	public User(PrologEngine logicEngine) {
		super(logicEngine, new Atom(LogtalkConstants.USER_LOGTALK_OBJECT));
	}

}