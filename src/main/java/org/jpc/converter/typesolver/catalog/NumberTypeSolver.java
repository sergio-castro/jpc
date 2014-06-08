package org.jpc.converter.typesolver.catalog;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.jpc.converter.typesolver.TypeSolver;
import org.jpc.term.FloatTerm;
import org.jpc.term.IntegerTerm;

public class NumberTypeSolver implements TypeSolver<Number> {

	@Override
	public Type inferType(Number number) {
		Class numberClass = number.getClass();
		if(numberClass.equals(Long.class) || numberClass.equals(Integer.class) || numberClass.equals(Short.class) || numberClass.equals(Byte.class) || 
				numberClass.equals(BigInteger.class) || numberClass.equals(AtomicInteger.class) || numberClass.equals(AtomicLong.class)) {
			return IntegerTerm.class;
		} else
			return FloatTerm.class;
	}

}
