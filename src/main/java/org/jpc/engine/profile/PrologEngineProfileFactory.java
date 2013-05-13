package org.jpc.engine.profile;

import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.prolog.driver.PrologEngineFactory;

public interface PrologEngineProfileFactory<T extends PrologEngine> {

	public PrologEngineProfile<T> createPrologEngineProfile(PrologEngineFactory<T> prologEngineFactory);
	
}
